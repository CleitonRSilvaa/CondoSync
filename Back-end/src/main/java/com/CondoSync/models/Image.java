package com.CondoSync.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer id;

    @NotBlank
    private String path;

    @NotBlank
    private String name;

    @JsonIgnore
    private Boolean defaultImg;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @JsonIgnore
    @CreationTimestamp
    private Instant creation;

    @JsonIgnore
    @UpdateTimestamp
    private Instant upudate;

    public Image(@NotBlank String path, @NotBlank String name) {
        this.path = path;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Image {id=" + id + ", url=" + path + "}";
    }

}
