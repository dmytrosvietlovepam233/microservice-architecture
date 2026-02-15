package com.learn.microservice.songservice.controller;

import com.learn.microservice.songservice.dto.SongDto;
import com.learn.microservice.songservice.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public ResponseEntity<List<SongDto>> getAllSongs() {
        List<SongDto> songs = songService.getAllSongs();
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable("id") Integer id) {
        SongDto song = songService.getSongById(id);
        return ResponseEntity.ok(song);
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> createSong(@Validated @RequestBody SongDto songDto) {
        SongDto created = songService.createSong(songDto);
        return ResponseEntity.ok(Map.of("id", created.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDto> updateSong(@PathVariable Integer id, @Validated @RequestBody SongDto songDto) {
        Optional<SongDto> updated = songService.updateSong(id, songDto);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Integer id) {
        if (songService.deleteSong(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Integer>>> deleteSongs(@RequestParam("id") String idsParam) {
        List<Integer> deletedIds = songService.deleteSongsByCsv(idsParam);
        return ResponseEntity.ok(Map.of("ids", deletedIds));
    }
}