package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.islab.entity.*;
import org.example.islab.repository.VenueRepository;
import org.example.islab.util.RandomStringGenerator;
import org.example.islab.validation.VenueValidator;
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
public class VenueService {
    private final VenueRepository venueRepository;
    private final VenueValidator venueValidator;
    private final HistoryService historyService;
    private final MinioService minioService;

    public List<Venue> getAll() {
        return venueRepository.findAll();
    }

    public Venue getById(Long id) {
        return venueRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Venue not found", null, null, null)
        );
    }

    public Venue create(Venue entity) throws IllegalArgumentException {
        if (venueValidator.validateVenue(entity)) {
            return venueRepository.save(entity);
        } else {
            throw new IllegalArgumentException("Venue name must be unique!");
        }
    }

    @Transactional
    public Venue updateById(Long id, Venue entity, User user) {
        Venue currentVenue = venueRepository.getReferenceById(id);

        if (currentVenue.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        currentVenue.setName(entity.getName());
        currentVenue.setCapacity(entity.getCapacity());
        currentVenue.setType(entity.getType());

        if (venueValidator.validateVenue(currentVenue)) {
            return venueRepository.save(currentVenue);
        } else {
            throw new IllegalArgumentException("Venue name must be unique!");
        }
    }

    public void deleteById(Long id, User user) {
        if (venueRepository.getReferenceById(id).getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        venueRepository.deleteById(id);
    }

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public List<Venue> uploadFile(MultipartFile file, User user) throws Exception {
        List<Venue> res = new ArrayList<>();
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
                Venue v = new Venue();

                v.setName(row.getCell(0).getStringCellValue());
                v.setCapacity((int) row.getCell(1).getNumericCellValue());
                v.setType(VenueType.valueOf(row.getCell(2).getStringCellValue()));
                v.setOwner(user);

                res.add(v);
            }

            if (res.stream().allMatch(venueValidator::validateVenue)){
                venueRepository.saveAll(res);
            }
            else {
                throw new IllegalArgumentException("Venue name must be unique!");
            }
            try (InputStream minioInputStream = new ByteArrayInputStream(fileBytes)) {
                minioService.uploadFile(fileName, minioInputStream, file.getSize(), file.getContentType());
            }
        } catch (Exception e) {
            historyService.create(ImportStatus.FAILED, fileName, 0L, user); // если не fileName, то лучше унести в контроллер и не засовывать в create нотификацию по web socket
            throw new RuntimeException("MinIO error!", e);
        }
        historyService.create(ImportStatus.SUCCESS, fileName, (long) res.size(), user);
        return res;
    }
}
