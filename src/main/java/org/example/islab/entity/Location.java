package org.example.islab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Integer x; //Поле не может быть null
    @NotNull
    private Long y; //Поле не может быть null
    private double z;

    public Location(
            final Integer x,
            final Long y,
            final double z
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
