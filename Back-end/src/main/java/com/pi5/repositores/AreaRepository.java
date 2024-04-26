package com.pi5.repositores;

import com.pi5.models.Area;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, UUID> {
}
