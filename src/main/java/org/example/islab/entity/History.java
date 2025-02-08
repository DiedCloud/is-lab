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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Import_History")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String fileName;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @NotNull
    private ImportStatus status = ImportStatus.FAILED;

    @NotNull
    @Min(0)
    private Long addedCount = 0L;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public History(ImportStatus status, String fileName, Long addedCount, User user){
        this.status = status;
        this.fileName = fileName;
        this.addedCount = addedCount;
        this.user = user;
    }
}
