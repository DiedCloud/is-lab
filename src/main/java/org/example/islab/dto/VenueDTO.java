package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.islab.entity.VenueType;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
public class VenueDTO {
    @Nullable Long id;
    String name;
    Integer capacity;
    VenueType type;
    @Nullable Long ownerId;
}
