package com.CondoSync.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CondoSync.models.Image;
import com.CondoSync.models.Area;
import com.CondoSync.models.DTOs.AreaDTO;
import com.CondoSync.repositores.AreaRepository;

import jakarta.validation.Valid;

@Service
public class AreaService {
    @Autowired
    private AreaRepository areaRepository;

    public Area register(@Valid AreaDTO areaDTO) {

        var area = new Area();
        List<Image> images = new ArrayList<>();

        area.setName(areaDTO.getName());
        area.setDescription(areaDTO.getDescription());
        area.setPrice(areaDTO.getPrice());
        area.setStatus(true);
        for (var img : areaDTO.getImages()) {
            Image image = new Image(img.path(), img.name());
            image.setArea(area);
            images.add(image);
        }
        area.setImages(images);

        return areaRepository.save(area);

    }

    public List<AreaDTO> listAll() {
        List<AreaDTO> areas = new ArrayList<>();
        for (var area : areaRepository.findAll()) {
            AreaDTO areaDTO = new AreaDTO();
            areaDTO.setName(area.getName());
            areaDTO.setDescription(area.getDescription());
            areaDTO.setPrice(area.getPrice());
            var Images = new ArrayList<AreaDTO.Image>();
            for (var img : area.getImages()) {
                Images.add(new AreaDTO.Image(img.getPath(), img.getName()));
            }
            areaDTO.setImages(Images);
            areas.add(areaDTO);
        }
        return areas;
    }

}
