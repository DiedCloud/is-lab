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
import org.example.islab.util.RandomStringGenerator;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final HistoryService historyService;
    private final MinioService minioService;

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
    public List<Event> uploadFile(MultipartFile file, User user) throws Exception {
        List<Event> res = new ArrayList<>();
        String fileName = RandomStringGenerator.getRandomString(10) + "_";

        byte[] fileBytes = file.getBytes(); // Читаем файл в массив байтов для переиспользования

        try (InputStream inputStream = new ByteArrayInputStream(fileBytes); Workbook workbook = new XSSFWorkbook(inputStream)) {
            if (file.getOriginalFilename() != null){
                String[] t = file.getOriginalFilename().split("/");
                fileName += t[t.length - 1];
            } else {
                fileName += "EmptyName";
            }

            Sheet sheet = workbook.getSheetAt(0); // Берем первый лист

            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Event e = new Event();

                e.setName(row.getCell(0).getStringCellValue());
                e.setMinAge((long) row.getCell(1).getNumericCellValue());
                e.setTicketsCount((int) row.getCell(2).getNumericCellValue());
                e.setOwner(user);

                res.add(e);
            }

            eventRepository.saveAll(res);
            try (InputStream minioInputStream = new ByteArrayInputStream(fileBytes)) {
                minioService.uploadFile(fileName, minioInputStream, file.getSize(), file.getContentType());
            }
        } catch (Exception e){
            historyService.create(ImportStatus.FAILED, fileName, 0L, user);
            throw e;
        }
        historyService.create(ImportStatus.SUCCESS, fileName, (long) res.size(), user);
        return res;
    }

    public void cancelEvent(Long id, User user) {
        if (eventRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");
        eventRepository.cancelEvent(id);
    }
}
