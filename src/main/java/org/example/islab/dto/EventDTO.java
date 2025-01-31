package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
public class EventDTO {
    @Nullable Long id;
    String name;
    Long minAge;
    Integer ticketsCount;
    @Nullable Long ownerId;
}
