package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.islab.entity.Event;
import org.example.islab.entity.ImportStatus;
import org.example.islab.entity.User;
import org.example.islab.repository.EventRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final HistoryService historyService;

    public List<Event> getAll(){
        return eventRepository.findAll();
    }

    public Event getById(Long id){
        return eventRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Event not found", null, null, null)
        );
    }

    public Event create(Event entity){
        return eventRepository.save(entity);
    }

    @Transactional
    public Event updateById(Long id, Event entity, User user){
        Event currentEvent = eventRepository.getReferenceById(id);

        if (currentEvent.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        currentEvent.setName(entity.getName());
        currentEvent.setMinAge(entity.getMinAge());
        currentEvent.setTicketsCount(entity.getTicketsCount());

        return eventRepository.save(currentEvent);
    }

    public void deleteById(Long id, User user){
        if (eventRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        eventRepository.deleteById(id);
    }

    @Transactional
    public List<Event> uploadFile(MultipartFile file, User user) throws IOException {
        List<Event> res = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Берем первый лист

            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Event e = new Event();

                e.setName(row.getCell(0).getStringCellValue());
                e.setMinAge((long) row.getCell(1).getNumericCellValue());
                e.setTicketsCount((int) row.getCell(2).getNumericCellValue());
                e.setOwner(user);

                eventRepository.save(e);

                res.add(e);
            }
        } catch (Exception e){
            historyService.create(ImportStatus.FAILED, 0L, user);
            throw e;
        }
        historyService.create(ImportStatus.SUCCESS, (long) res.size(), user);
        return res;
    }

    public void cancelEvent(Long id, User user) {
        if (eventRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");
        eventRepository.cancelEvent(id);
    }
}
