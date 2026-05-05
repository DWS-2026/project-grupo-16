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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}