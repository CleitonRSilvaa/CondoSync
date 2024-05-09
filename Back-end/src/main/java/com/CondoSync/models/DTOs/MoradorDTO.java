package com.CondoSync.models.DTOs;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MoradorDTO {

    @NotBlank(message = "O CPF é obrigatorio")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres")
    private String cpf;

    @Size(min = 1, max = 1, message = "O bloco deve ter 1 caracter")
    private String bloco;

    @Size(min = 1, max = 3, message = "O apartamento deve ter entre 2 e 5 caracteres")
    private String apartamento;

    private String celular;

    @Email(message = "O email deve ser valido")
    @NotBlank(message = "O email é obrigatorio")
    @Size(min = 5, max = 100, message = "O email deve ter entre 5 e 100 caracteres")
    private String email;

    @NotBlank(message = "O nome é obrigatorio")
    @Size(min = 5, max = 100, message = "O nome deve ter entre 5 e 100 caracteres")
    private String nome;

    @NotBlank(message = "A senha é obrigatorio")
    @Size(min = 5, max = 100, message = "A senha deve ter entre 5 e 100 caracteres")
    private String senha;

    private List<Integer> rolesIds;

    public void setRolesIds(List<Integer> rolesIds) {
        for (Integer id : rolesIds) {
            if (id != null && id >= 0) {
                this.rolesIds.add(id);
            } else {
                throw new IllegalArgumentException("Role ID não pode ser negativo.");
            }
        }
    }

    public void addRoleId(Integer roleId) {
        if (roleId != null && roleId >= 0) {
            rolesIds.add(roleId);
        } else {
            throw new IllegalArgumentException("Role ID não pode ser negativo.");
        }
    }

}
