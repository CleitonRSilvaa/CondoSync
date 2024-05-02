package com.CondoSync.repositores;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CondoSync.models.Area;

public interface AreaRepository extends JpaRepository<Area, UUID> {
}
