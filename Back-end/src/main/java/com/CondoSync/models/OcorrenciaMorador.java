package com.CondoSync.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ocorrencias_moradores")
public class OcorrenciaMorador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ocorrencia_morador_id")
    private Integer id;

    @NotNull(message = "O morador é obrigatorio")
    @ManyToOne
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador morador;

    @NotNull(message = "A ocorrências é obrigatorio")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ocorrencia_id", nullable = false)
    private Ocorrencia ocorrencia;

}
