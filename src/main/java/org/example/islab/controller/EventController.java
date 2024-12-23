package org.example.islab.controller;

import lombok.AllArgsConstructor;
import org.example.islab.dto.EventDTO;
import org.example.islab.entity.Event;
import org.example.islab.entity.User;
import org.example.islab.service.EventService;
import org.example.islab.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping
    public List<EventDTO> getAllEvents() {
        return eventService.getAll().stream().map(this::convertToDto).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDTO getEventByID(@PathVariable Long id) {
        return convertToDto(eventService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createEvent(@RequestBody EventDTO dto) {
        Event entity = convertToEntity(dto, userService.getCurrentUser());
        Event saved = eventService.create(entity);
        simpMessagingTemplate.convertAndSend("/topic/newEvent", saved);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventDTO dto) {
        Event entity = convertToEntity(dto, userService.getCurrentUser());
        Event updated = eventService.updateById(id, entity, userService.getCurrentUser());
        simpMessagingTemplate.convertAndSend("/topic/updatedEvent", convertToDto(updated));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteById(id, userService.getCurrentUser());
        simpMessagingTemplate.convertAndSend("/topic/removeEvent", id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> cancelEvent(@PathVariable Long id) {
        eventService.cancelEvent(id);
        simpMessagingTemplate.convertAndSend("/topic/removeEvent", id);
        return ResponseEntity.noContent().build();
    }

    private EventDTO convertToDto(Event event){
        return new EventDTO(
                event.getName(),
                event.getMinAge(),
                event.getTicketsCount()
        );
    }

    private Event convertToEntity(EventDTO dto, User owner) {
        return new Event(
                dto.getName(),
                dto.getMinAge(),
                dto.getTicketsCount(),
                owner
        );
    }
}
