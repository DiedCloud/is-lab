package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.entity.Event;
import org.example.islab.entity.User;
import org.example.islab.repository.EventRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<Event> getAll(){
        return eventRepository.findAll();
    }

    public Event getById(Long id){
        return eventRepository.getReferenceById(id);
    }

    public Event create(Event entity){
        return eventRepository.save(entity);
    }

    public Event updateById(Long id, Event entity, User user){
        Event currentEvent = eventRepository.getReferenceById(id);

        if (currentEvent.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        currentEvent.setName(entity.getName());
        currentEvent.setMinAge(entity.getMinAge());
        currentEvent.setTicketsCount(entity.getTicketsCount());

        return eventRepository.save(currentEvent);
    }

    public void deleteById(Long id, User user){
        if (eventRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        eventRepository.deleteById(id);
    }

    public void cancelEvent(Long id, User user) {
        if (eventRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");
        eventRepository.cancelEvent(id);
    }
}
