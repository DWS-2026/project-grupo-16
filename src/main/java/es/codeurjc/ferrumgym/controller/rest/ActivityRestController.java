package es.codeurjc.ferrumgym.controller.rest;

import es.codeurjc.ferrumgym.dto.ActivityDTO;
import es.codeurjc.ferrumgym.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
}