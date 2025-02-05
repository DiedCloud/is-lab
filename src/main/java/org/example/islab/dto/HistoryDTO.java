package org.example.islab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.islab.entity.ImportStatus;

@Data
@AllArgsConstructor
public class HistoryDTO {
    Long id;
    ImportStatus status;
    Long userId;
    String login;
    Long addedCount;
}
