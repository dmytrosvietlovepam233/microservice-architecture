package com.learn.microservice.songservice.service;

import com.learn.microservice.songservice.dto.SongDto;
import com.learn.microservice.songservice.exception.BadRequestException;
import com.learn.microservice.songservice.exception.ConflictException;
import com.learn.microservice.songservice.exception.ResourceNotFoundException;
import com.learn.microservice.songservice.mapper.SongMapper;
import com.learn.microservice.songservice.model.Song;
import com.learn.microservice.songservice.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<SongDto> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(SongMapper::toDto)
                .collect(Collectors.toList());
    }

    public SongDto getSongById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid value '" + id + "' for ID. Must be a positive integer");
        }
        return songRepository.findById(id)
                .map(SongMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Song metadata for ID=" + id + " not found"));
    }

    public SongDto createSong(SongDto songDto) {
        if (songRepository.existsById(songDto.getId())) {
            throw new ConflictException("Metadata for resource ID=" + songDto.getId() + " already exists");
        }
        Song song = SongMapper.toEntity(songDto);
        Song saved = songRepository.save(song);
        return SongMapper.toDto(saved);
    }

    public Optional<SongDto> updateSong(Integer id, SongDto songDto) {
        return songRepository.findById(id)
                .map(existing -> {
                    existing.setName(songDto.getName());
                    existing.setArtist(songDto.getArtist());
                    existing.setAlbum(songDto.getAlbum());
                    existing.setDuration(songDto.getDuration());
                    existing.setYear(songDto.getYear());
                    Song updated = songRepository.save(existing);
                    return SongMapper.toDto(updated);
                });
    }

    public boolean deleteSong(Integer id) {
        if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Integer> deleteSongs(List<Integer> ids) {
        List<Integer> deletedIds = new ArrayList<>();
        for (Integer id : ids) {
            if (songRepository.existsById(id)) {
                songRepository.deleteById(id);
                deletedIds.add(id);
            }
        }
        return deletedIds;
    }

    public List<Integer> parseAndValidateIds(String idsParam) {
        if (idsParam.length() > 200) {
            throw new BadRequestException(
                    "CSV string is too long: received " + idsParam.length() + " characters, maximum allowed is 200"
            );
        }
        List<Integer> ids = new ArrayList<>();
        for (String part : idsParam.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.matches("^[1-9]\\d*$")) {
                throw new BadRequestException("Invalid ID format: '" + trimmed + "'. Only positive integers are allowed");
            }
            ids.add(Integer.valueOf(trimmed));
        }
        return ids;
    }

    public List<Integer> deleteSongsByCsv(String idsParam) {
        List<Integer> ids = parseAndValidateIds(idsParam);
        return deleteSongs(ids);
    }
}