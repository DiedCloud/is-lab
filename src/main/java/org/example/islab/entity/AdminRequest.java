package org.example.islab.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    User user;

    LocalDateTime requestDate = LocalDateTime.now();

    @Enumerated(value = EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    AdminRequestStatus status;

    String comment;

    public AdminRequest(
            final User user,
            final AdminRequestStatus status,
            final String comment,
            final LocalDateTime requestDate
    ){
        this.user = user;
        this.status = status;
        this.comment = comment;
        this.requestDate = requestDate;
    }
}
