package org.example.islab.controller;

import lombok.AllArgsConstructor;
import org.example.islab.annotation.RequireRole;
import org.example.islab.dto.HistoryDTO;
import org.example.islab.entity.History;
import org.example.islab.entity.UserType;
import org.example.islab.service.HistoryService;
import org.example.islab.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/import-history")
@AllArgsConstructor
public class HistoryController {
    private final UserService userService;
    private final HistoryService historyService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/admin")
    @RequireRole(UserType.ADMIN)
    public List<HistoryDTO> getAll() {
        return historyService.getAll().stream().map(HistoryService::convertToDto).toList();
    }

    @GetMapping
    public List<HistoryDTO> getAllByUser() {
        return historyService.getAll(userService.getCurrentUser()).stream().map(HistoryService::convertToDto).toList();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(@PathVariable Long id) throws Exception {
        History his = historyService.getById(id);
        InputStream is = this.historyService.getFile(his);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + his.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(is.readAllBytes());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws Exception {
        this.historyService.deleteById(id, userService.getCurrentUser());
        simpMessagingTemplate.convertAndSend("/topic/removeImport", id);
    }
}
