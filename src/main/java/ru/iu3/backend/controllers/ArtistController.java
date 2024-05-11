package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Artists;
import ru.iu3.backend.repositories.ArtistsRepository;
import ru.iu3.backend.repositories.CountryRepository;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import ru.iu3.backend.tools.DataValidationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")

public class ArtistController {

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    ArtistsRepository artistsRepository;

    @GetMapping("/artists")
    public Page getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return artistsRepository.findAll(
                PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity getArtist(@PathVariable(value = "id") Long artistId) throws DataValidationException {
        Artists artists = artistsRepository.findById(artistId).
                orElseThrow(() -> new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artists);
    }



    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artists artists)
            throws Exception {
        try {
            Artists newArtists = artistsRepository.save(artists);
            return new ResponseEntity<Object>(newArtists, HttpStatus.OK);
        }
        catch(Exception exception) {
            if (exception.getMessage().contains("artists.name_UNIQUE")) {
                throw new DataValidationException("Эта страна уже есть в базе");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }


    @PutMapping("/artists/{id}")
    public ResponseEntity<Artists> updateArtists(@PathVariable(value = "id") Long artistId,
                                                 @RequestBody Artists artistDetails) {
        Artists artists = null;
        Optional<Artists>
                cc = artistsRepository.findById(artistId);
        if (cc.isPresent()) {
            artists = cc.get();
            artists.name = artistDetails.name;
            artistsRepository.save(artists);
            return ResponseEntity.ok(artists);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "artist not found");
        }
    }


    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Object> deleteArtists(@PathVariable(value = "id") Long artistId) {
        Optional<Artists>
                artists = artistsRepository.findById(artistId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (artists.isPresent()) {
            artistsRepository.delete(artists.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }
    @PostMapping("/deleteartists")
    public ResponseEntity deleteArtists(@Valid @RequestBody List artists) {
        artistsRepository.deleteAll(artists);
        return new ResponseEntity(HttpStatus.OK);
    }

}