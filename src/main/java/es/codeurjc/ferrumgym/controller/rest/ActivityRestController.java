package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.ActivityDTO;
import es.codeurjc.ferrumgym.dto.ActivityMapper;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityRestController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityMapper activityMapper;

    @Operation(summary = "Get all activities paginated")
    @GetMapping
    public ResponseEntity<Page<ActivityDTO>> getAllActivities(@PageableDefault(size = 10) Pageable pageable) {
        Page<Activity> activities = activityService.findAll(pageable);
        return ResponseEntity.ok(activities.map(activityMapper::toDTO));
    }

    @Operation(summary = "Get an activity by its id")
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        return activityService.findById(id)
                .map(activity -> ResponseEntity.ok(activityMapper.toDTO(activity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new activity (Text data only)")
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody ActivityDTO activityDto) {
        Activity activity = activityMapper.toEntity(activityDto);
        Activity savedActivity = activityService.save(activity);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedActivity.getId())
                .toUri();

        return ResponseEntity.created(location).body(activityMapper.toDTO(savedActivity));
    }

    @Operation(summary = "Update an existing activity (Text data only)")
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO activityDto) {
        Activity updatedDetails = activityMapper.toEntity(activityDto);
        Activity savedActivity = activityService.update(id, updatedDetails);
        return ResponseEntity.ok(activityMapper.toDTO(savedActivity));
    }

    @Operation(summary = "Update the image of an activity")
    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updateActivityImage(@PathVariable Long id, @RequestParam MultipartFile imageFile)
            throws IOException {
        Activity activity = activityService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        activityService.saveImage(activity, imageFile);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update the information PDF of an activity")
    @PutMapping("/{id}/pdf")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<Void> updateActivityPdf(@PathVariable Long id, @RequestParam MultipartFile pdfFile)
            throws IOException {

        Activity activity = activityService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        if (pdfFile.isEmpty() || pdfFile.getContentType() == null || !pdfFile.getContentType().equals("application/pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be a valid PDF");
        }

        String originalName = pdfFile.getOriginalFilename();
        if (originalName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filename");
        }

        String baseName = Paths.get(originalName).getFileName().toString();
        String safeFilename = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");

        Path uploadPath = Paths.get("uploads/docs/").toAbsolutePath();
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(safeFilename).normalize();

        if (!filePath.startsWith(uploadPath)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Path Traversal attempt detected");
        }

        Files.copy(pdfFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        activity.setPdfFilename(safeFilename);
        activityService.save(activity); 

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete an activity")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get the image of an activity")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getActivityImage(@PathVariable Long id) {
        return activityService.findById(id)
                .filter(activity -> activity.getImage() != null)
                .map(activity -> ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg")
                        .body(activity.getImage()))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Download the information PDF of an activity")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<org.springframework.core.io.Resource> getActivityPdf(@PathVariable Long id)
            throws java.net.MalformedURLException {
        Activity activity = activityService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        String fileName = activity.getPdfFilename();
        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path rootPath = Paths.get("uploads/docs/").toAbsolutePath();
        Path filePath = rootPath.resolve(fileName).normalize();

        if (!filePath.startsWith(rootPath)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid file path");
        }
        org.springframework.core.io.Resource pdf = new org.springframework.core.io.UrlResource(filePath.toUri());

        if (!pdf.exists() || !pdf.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The PDF file does not exist on disk");
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(pdf);
    }
}