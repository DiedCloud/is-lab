package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.islab.entity.AdminRequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminRequestDTO {
    Long id;
    Long userId;
    String login;
    LocalDateTime requestedDate;
    AdminRequestStatus status;
    String comment;
}
