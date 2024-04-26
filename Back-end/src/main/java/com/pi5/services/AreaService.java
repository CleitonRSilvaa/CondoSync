package com.pi5.services;

import com.pi5.repositores.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaService {
    @Autowired
    private AreaRepository areaRepository;

}
