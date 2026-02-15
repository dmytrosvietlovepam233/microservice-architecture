package com.learn.microservice.resourceservice.service;

import com.learn.microservice.resourceservice.model.Resource;
import com.learn.microservice.resourceservice.repository.ResourceRepository;
import com.learn.microservice.resourceservice.dto.SongDto;
import com.learn.microservice.resourceservice.exception.BadRequestException;
import com.learn.microservice.resourceservice.exception.ResourceNotFoundException;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final RestTemplate restTemplate;
    private final Tika tika = new Tika();

    @Value("${song.service.url}")
    private String songServiceUrl;

    public ResourceService(ResourceRepository resourceRepository, RestTemplate restTemplate) {
        this.resourceRepository = resourceRepository;
        this.restTemplate = restTemplate;
    }

    public Integer uploadResource(byte[] mp3Data, String contentType) {
        if (!"audio/mpeg".equalsIgnoreCase(contentType)) {
            throw new BadRequestException("Invalid file format: " + contentType + ". Only MP3 files are allowed");
        }
        if (!isMp3(mp3Data)) {
            throw new BadRequestException("Invalid file format: Only MP3 files are allowed");
        }
        Resource resource = new Resource();
        resource.setData(mp3Data);
        Resource saved = resourceRepository.save(resource);
        SongDto songDto = extractMetadata(mp3Data, saved.getId());
        sendMetadataToSongService(songDto);
        return saved.getId();
    }

    public byte[] getResourceData(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid value '" + id + "' for ID. Must be a positive integer");
        }
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID=" + id + " not found"));
        return resource.getData();
    }

    public List<Integer> deleteResourcesByCsv(String idsParam) {
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
        List<Integer> deletedIds = new ArrayList<>();
        for (Integer id : ids) {
            if (resourceRepository.existsById(id)) {
                resourceRepository.deleteById(id);
                deletedIds.add(id);
            }
        }

        if (!deletedIds.isEmpty()) {
            String deletedIdsCsv = deletedIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            deleteSongMetadataByCsv(deletedIdsCsv);
        }
        return deletedIds;
    }

    private boolean isMp3(byte[] data) {
        try {
            String mimeType = tika.detect(data);
            return "audio/mpeg".equals(mimeType);
        } catch (Exception e) {
            return false;
        }
    }

    private SongDto extractMetadata(byte[] mp3Data, Integer resourceId) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(mp3Data)) {
            Metadata metadata = new Metadata();
            tika.parse(input, metadata);

            String name = metadata.get("dc:title");
            String artist = metadata.get("xmpDM:albumArtist");
            String album = metadata.get("xmpDM:album");
            String durationSec = metadata.get("xmpDM:duration");

            String duration = "00:00";
            if (durationSec != null) {
                int totalSeconds = (int) Math.round(Double.parseDouble(durationSec));
                duration = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
            }

            String year = metadata.get("xmpDM:releaseDate");

            SongDto songDto = new SongDto();
            songDto.setId(resourceId);
            songDto.setName(name);
            songDto.setArtist(artist);
            songDto.setAlbum(album);
            songDto.setDuration(duration);
            songDto.setYear(year);
            return songDto;
        } catch (Exception e) {
            throw new BadRequestException("Failed to extract MP3 metadata");
        }
    }

    private void sendMetadataToSongService(SongDto songDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SongDto> request = new HttpEntity<>(songDto, headers);
        restTemplate.postForEntity(songServiceUrl, request, Void.class);
    }

    private void deleteSongMetadataByCsv(String idsParam) {
        String url = songServiceUrl + "?id=" + idsParam;
        try {
            restTemplate.delete(url);
        } catch (Exception e) {
            // TODO
        }
    }
}