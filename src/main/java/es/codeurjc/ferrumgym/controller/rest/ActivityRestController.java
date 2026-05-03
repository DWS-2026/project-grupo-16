package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.ActivityDTO;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/activities") // Plural y prefijo obligatorio [cite: 487, 490]
public class ActivityRestController {

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public ResponseEntity<Page<ActivityDTO>> getActivities(Pageable page) {
        // Usamos .map(ActivityDTO::new) para convertir cada Activity de la página en un
        // DTO
        return ResponseEntity.ok(activityService.findAll(page).map(ActivityDTO::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivity(@PathVariable Long id) {
        return activityService.findById(id)
                .map(activity -> ResponseEntity.ok(new ActivityDTO(activity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody ActivityDTO activityDto) {
        ActivityDTO saved = activityService.save(activityDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved); // 201 Created + Location
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable long id, @RequestBody ActivityDTO activityDto) {
        // Forzamos que el DTO tenga el ID de la URL para que el Service sepa que es un
        // UPDATE
        activityDto.setId(id);

        ActivityDTO updated = activityService.save(activityDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getActivityImage(@PathVariable Long id) {
        // Buscamos la entidad (no el DTO) para acceder al campo de la imagen
        return activityService.findById(id)
                .filter(activity -> activity.getImage() != null)
                .map(activity -> ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg") // O "image/png"
                        .body(activity.getImage()))
                .orElse(ResponseEntity.notFound().build());
    }

	// Endpoint to download the file (PDF) from disk (Not from DB)
	@GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadActivityPdf(@PathVariable Long id) {
        Optional<Activity> activityOpt = activityService.findById(id);

        // Check if activity exists and has a PDF filename
        if (activityOpt.isPresent() && activityOpt.get().getPdfFilename() != null) {
            try {
                // Construct the path to the stored PDF file in disk
                Path filePath = Paths.get("src/main/resources/static/docs/")
                        .resolve(activityOpt.get().getPdfFilename()).normalize();
                Resource resource = new UrlResource(filePath.toUri());

                // Check if the file exists and is readable
                if (resource.exists() && resource.isReadable()) {
                    return ResponseEntity.ok()
                            // Prompt file download with original filename
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(resource);
                }
            } catch (MalformedURLException e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        // Return 404 if activity doesn't exist, has no PDF, or file is unreadable
        return ResponseEntity.notFound().build();
    }
}
