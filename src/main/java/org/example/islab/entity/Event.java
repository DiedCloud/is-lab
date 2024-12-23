package org.example.islab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Collection;

import static org.hibernate.internal.util.collections.CollectionHelper.listOf;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotEmpty
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull
    private Long minAge; //Поле не может быть null

    @NotNull
    @Min(1)
    private Integer ticketsCount; //Поле не может быть null, Значение поля должно быть больше 0

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Ticket> ticket = listOf();

    public Event(
            final String name,
            final Long minAge,
            final Integer ticketsCount,
            final User user
    ) {
        this.name = name;
        this.minAge = minAge;
        this.ticketsCount = ticketsCount;
        this.owner = user;
    }
}
