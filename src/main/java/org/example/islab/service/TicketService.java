package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.entity.*;
import org.example.islab.repository.CoordinatesRepository;
import org.example.islab.repository.LocationRepository;
import org.example.islab.repository.PersonRepository;
import org.example.islab.repository.TicketRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;
    private final CoordinatesRepository coordinatesRepository;

    public List<Ticket> getAll(){
        return ticketRepository.findAll();
    }

    public Ticket getById(Long id){
        return ticketRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Ticket not found", null, null, null)
        );
    }

    public Ticket create(Ticket entity){
        coordinatesRepository.save(entity.getCoordinates());
        locationRepository.save(entity.getPerson().getLocation());
        personRepository.save(entity.getPerson());
        return ticketRepository.save(entity);
    }

    public Ticket updateById(Long id, Ticket entity, User user){
        Ticket currentTicket = ticketRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Ticket not found", null, null, null)
        );

        if (currentTicket.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        currentTicket.setName(entity.getName());

        Coordinates cord = currentTicket.getCoordinates();
        cord.setX(entity.getCoordinates().getX());
        cord.setY(entity.getCoordinates().getY());
        coordinatesRepository.save(cord);

        currentTicket.setCreationDate(entity.getCreationDate());

        Person person = currentTicket.getPerson();

        Location loc = currentTicket.getPerson().getLocation();
        loc.setX(entity.getPerson().getLocation().getX());
        loc.setY(entity.getPerson().getLocation().getY());
        loc.setZ(entity.getPerson().getLocation().getZ());
        locationRepository.save(loc);

        person.setEyeColor(entity.getPerson().getEyeColor());
        person.setHairColor(entity.getPerson().getHairColor());
        person.setBirthday(entity.getPerson().getBirthday());
        person.setHeight(entity.getPerson().getHeight());
        person.setWeight(entity.getPerson().getWeight());
        personRepository.save(person);

        currentTicket.setEvent(entity.getEvent());
        currentTicket.setPrice(entity.getPrice());
        currentTicket.setType(entity.getType());
        currentTicket.setDiscount(entity.getDiscount());
        currentTicket.setNumber(entity.getNumber());
        currentTicket.setComment(entity.getComment());
        currentTicket.setVenue(entity.getVenue());

        return ticketRepository.save(currentTicket);
    }

    public void deleteById(Long id, User user){
        Ticket ticket = ticketRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Ticket not found", null, null, null)
        );
        if (ticket.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        ticketRepository.deleteById(id);
    }

    public Long getTotalNumber() {
        return ticketRepository.getTotalNumber();
    }

    public List<Ticket> findBySubstring(String substring) {
        return ticketRepository.findBySubstring(substring);
    }

    public List<Ticket> findByPrefix(String prefix) {
        return ticketRepository.findByPrefix(prefix);
    }

    public Ticket duplicateAsVip(Long id) {
        return ticketRepository.findById(ticketRepository.duplicateAsVip(id))
                .orElseThrow(() -> HttpServerErrorException.create(HttpStatusCode.valueOf(500), "Creating error", null, null, null));
    }
}
