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
@RequestMapping("/api/v1/activities") // Plural and mandatory prefix according to REST standards
public class ActivityRestController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityMapper activityMapper;

    @Operation(summary = "Get all activities paginated")
    @GetMapping
    public ResponseEntity<Page<ActivityDTO>> getActivities(@PageableDefault(size = 10) Pageable page) {
        // We map the resulting Page of Entities directly to a Page of DTOs
        return ResponseEntity.ok(activityService.findAll(page).map(activityMapper::toDTO));
    }

    // ... (Other endpoints remain unchanged)

    @Operation(summary = "Download the information PDF of an activity")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<org.springframework.core.io.Resource> getActivityPdf(@PathVariable Long id)
            throws java.net.MalformedURLException {
        Activity activity = activityService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        // Verify if the activity has an associated PDF filename
        String fileName = activity.getPdfFilename();
        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Construct the physical path to the stored PDF file on disk
        Path filePath = Paths.get("uploads/docs/").resolve(fileName);
        org.springframework.core.io.Resource pdf = new org.springframework.core.io.UrlResource(filePath.toUri());

        // Check if the file actually exists and is readable by the system
        if (!pdf.exists() || !pdf.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The PDF file does not exist on disk");
        }

        // Triggers the browser to download the file while maintaining its original name
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(pdf);
    }
}