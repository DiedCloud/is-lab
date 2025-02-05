package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.islab.entity.*;
import org.example.islab.repository.VenueRepository;
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
public class VenueService {
    private final VenueRepository venueRepository;
    private final HistoryService historyService;

    public List<Venue> getAll(){
        return venueRepository.findAll();
    }

    public Venue getById(Long id){
        return venueRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Venue not found", null, null, null)
        );
    }

    public Venue create(Venue entity){
        return venueRepository.save(entity);
    }

    @Transactional
    public Venue updateById(Long id, Venue entity, User user){
        Venue currentVenue = venueRepository.getReferenceById(id);

        if (currentVenue.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        currentVenue.setName(entity.getName());
        currentVenue.setCapacity(entity.getCapacity());
        currentVenue.setType(entity.getType());

        return venueRepository.save(currentVenue);
    }

    public void deleteById(Long id, User user){
        if (venueRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        venueRepository.deleteById(id);
    }

    @Transactional
    public List<Venue> uploadFile(MultipartFile file, User user) throws IOException {
        List<Venue> res = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Берем первый лист

            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Venue v = new Venue();

                v.setName(row.getCell(0).getStringCellValue());
                v.setCapacity((int) row.getCell(1).getNumericCellValue());
                v.setType(VenueType.valueOf(row.getCell(2).getStringCellValue()));
                v.setOwner(user);

                venueRepository.save(v);

                res.add(v);
            }
        } catch (Exception e){
            historyService.create(ImportStatus.FAILED, 0L, user);
            throw e;
        }
        historyService.create(ImportStatus.SUCCESS, (long) res.size(), user);
        return res;
    }
}
