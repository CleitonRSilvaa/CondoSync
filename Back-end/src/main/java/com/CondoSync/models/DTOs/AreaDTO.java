package com.CondoSync.models.DTOs;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AreaDTO {

    private UUID id;

    @NotBlank(message = "A nome é obrigatorio")
    @Size(min = 5, max = 100, message = "O nome deve ter entre 5 e 100 caracteres")
    private String name;

    @NotBlank(message = "A descrição é obrigatoria")
    @Size(min = 5, max = 100, message = "A descrição deve ter entre 5 e 100 caracteres")
    private String description;

    @NotNull(message = "O preço é obrigatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
    private BigDecimal price;

    private List<Image> images;

    public record Image(String path, String name) {
    }
}
