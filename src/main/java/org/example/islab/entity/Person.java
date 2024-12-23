package org.example.islab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @NotNull
    private Color eyeColor; //Поле может быть null

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @NotNull
    private Color hairColor; //Поле может быть null

    @ManyToOne
    @NotNull
    private Location location; //Поле не может быть null

    @NotNull
    private java.time.LocalDate birthday; //Поле не может быть null

    @Min(1)
    private float height; //Значение поля должно быть больше 0

    @Min(1)
    private int weight; //Значение поля должно быть больше 0

    public Person(
            final Color eyeColor,
            final Color hairColor,
            final Location location,
            final LocalDate birthday,
            final float height,
            final int weight
    ) {
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
    }
}
