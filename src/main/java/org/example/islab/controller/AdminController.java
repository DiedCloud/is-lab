package org.example.islab.controller;

import lombok.AllArgsConstructor;
import org.example.islab.annotation.RequireRole;
import org.example.islab.dto.*;
import org.example.islab.entity.*;
import org.example.islab.service.AdminRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {
    private final AdminRequestService adminRequestService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping
    @RequireRole(UserType.ADMIN)
    public List<AdminRequestDTO> getAllRequests() {
        return adminRequestService.getAll().stream().map(this::convertToDto).toList();
    }

    @PutMapping("/approve")
    @RequireRole(UserType.ADMIN)
    public ResponseEntity<?> approveRequest(@RequestBody Long requestId) {
        AdminRequest updated = adminRequestService.approveRequest(requestId);
        simpMessagingTemplate.convertAndSend("/topic/updatedAdminRequest", updated);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/decline")
    @RequireRole(UserType.ADMIN)
    public ResponseEntity<?>  rejectRequest(@RequestBody Long requestId) {
        AdminRequest updated = adminRequestService.rejectRequest(requestId);
        simpMessagingTemplate.convertAndSend("/topic/updatedAdminRequest", updated);
        return ResponseEntity.noContent().build();
    }

    private AdminRequestDTO convertToDto(AdminRequest request){
        return new AdminRequestDTO(
                new UserDTO(
                        request.getUser().getUserType(),
                        request.getUser().getLogin()
                ),
                request.getRequestDate(),
                request.getStatus(),
                request.getComment()
        );
    }
}
