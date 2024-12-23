package org.example.islab.repository;

import org.example.islab.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query(value = "SELECT * FROM calculate_total_number()", nativeQuery = true)
    Long getTotalNumber();

    @Query(value = "SELECT * FROM find_tickets_by_comment_substring(:substring)", nativeQuery = true)
    List<Ticket> findBySubstring(@Param("substring") String substring);

    @Query(value = "SELECT * FROM find_tickets_by_comment_prefix(:prefix)", nativeQuery = true)
    List<Ticket> findByPrefix(@Param("prefix") String prefix);

    @Query(value = "SELECT duplicate_ticket_as_vip(:id);", nativeQuery = true)
    Ticket duplicateAsVip(@Param("id") Long id);
}
