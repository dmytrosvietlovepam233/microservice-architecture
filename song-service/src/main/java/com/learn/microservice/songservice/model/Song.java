package com.learn.microservice.songservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class Song {
    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String artist;

    @Column(nullable = false, length = 100)
    private String album;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private String year;
}