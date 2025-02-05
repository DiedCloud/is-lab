package org.example.islab.service;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.islab.entity.*;
import org.example.islab.entity.Color;
import org.example.islab.repository.CoordinatesRepository;
import org.example.islab.repository.LocationRepository;
import org.example.islab.repository.PersonRepository;
import org.example.islab.repository.TicketRepository;
import org.example.islab.validation.TicketValidator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final PersonRepository personRepository;
    private final LocationRepository locationRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final EventService eventService;
    private final VenueService venueService;
    private final HistoryService historyService;
    private final TicketValidator ticketValidator;

    public List<Ticket> getAll(){
        return ticketRepository.findAll();
    }

    public Ticket getById(Long id){
        return ticketRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Ticket not found", null, null, null)
        );
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Ticket create(Ticket entity){
        coordinatesRepository.save(entity.getCoordinates());
        locationRepository.save(entity.getPerson().getLocation());
        personRepository.save(entity.getPerson());

        if (ticketValidator.validateTicket(entity)) {
            return ticketRepository.save(entity);
        } else {
            throw new IllegalArgumentException("Ticket type/number must be unique!");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Ticket updateById(Long id, Ticket entity, User user){
        Ticket currentTicket = ticketRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Ticket not found", null, null, null)
        );

        if (currentTicket.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        currentTicket.setName(entity.getName());

        Coordinates cord = currentTicket.getCoordinates();
        cord.setX(entity.getCoordinates().getX());
        cord.setY(entity.getCoordinates().getY());
        coordinatesRepository.save(cord);

        currentTicket.setCreationDate(entity.getCreationDate());

        Person person = currentTicket.getPerson();

        Location loc = currentTicket.getPerson().getLocation();
        loc.setX(entity.getPerson().getLocation().getX());
        loc.setY(entity.getPerson().getLocation().getY());
        loc.setZ(entity.getPerson().getLocation().getZ());
        locationRepository.save(loc);

        person.setEyeColor(entity.getPerson().getEyeColor());
        person.setHairColor(entity.getPerson().getHairColor());
        person.setBirthday(entity.getPerson().getBirthday());
        person.setHeight(entity.getPerson().getHeight());
        person.setWeight(entity.getPerson().getWeight());
        personRepository.save(person);

        currentTicket.setEvent(entity.getEvent());
        currentTicket.setPrice(entity.getPrice());
        currentTicket.setType(entity.getType());
        currentTicket.setDiscount(entity.getDiscount());
        currentTicket.setNumber(entity.getNumber());
        currentTicket.setComment(entity.getComment());
        currentTicket.setVenue(entity.getVenue());

        if (ticketValidator.validateTicket(currentTicket)) {
            return ticketRepository.save(currentTicket);
        } else {
            throw new IllegalArgumentException("Ticket type/number must be unique!");
        }
    }

    public void deleteById(Long id, User user){
        Ticket ticket = ticketRepository.findById(id).orElseThrow(
                () -> HttpClientErrorException.create(HttpStatusCode.valueOf(404), "Ticket not found", null, null, null)
        );
        if (ticket.getOwner() != user
                && user.getAuthorities().stream().noneMatch((GrantedAuthority it) -> it.getAuthority().equals("ADMIN")))
            throw new AccessDeniedException("You are not owner of the object");

        ticketRepository.deleteById(id);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Ticket> uploadFile(MultipartFile file, User user) throws IOException {
        Jsr310JpaConverters.LocalDateConverter ldc = new Jsr310JpaConverters.LocalDateConverter();
        List<Ticket> res = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Берем первый лист

            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Ticket t = new Ticket();

                t.setName(row.getCell(0).getStringCellValue());

                Coordinates cord = new Coordinates();
                cord.setX((int) row.getCell(1).getNumericCellValue());
                cord.setY((int) row.getCell(2).getNumericCellValue());
                coordinatesRepository.save(cord);
                t.setCoordinates(cord);

                t.setCreationDate(ldc.convertToEntityAttribute(row.getCell(3).getDateCellValue()));

                Person pers = new Person();
                pers.setEyeColor(Color.valueOf(row.getCell(4).getStringCellValue()));
                pers.setHairColor(Color.valueOf(row.getCell(5).getStringCellValue()));

                Location loc = new Location();
                loc.setX((int) row.getCell(6).getNumericCellValue());
                loc.setY((long) row.getCell(7).getNumericCellValue());
                loc.setZ(row.getCell(8).getNumericCellValue());
                locationRepository.save(loc);
                pers.setLocation(loc);

                pers.setBirthday(ldc.convertToEntityAttribute(row.getCell(9).getDateCellValue()));
                pers.setHeight((float) row.getCell(10).getNumericCellValue());
                pers.setWeight((int) row.getCell(11).getNumericCellValue());
                personRepository.save(pers);
                t.setPerson(pers);

                t.setEvent(eventService.getById((long) row.getCell(12).getNumericCellValue()));

                t.setPrice(row.getCell(13).getNumericCellValue());
                t.setType(TicketType.valueOf(row.getCell(14).getStringCellValue()));
                t.setDiscount((float) row.getCell(15).getNumericCellValue());
                t.setNumber((long) row.getCell(16).getNumericCellValue());
                t.setComment(row.getCell(17).getStringCellValue());

                t.setVenue(venueService.getById((long) row.getCell(18).getNumericCellValue()));

                t.setOwner(user);

                if (ticketValidator.validateTicket(t)) {
                    ticketRepository.save(t);
                } else {
                    throw new IllegalArgumentException("Ticket type/number must be unique!");
                }

                res.add(t);
            }
        } catch (Exception e){
            historyService.create(ImportStatus.FAILED, 0L, user);
            throw e;
        }
        historyService.create(ImportStatus.SUCCESS, (long) res.size(), user);
        return res;
    }

    public Long getTotalNumber() {
        return ticketRepository.getTotalNumber();
    }

    public List<Ticket> findBySubstring(String substring) {
        return ticketRepository.findBySubstring(substring);
    }

    public List<Ticket> findByPrefix(String prefix) {
        return ticketRepository.findByPrefix(prefix);
    }

    public Ticket duplicateAsVip(Long id) {
        return ticketRepository.findById(ticketRepository.duplicateAsVip(id))
                .orElseThrow(() -> HttpServerErrorException.create(HttpStatusCode.valueOf(500), "Creating error", null, null, null));
    }
}
