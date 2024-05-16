package com.CondoSync.models.DTOs;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.validator.constraints.br.CPF;

import com.CondoSync.models.Morador;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MoradorDTO {

    private UUID id;

    @CPF(message = "O CPF deve ser valido")
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

    @NotBlank(message = "O nome completo é obrigatorio")
    @Size(min = 5, max = 100, message = "O nome completo deve ter entre 5 e 100 caracteres")
    private String nomeCompleto;

    @NotBlank(message = "A senha é obrigatorio")
    @Size(min = 5, max = 100, message = "A senha deve ter entre 5 e 100 caracteres")
    private String senha;

    @NotBlank(message = "A confirmação de senha é obrigatorio")
    @Size(min = 5, max = 100, message = "A confirmação de senha deve ter entre 5 e 100 caracteres")
    private String confirmacaoSenha;

    @NotEmpty(message = "rolesIds não pode ser nulo ou vazio.")
    private Set<Long> rolesIds;

    public void addRoleId(Long roleId) {
        if (roleId != null && roleId > 0) {
            this.rolesIds.add(roleId);
        } else {
            throw new IllegalArgumentException("Role ID não pode ser negativo.");
        }
    }

    public void validateRolesIds() {
        for (Long id : this.rolesIds) {
            if (id == null || id < 1) {
                throw new IllegalArgumentException("Role ID deve ser positivo e maior que zero.");
            }

        }
    }

    @JsonIgnore
    public boolean isSenhaValida() {
        return this.senha.equals(this.confirmacaoSenha);
    }

    public MoradorDTO(Morador morador) {
        this.id = morador.getId();
        this.cpf = morador.getCpf();
        this.bloco = morador.getBloco();
        this.apartamento = morador.getApartamento();
        this.celular = morador.getCelular();
        this.email = morador.getEmail();
        this.nomeCompleto = morador.getNome();
        this.rolesIds = morador.getRolesIds();
    }

}
