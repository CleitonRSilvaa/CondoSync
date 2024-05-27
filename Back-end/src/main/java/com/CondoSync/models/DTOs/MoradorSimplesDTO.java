package com.CondoSync.models.DTOs;

import java.util.UUID;

import com.CondoSync.models.Morador;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MoradorSimplesDTO {

    private UUID id;

    private String cpf;

    private String rg;

    private String bloco;

    private String apartamento;

    private Integer andar;

    private String torre;

    private String celular;

    private String email;

    private String nomeCompleto;

    public MoradorSimplesDTO(Morador morador) {
        this.id = morador.getId();
        this.cpf = morador.getCpf();
        this.bloco = morador.getBloco();
        this.apartamento = morador.getApartamento();
        this.celular = morador.getCelular();
        this.email = morador.getEmail();
        this.nomeCompleto = morador.getNome();
        this.torre = morador.getTorre();
        this.rg = morador.getRg();
    }

}
