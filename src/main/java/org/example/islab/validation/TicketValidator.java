package org.example.islab.validation;

import lombok.AllArgsConstructor;
import org.example.islab.entity.Ticket;
import org.example.islab.repository.TicketRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class TicketValidator {
    private final TicketRepository ticketRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public boolean validateTicket(Ticket ticket) {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream().noneMatch((Ticket it) -> it.getNumber() == ticket.getNumber() && it.getType() == ticket.getType() && !Objects.equals(it.getId(), ticket.getId()));
    }
}
