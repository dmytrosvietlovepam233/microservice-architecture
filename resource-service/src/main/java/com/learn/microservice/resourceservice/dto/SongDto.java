package com.learn.microservice.resourceservice.dto;

import lombok.Data;

@Data
public class SongDto {
    private Integer id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}