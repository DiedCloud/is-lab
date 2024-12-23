package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.islab.entity.TicketType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TicketDTO {
    Long id;
    String name;
    CoordinatesDTO coordinates;
    LocalDate creationDate;
    PersonDTO person;
    Long eventId;
    Double price;
    TicketType type;
    Double discount;
    Long number;
    String comment;
    Long venueId;
}
