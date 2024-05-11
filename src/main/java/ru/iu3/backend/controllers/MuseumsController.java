package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.repositories.MuseumRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.tools.DataValidationException;
@CrossOrigin(origins = "http://localhost:3000")
@RestController("/api/v1")
@RequestMapping("/api/v1")
public class MuseumsController {
    @Autowired
    MuseumRepository museumRepository;
    @GetMapping("/museums")
    public Page<Museum> getAllMuseums(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return museumRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/museums/{id}")
    public ResponseEntity<Museum> getMuseum(@PathVariable(value = "id") Long museumID)
            throws DataValidationException {
        Museum museum = museumRepository.findById(museumID)
                .orElseThrow(() -> new DataValidationException("Музей не найден"));

        return ResponseEntity.ok(museum);
    }

    @GetMapping("/museums/{id}/paintings")
    public ResponseEntity<List<Painting>> getPaintingMuseums(@PathVariable(value = "id") Long museumID) {
        Optional<Museum> cc = museumRepository.findById(museumID);
        if (cc.isPresent()) {
            return ResponseEntity.ok(cc.get().paintings);
        }

        return ResponseEntity.ok(new ArrayList<Painting>());
    }

@PostMapping("/museums")
public ResponseEntity<Object> createMuseum(@RequestBody Museum museum) throws DataValidationException {
        try {
            Museum newMusem = museumRepository.save(museum);
            return new ResponseEntity<Object>(newMusem, HttpStatus.OK);
        } catch (Exception exception) {
            if (exception.getMessage().contains("countries.name_UNIQUE")) {
                throw new DataValidationException("Эта страна уже есть в базе");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }

@PutMapping("/museums/{id}")
public ResponseEntity<Museum> updateMuseum(@PathVariable(value = "id") Long museumID,
                                           @RequestBody Museum museumDetails) throws DataValidationException {
    try {
        Museum museum = museumRepository.findById(museumID).
                orElseThrow(() -> new DataValidationException("Музей с таким индексом не найдена"));
            museum.name = museumDetails.name;
            museumRepository.save(museum);
            return ResponseEntity.ok(museum);
    } catch (Exception exception) {
        if (exception.getMessage().contains("ConstraintViolationException")) {
            throw new DataValidationException("Этот музей уже есть в базе");
        } else {
            throw new DataValidationException("Неизвестная ошибка");
        }
        }
    }
    @PostMapping("/deletemuseums")
    public ResponseEntity deleteMuseums(@Validated @RequestBody List<Museum> museums) {
        museumRepository.deleteAll(museums);
        return new ResponseEntity(HttpStatus.OK);
    }
@DeleteMapping("/museums/{id}")
public ResponseEntity<Object> deleteCountry(@PathVariable(value = "id") Long museumID) {
    Optional<Museum> museum = museumRepository.findById(museumID);
    Map<String, Boolean> resp = new HashMap<>();


    if (museum.isPresent()) {
            museumRepository.delete(museum.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else {
            resp.put("deleted", Boolean.FALSE);}
        return ResponseEntity.ok(resp);
    }
}
