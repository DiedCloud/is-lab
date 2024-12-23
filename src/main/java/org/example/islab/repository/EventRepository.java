package org.example.islab.repository;

import org.example.islab.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT * FROM cancel_event_and_delete_tickets(:id)", nativeQuery = true)
    void cancelEvent(@Param("id") Long id);
}
