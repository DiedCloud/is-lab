package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.islab.entity.Color;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PersonDTO {
    Color eyeColor;
    Color hairColor;
    LocationDTO locationDTO;
    LocalDate birthday;
    Float height;
    Integer weight;
}
