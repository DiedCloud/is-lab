package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.dto.HistoryDTO;
import org.example.islab.entity.History;
import org.example.islab.entity.ImportStatus;
import org.example.islab.entity.User;
import org.example.islab.repository.HistoryRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.io.InputStream;
import java.util.List;

@Service
@AllArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final MinioService minioService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<History> getAll(){
        return historyRepository.findAll();
    }

    public List<History> getAll(User user){
        return historyRepository.findAllByUser(user);
    }

    public History getById(Long id){
        return historyRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "History row not found", null, null, null)
        );
    }

    public History create(ImportStatus status, String fileName, Long addedCount, User user) {
        History history = new History(status, fileName, addedCount, user);
        history = historyRepository.save(history);
        simpMessagingTemplate.convertAndSend("/topic/newImport", convertToDto(history));
        return history;
    }

    @Transactional
    public void deleteById(Long id, User user) throws Exception {
        History his = historyRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "History row not found", null, null, null)
        );
        if (his.getUser() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not loader of the object");

        minioService.deleteFile(his.getFileName());
        historyRepository.deleteById(id);
    }

    public InputStream getFile(History his) throws Exception {
        return minioService.downloadFile(his.getFileName());
    }

    public static HistoryDTO convertToDto(History history){
        return new HistoryDTO(
                history.getId(),
                history.getFileName(),
                history.getStatus(),
                history.getUser().getId(),
                history.getUser().getLogin(),
                history.getAddedCount()
        );
    }
}
