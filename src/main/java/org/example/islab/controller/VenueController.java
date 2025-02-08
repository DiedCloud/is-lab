package org.example.islab.controller;

import lombok.AllArgsConstructor;
import org.example.islab.dto.VenueDTO;
import org.example.islab.entity.Venue;
import org.example.islab.entity.User;
import org.example.islab.service.VenueService;
import org.example.islab.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/venue")
@AllArgsConstructor
public class VenueController {
    private final VenueService venueService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping
    public List<VenueDTO> getAllVenues() {
        return venueService.getAll().stream().map(this::convertToDto).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VenueDTO getVenueByID(@PathVariable Long id) {
        return convertToDto(venueService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createVenue(@RequestBody VenueDTO dto) {
        Venue entity = convertToEntity(dto, userService.getCurrentUser());
        Venue saved = venueService.create(entity);
        simpMessagingTemplate.convertAndSend("/topic/newVenue", convertToDto(saved));
        return saved.getId();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateVenue(@PathVariable Long id, @RequestBody VenueDTO dto) {
        Venue entity = convertToEntity(dto, userService.getCurrentUser());
        Venue updated = venueService.updateById(id, entity, userService.getCurrentUser());
        simpMessagingTemplate.convertAndSend("/topic/updatedVenue", convertToDto(updated));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteVenue(@PathVariable Long id) {
        venueService.deleteById(id, userService.getCurrentUser());
        simpMessagingTemplate.convertAndSend("/topic/removeVenue", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-file")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
        try {
            List<Venue> data = venueService.uploadFile(file, userService.getCurrentUser());
            for (Venue datum : data) {
                simpMessagingTemplate.convertAndSend("/topic/newVenue", convertToDto(datum));
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    private VenueDTO convertToDto(Venue venue){
        return new VenueDTO(
                venue.getId(),
                venue.getName(),
                venue.getCapacity(),
                venue.getType(),
                venue.getOwner().getId()
        );
    }

    private Venue convertToEntity(VenueDTO dto, User owner) {
        return new Venue(
                dto.getName(),
                dto.getCapacity(),
                dto.getType(),
                owner
        );
    }
}
