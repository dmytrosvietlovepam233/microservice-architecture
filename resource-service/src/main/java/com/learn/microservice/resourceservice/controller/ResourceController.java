package com.learn.microservice.resourceservice.controller;

import com.learn.microservice.resourceservice.service.ResourceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> uploadResource(@RequestBody byte[] mp3Data,
                                                               @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType) {
        Integer id = resourceService.uploadResource(mp3Data, contentType);
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getResource(@PathVariable("id") Integer id) {
        byte[] data = resourceService.getResourceData(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length))
                .body(data);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Integer>>> deleteResources(@RequestParam("id") String idsParam) {
        List<Integer> deletedIds = resourceService.deleteResourcesByCsv(idsParam);
        return ResponseEntity.ok(Map.of("ids", deletedIds));
    }
}
