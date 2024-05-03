package com.CondoSync.repositores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.CondoSync.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
