package org.example.islab.validation;

import lombok.AllArgsConstructor;
import org.example.islab.entity.Venue;
import org.example.islab.repository.VenueRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class VenueValidator {
    private final VenueRepository venueRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public boolean validateVenue(Venue venue) {
        List<Venue> allTickets = venueRepository.findAll();
        return allTickets.stream().noneMatch((Venue it) -> it.getName().equals(venue.getName()) && !Objects.equals(it.getId(), venue.getId()));
    }
}
