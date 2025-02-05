package org.example.islab.controller;

import lombok.AllArgsConstructor;
import org.example.islab.annotation.RequireRole;
import org.example.islab.dto.HistoryDTO;
import org.example.islab.entity.History;
import org.example.islab.entity.UserType;
import org.example.islab.service.HistoryService;
import org.example.islab.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/import-history")
@AllArgsConstructor
public class HistoryController {
    private final UserService userService;
    private final HistoryService historyService;

    @GetMapping("/admin")
    @RequireRole(UserType.ADMIN)
    public List<HistoryDTO> getAll() {
        return historyService.getAll().stream().map(this::convertToDto).toList();
    }

    @GetMapping
    public List<HistoryDTO> getAllByUser() {
        return historyService.getAll(userService.getCurrentUser()).stream().map(this::convertToDto).toList();
    }

    private HistoryDTO convertToDto(History history){
        return new HistoryDTO(
                history.getId(),
                history.getStatus(),
                history.getUser().getId(),
                history.getUser().getLogin(),
                history.getAddedCount()
        );
    }
}
