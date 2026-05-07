package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.ActivityDTO; 
import es.codeurjc.ferrumgym.dto.ActivityMapper;
import es.codeurjc.ferrumgym.model.Activity;
import es.codeurjc.ferrumgym.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        // El Mapper transforma la Entidad pura del servicio en el Record de respuesta
        return ResponseEntity.ok(activities.map(activityMapper::toDTO));
    }

    @Operation(summary = "Create a new activity")
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody ActivityDTO activityDto) {
        Activity activity = activityMapper.toEntity(activityDto);
        activityService.save(activity);
        
        java.net.URI location = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(activity.getId())
                .toUri();

        return ResponseEntity.created(location).body(activityMapper.toDTO(activity));
    }

    @Operation(summary = "Update an existing activity")
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO activityDto) {

        Activity updatedDetails = activityMapper.toEntity(activityDto);
        
        // 3. El servicio se encarga de la lógica de guardado
        Activity savedActivity = activityService.update(id, updatedDetails);
        
        return ResponseEntity.ok(activityMapper.toDTO(savedActivity));
    }

    @Operation(summary = "Delete an activity")
    @DeleteMapping("/{id}")
    // El Punto 7: Borrado de una actividad
    // Además, esto está protegido por @PreAuthorize o por SecurityConfig
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get an activity by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity found",
            content = { @Content(mediaType = "application/json",
            schema = @Schema(implementation = ActivityDTO.class)) }), 
        @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        return activityService.findById(id)
                .map(activity -> ResponseEntity.ok(activityMapper.toDTO(activity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get the image of an activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image found", content = @Content),
        @ApiResponse(responseCode = "404", description = "Image or Activity not found", content = @Content)
    })
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getActivityImage(@PathVariable Long id) {
        // Mantenemos el estilo funcional que evita errores de Optional
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

        // Path where files are stored on disk
        java.nio.file.Path filePath = java.nio.file.Paths.get("uploads/docs/").resolve(fileName);
        org.springframework.core.io.Resource pdf = new org.springframework.core.io.UrlResource(filePath.toUri());

        if (!pdf.exists() || !pdf.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The PDF file does not exist on the server disk");
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(pdf);
    }


}