package org.example.islab.controller;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.example.islab.dto.*;
import org.example.islab.entity.*;
import org.example.islab.service.EventService;
import org.example.islab.service.TicketService;
import org.example.islab.service.UserService;
import org.example.islab.service.VenueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket")
@AllArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final EventService eventService;
    private final VenueService venueService;
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping
    public List<TicketDTO> getAllTickets() {
        return ticketService.getAll().stream().map(this::convertToDto).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TicketDTO getTicketByID(@PathVariable Long id) {
        return convertToDto(ticketService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createTicket(@RequestBody TicketDTO dto) {
        Ticket entity = convertToEntity(dto, userService.getCurrentUser());
        Ticket saved = ticketService.create(entity);
        simpMessagingTemplate.convertAndSend("/topic/newTicket", convertToDto(saved));
        return saved.getId();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateTicket(@PathVariable Long id, @RequestBody TicketDTO dto) {
        Ticket entity = convertToEntity(dto, userService.getCurrentUser());
        Ticket updated = ticketService.updateById(id, entity, userService.getCurrentUser());
        simpMessagingTemplate.convertAndSend("/topic/updatedTicket", convertToDto(updated));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        ticketService.deleteById(id, userService.getCurrentUser());
        simpMessagingTemplate.convertAndSend("/topic/removeTicket", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total-number")
    @ResponseStatus(HttpStatus.OK)
    public Long getTotalNumber() {
        return ticketService.getTotalNumber();
    }

    @GetMapping("/find-by-substring")
    @ResponseStatus(HttpStatus.OK)
    public List<TicketDTO> findBySubstring(@PathParam("substring") String substring) {
        return ticketService.findBySubstring(substring).stream().map(this::convertToDto).toList();
    }

    @GetMapping("/find-by-prefix")
    @ResponseStatus(HttpStatus.OK)
    public List<TicketDTO> findByPrefix(@PathParam("prefix") String prefix) {
        return ticketService.findByPrefix(prefix).stream().map(this::convertToDto).toList();
    }

    @PostMapping("/duplicate-vip/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Long duplicateAsVip(@PathVariable Long id) {
        Ticket saved = ticketService.duplicateAsVip(id);
        simpMessagingTemplate.convertAndSend("/topic/newTicket", convertToDto(saved));
        return saved.getId();
    }


    private TicketDTO convertToDto(Ticket ticket){
        return new TicketDTO(
                ticket.getId(),
                ticket.getName(),
                new CoordinatesDTO(
                        ticket.getCoordinates().getX(),
                        ticket.getCoordinates().getY()
                ),
                ticket.getCreationDate(),
                new PersonDTO(
                        ticket.getPerson().getEyeColor(),
                        ticket.getPerson().getHairColor(),
                        new LocationDTO(
                                ticket.getPerson().getLocation().getX().longValue(),
                                ticket.getPerson().getLocation().getY(),
                                ticket.getPerson().getLocation().getZ()
                        ),
                        ticket.getPerson().getBirthday(),
                        ticket.getPerson().getHeight(),
                        ticket.getPerson().getWeight()
                ),
                ticket.getEvent().getId(),
                ticket.getPrice(),
                ticket.getType(),
                (double) ticket.getDiscount(),
                ticket.getNumber(),
                ticket.getComment(),
                ticket.getVenue().getId(),
                ticket.getOwner().getId()
        );
    }

    private Ticket convertToEntity(TicketDTO dto, User owner) {
        return new Ticket(
                dto.getName(),
                new Coordinates(
                        dto.getCoordinates().getX(),
                        dto.getCoordinates().getY()
                ),
                dto.getCreationDate(),
                new Person(
                        dto.getPerson().getEyeColor(),
                        dto.getPerson().getHairColor(),
                        new Location(
                                dto.getPerson().getLocation().getX().intValue(),
                                dto.getPerson().getLocation().getY(),
                                dto.getPerson().getLocation().getZ()
                        ),
                        dto.getPerson().getBirthday(),
                        dto.getPerson().getHeight(),
                        dto.getPerson().getWeight()
                ),
                eventService.getById(dto.getEventId()),
                dto.getPrice(),
                dto.getType(),
                dto.getDiscount().floatValue(),
                dto.getNumber(),
                dto.getComment(),
                venueService.getById(dto.getVenueId()),
                owner
        );
    }
}
