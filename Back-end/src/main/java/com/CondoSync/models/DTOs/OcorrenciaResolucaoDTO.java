package com.CondoSync.models.DTOs;

import com.CondoSync.models.StatusOcorrencia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OcorrenciaResolucaoDTO {

    private Integer id;
    private String resolution;
    private StatusOcorrencia status;

}
