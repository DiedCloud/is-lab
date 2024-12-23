package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.islab.entity.VenueType;

@Data
@AllArgsConstructor
public class VenueDTO {
    String name;
    Integer capacity;
    VenueType type;
}
