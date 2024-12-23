package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.entity.Venue;
import org.example.islab.entity.User;
import org.example.islab.repository.VenueRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;

    public List<Venue> getAll(){
        return venueRepository.findAll();
    }

    public Venue getById(Long id){
        return venueRepository.getReferenceById(id);
    }

    public Venue create(Venue entity){
        return venueRepository.save(entity);
    }

    public Venue updateById(Long id, Venue entity, User user){
        Venue currentVenue = venueRepository.getReferenceById(id);

        if (currentVenue.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        currentVenue.setName(entity.getName());
        currentVenue.setCapacity(entity.getCapacity());
        currentVenue.setType(entity.getType());

        return venueRepository.save(currentVenue);
    }

    public void deleteById(Long id, User user){
        if (venueRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        venueRepository.deleteById(id);
    }
}
