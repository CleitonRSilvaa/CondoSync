package com.CondoSync.models;

import java.time.Instant;

import jakarta.persistence.OneToOne;

public class Aviso {

    private Integer id;

    private String title;

    private String description;

    private boolean status;

    @OneToOne
    private Image image;

    private Instant creation;

    private Instant upudate;

}
