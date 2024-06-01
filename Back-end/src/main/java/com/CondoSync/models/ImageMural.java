package com.CondoSync.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "image_mural")
public class ImageMural {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "image_id")
  private Integer id;

  @NotBlank
  @Lob
  @Column(length = 1000000, nullable = false)
  private String base64;

  @NotBlank(message = "Name is mandatory")
  private String name;

  @JsonIgnore
  private Boolean defaultImg;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mural_id", nullable = false)
  private Mural mural;

  @JsonIgnore
  @CreationTimestamp
  private Instant creation;

  @JsonIgnore
  @UpdateTimestamp
  private Instant upudate;

}
