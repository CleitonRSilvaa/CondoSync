package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CondoSync.repositores.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

}
