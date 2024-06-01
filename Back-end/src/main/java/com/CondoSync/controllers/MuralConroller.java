package com.CondoSync.controllers;

import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CondoSync.models.Mural;
import com.CondoSync.models.DTOs.MuralDTO;
import com.CondoSync.services.MuralService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/mural")
@Validated
public class MuralConroller {

  @Autowired
  private MuralService muralService;

  @PostMapping("/save")
  public ResponseEntity<Mural> save(@RequestBody @Valid MuralDTO mural) {
    return ResponseEntity.ok(muralService.save(mural));
  }

  @GetMapping("/list")
  public ResponseEntity<?> list() {
    return ResponseEntity.ok(muralService.findAll());
  }

  @GetMapping("/findById/{id}")
  public ResponseEntity<?> findById(@PathVariable Integer id) {
    return ResponseEntity.ok(muralService.findById(id));
  }

  @PostMapping("/deleteById/{id}")
  public ResponseEntity<?> deleteById(@PathVariable Integer id) {
    muralService.deleteById(id);
    return ResponseEntity.ok().build();
  }

}
