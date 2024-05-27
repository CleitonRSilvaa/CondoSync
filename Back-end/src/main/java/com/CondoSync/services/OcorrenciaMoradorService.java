package com.CondoSync.services;

import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;

import com.CondoSync.repositores.OcorrenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.CondoSync.models.Ocorrencia;
import com.CondoSync.models.OcorrenciaMorador;
import com.CondoSync.models.StatusOcorrencia;
import com.CondoSync.models.DTOs.OcorenciaDTO;
import com.CondoSync.models.DTOs.OcorrenciaResolucaoDTO;
import com.CondoSync.repositores.OcorrenciaMoradorRepository;

@Service
public class OcorrenciaMoradorService {

    @Autowired
    private OcorrenciaMoradorRepository ocorrenciaMoradorRepository;

    @Autowired
    private OcorrenciaRepository ocorrenciaRepository;

    @Autowired
    private MoradorService moradorService;

    public List<OcorenciaDTO> getOcorrenciaMorador(String moradorUserName) {

        var morador = moradorService.findMoradorByEmail(moradorUserName);

        var ocorrenciaMorador = ocorrenciaMoradorRepository.findByMorador_Id(morador.getId());

        var resultList = ocorrenciaMorador.stream()
                .map(OcorrenciaMorador::getOcorrencia)
                .map(OcorenciaDTO::new)
                .sorted(
                        (o1, o2) -> {
                            if (o1.getStatus().equals(StatusOcorrencia.ABERTO.getStatus())
                                    && !o2.getStatus().equals(StatusOcorrencia.ABERTO.getStatus())) {
                                return -1;
                            } else if (!o1.getStatus().equals(StatusOcorrencia.ABERTO.getStatus())
                                    && o2.getStatus().equals(StatusOcorrencia.ABERTO.getStatus())) {
                                return 1;
                            } else {
                                return o1.getCreation().compareTo(o2.getCreation());
                            }
                        })
                .collect(Collectors.toList());

        return resultList;
    }

    public ResponseEntity<?> getOcorrenciaMoradorById(Integer id) {
        return ResponseEntity.ok().body(ocorrenciaMoradorRepository.findById(id));
    }

    public ResponseEntity<?> createOcorrenciaMorador(Ocorrencia ocorrencia, String moradorUserName) {

        var morador = moradorService.findMoradorByEmail(moradorUserName);

        var ocorrenciaMorador = new OcorrenciaMorador();

        ocorrencia.setStatus(StatusOcorrencia.ABERTO);

        ocorrenciaMorador.setOcorrencia(ocorrencia);
        ocorrenciaMorador.setMorador(morador);

        return ResponseEntity.ok().body(ocorrenciaMoradorRepository.save(ocorrenciaMorador));
    }

    public ResponseEntity<?> createOcorrenciaMorador(OcorenciaDTO ocorenciaDTO, String moradorUserName) {

        var morador = moradorService.findMoradorByEmail(moradorUserName);

        var ocorrenciaMorador = new OcorrenciaMorador();

        var ocorrencia = new Ocorrencia();

        ocorrencia.setTitle(ocorenciaDTO.getTitle());
        ocorrencia.setDescription(ocorenciaDTO.getDescription());
        ocorrencia.setStatus(StatusOcorrencia.ABERTO);

        ocorrenciaMorador.setOcorrencia(ocorrencia);
        ocorrenciaMorador.setMorador(morador);

        System.out.println(morador);

        ocorrenciaMoradorRepository.save(ocorrenciaMorador);

        return ResponseEntity.ok().body("");
    }

    public ResponseEntity<?> cancelOcorrenciaMorador(Integer id, String moradorUserName) {
        OcorrenciaMorador ocorrenciaMorador = ocorrenciaMoradorRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Ocorrencia não encontrada!"));

        if (ocorrenciaMorador.getOcorrencia().getStatus().equals(StatusOcorrencia.CANCELADA)) {
            throw new IllegalArgumentException("Ocorrencia já cancelada");
        }

        if (ocorrenciaMorador.getOcorrencia().getStatus().equals(StatusOcorrencia.RESOLVIDA)) {
            throw new IllegalArgumentException("Ocorrencia já resolvida");
        }

        ocorrenciaMorador.getOcorrencia().setStatus(StatusOcorrencia.CANCELADA);
        ocorrenciaMoradorRepository.save(ocorrenciaMorador);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    public ResponseEntity<?> updateOcorrenciaMorador(Integer id, OcorrenciaMorador ocorrenciaMorador) {
        OcorrenciaMorador ocorrenciaMorador2 = ocorrenciaMoradorRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Ocorrencia não encontrada!"));
        ocorrenciaMorador2.setMorador(ocorrenciaMorador.getMorador());
        ocorrenciaMorador2.setOcorrencia(ocorrenciaMorador.getOcorrencia());
        return ResponseEntity.ok().body(ocorrenciaMoradorRepository.save(ocorrenciaMorador2));
    }

    public List<?> getAllOcorrenciaMorador() {
        var resultList = ocorrenciaMoradorRepository.findAll().stream()
                .map(OcorenciaDTO::new)
                .sorted(Comparator
                        .comparing((OcorenciaDTO o) -> o.getStatus().equals(StatusOcorrencia.ABERTO.getStatus()) ? 0
                                : o.getStatus().equals(StatusOcorrencia.EM_ANDAMENTO.getStatus()) ? 1 : 2)
                        .thenComparing(OcorenciaDTO::getCreation, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return resultList;
    }

    public ResponseEntity<?> updateOcorrenciaMoradorResolucao(Integer id, OcorrenciaResolucaoDTO ocorenciaDTO) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Ocorrencia não encontrada!"));

        if (!ocorrencia.getStatus().equals(StatusOcorrencia.ABERTO)
                && ocorenciaDTO.getStatus().equals(StatusOcorrencia.ABERTO)) {
            throw new IllegalArgumentException("Ocorrencia não pode voltar para aberto");
        }

        if (ocorrencia.getStatus().equals(StatusOcorrencia.ABERTO)
                && ocorenciaDTO.getStatus().equals(StatusOcorrencia.ABERTO)) {
            ocorrencia.setStatus(StatusOcorrencia.EM_ANDAMENTO);

        } else {
            ocorrencia.setStatus(ocorenciaDTO.getStatus());

        }

        ocorrencia.setResolution(ocorenciaDTO.getResolution());
        return ResponseEntity.ok().body(ocorrenciaRepository.save(ocorrencia));
    }

}
