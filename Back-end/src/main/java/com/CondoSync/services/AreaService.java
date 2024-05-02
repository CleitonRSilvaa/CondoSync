package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CondoSync.repositores.AreaRepository;

@Service
public class AreaService {
    @Autowired
    private AreaRepository areaRepository;

}
