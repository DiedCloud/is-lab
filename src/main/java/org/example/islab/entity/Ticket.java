package org.example.islab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotEmpty
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Column(nullable = false)
    private java.time.LocalDate creationDate = LocalDate.now(); //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Min(1)
    private Double price; //Поле не может быть null, Значение поля должно быть больше 0

    @Min(1)
    @Max(99)
    private float discount; //Значение поля должно быть больше 0, Максимальное значение поля: 100

    @Min(1)
    private long number; //Значение поля должно быть больше 0

    @Column(length = 426)
    private String comment; //Длина строки не должна быть больше 426, Поле может быть null

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    private TicketType type; //Поле может быть null

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id", unique = true, nullable = false)
    private Coordinates coordinates; //Поле не может быть null
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id", unique = true, nullable = false)
    private Person person; //Поле может быть null

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; //Поле не может быть null
    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue; //Поле может быть null

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public Ticket(
            final String name,
            final Coordinates coordinates,
            final LocalDate creationDate,
            final Person person,
            final Event event,
            final Double price,
            final TicketType type,
            final float discount,
            final long number,
            final String comment,
            final Venue venue
    ) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.person = person;
        this.event = event;
        this.price = price;
        this.type = type;
        this.discount = discount;
        this.number = number;
        this.comment = comment;
        this.venue = venue;
    }
}
