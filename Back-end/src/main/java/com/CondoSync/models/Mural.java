package com.CondoSync.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "mural")
public class Mural {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "mural_id")
  private Integer id;

  @NotBlank(message = "Title is mandatory")
  @Size(max = 50, message = "Title must be less than 50 characters")
  private String title;

  @Size(max = 255, message = "Description must be less than 255 characters")
  private String description;

  private boolean status;

  @NotNull(message = "Image is mandatory")
  @OneToOne(mappedBy = "mural", cascade = CascadeType.ALL, orphanRemoval = true)
  private ImageMural image;

  @JsonIgnore
  @CreationTimestamp
  private Instant creation;

  @JsonIgnore
  @UpdateTimestamp
  private Instant upudate;

}
