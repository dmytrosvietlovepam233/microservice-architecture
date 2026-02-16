package com.learn.microservice.songservice.controller;

import com.learn.microservice.songservice.dto.SongDto;
import com.learn.microservice.songservice.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
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

    @DeleteMapping
    public ResponseEntity<Map<String, List<Integer>>> deleteSongs(@RequestParam("id") String idsParam) {
        List<Integer> deletedIds = songService.deleteSongsByCsv(idsParam);
        return ResponseEntity.ok(Map.of("ids", deletedIds));
    }
}