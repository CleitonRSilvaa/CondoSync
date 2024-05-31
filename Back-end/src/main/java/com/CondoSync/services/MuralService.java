package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CondoSync.models.ImageMural;
import com.CondoSync.models.Mural;
import com.CondoSync.models.DTOs.MuralDTO;
import com.CondoSync.repositores.MuralRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MuralService {

  @Autowired
  private MuralRepository muralRepository;

  public Mural save(Mural mural) {
    return muralRepository.save(mural);
  }

  public Mural save(MuralDTO muralDTO) {

    var mural = new Mural();

    var imageMural = new ImageMural();

    mural.setTitle(muralDTO.getTitle());
    mural.setDescription(muralDTO.getDescription());
    mural.setStatus(true);

    imageMural.setBase64(muralDTO.getImage().getBase64());
    imageMural.setName(muralDTO.getImage().getName());
    imageMural.setDefaultImg(muralDTO.getImage().getDefaultImg());
    imageMural.setDefaultImg(true);

    mural.setImage(imageMural);
    imageMural.setMural(mural);

    return muralRepository.save(mural);
  }

  public Mural findById(Integer id) {
    return muralRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("Mural não encontrado com o id: " + id));

  }

  public void deleteById(Integer id) {

    var mural = findById(id);
    muralRepository.delete(mural);
  }

  public Mural update(Mural muralSave) {

    var mural = findById(muralSave.getId());
    mural.setTitle(muralSave.getTitle());
    mural.setDescription(muralSave.getDescription());
    mural.setStatus(muralSave.isStatus());
    mural.setImage(muralSave.getImage());

    return muralRepository.save(mural);
  }

  public Iterable<MuralDTO> findAll() {
    return muralRepository.findAll().stream().map(MuralDTO::new).toList();
  }

}
