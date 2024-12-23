package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.entity.Ticket;
import org.example.islab.entity.User;
import org.example.islab.repository.TicketRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    public List<Ticket> getAll(){
        return ticketRepository.findAll();
    }

    public Ticket getById(Long id){
        return ticketRepository.getReferenceById(id);
    }

    public Ticket create(Ticket entity){
        return ticketRepository.save(entity);
    }

    public Ticket updateById(Long id, Ticket entity, User user){
        Ticket currentTicket = ticketRepository.getReferenceById(id);

        if (currentTicket.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        currentTicket.setName(entity.getName());
        currentTicket.setCoordinates(entity.getCoordinates());
        currentTicket.setCreationDate(entity.getCreationDate());
        currentTicket.setPerson(entity.getPerson());
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
        if (ticketRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        ticketRepository.deleteById(id);
    }
}
