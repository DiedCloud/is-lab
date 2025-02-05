package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.example.islab.entity.History;
import org.example.islab.entity.ImportStatus;
import org.example.islab.entity.User;
import org.example.islab.repository.HistoryRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@AllArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

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

    public Long create(ImportStatus status, Long addedCount, User user) {
        History history = new History(status, addedCount, user);
        return historyRepository.save(history).getId();
    }
}
