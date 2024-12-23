package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDTO {
    Long x;
    Long y;
    Double z;
}
