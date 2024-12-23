package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventDTO {
    String name;
    Long minAge;
    Integer ticketsCount;
}
