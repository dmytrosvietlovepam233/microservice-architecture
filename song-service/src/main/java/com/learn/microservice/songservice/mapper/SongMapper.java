package com.learn.microservice.songservice.mapper;

import com.learn.microservice.songservice.dto.SongDto;
import com.learn.microservice.songservice.model.Song;

public class SongMapper {
    public static SongDto toDto(Song song) {
        SongDto dto = new SongDto();
        dto.setId(song.getId());
        dto.setName(song.getName());
        dto.setArtist(song.getArtist());
        dto.setAlbum(song.getAlbum());
        dto.setDuration(song.getDuration());
        dto.setYear(song.getYear());
        return dto;
    }

    public static Song toEntity(SongDto dto) {
        Song song = new Song();
        song.setId(dto.getId());
        song.setName(dto.getName());
        song.setArtist(dto.getArtist());
        song.setAlbum(dto.getAlbum());
        song.setDuration(dto.getDuration());
        song.setYear(dto.getYear());
        return song;
    }
}