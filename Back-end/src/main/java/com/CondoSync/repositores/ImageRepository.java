package com.CondoSync.repositores;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
