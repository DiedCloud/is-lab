package org.example.islab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.Collection;

import static org.hibernate.internal.util.collections.CollectionHelper.listOf;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotEmpty
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Min(1)
    private int capacity; //Значение поля должно быть больше 0

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @NotNull
    private VenueType type; //Поле не может быть null

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Ticket> ticket = listOf();

    public Venue(
            final String name,
            final int capacity,
            final VenueType type
    ) {
        this.name = name;
        this.capacity = capacity;
        this.type = type;
    }
}
