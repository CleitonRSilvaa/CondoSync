package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CondoSync.models.ReservaMorador;
import com.CondoSync.repositores.ReservaMoradorRepository;

import jakarta.validation.Valid;

@Service
public class ReservaMoradorService {

    @Autowired
    private ReservaMoradorRepository reservaMoradorRepository;

    public ReservaMorador save(@Valid ReservaMorador reservaMorador) {

        return reservaMoradorRepository.save(reservaMorador);
    }

}
