package com.CondoSync.models.DTOs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.CondoSync.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioDTO {

  private UUID id;

  @NotBlank(message = "Email é obrigatório")
  @Size(max = 50, message = "Email deve ter no máximo 50 caracteres")
  @Email(message = "Email inválido")
  private String email;

  @NotBlank(message = "Nome completo é obrigatório")
  @Size(max = 150, message = "Nome completo deve ter no máximo 150 caracteres")
  private String nomeCompleto;

  private boolean status;

  // @NotBlank(message = "A senha é obrigatorio")
  // @Size(min = 5, max = 100, message = "A senha deve ter entre 5 e 100
  // caracteres")
  private String senha;

  // @NotBlank(message = "A confirmação de senha é obrigatorio")
  // @Size(min = 5, max = 100, message = "A confirmação de senha deve ter entre 5
  // e 100 caracteres")
  private String confirmacaoSenha;

  @NotEmpty(message = "rolesIds não pode ser nulo ou vazio.")
  private Set<Long> rolesIds;

  private HashMap<Long, String> roles;

  public UsuarioDTO(User user) {
    this.id = user.getId();
    this.email = user.getUsername();
    this.nomeCompleto = user.getFullName();
    this.status = user.isStatus();
    this.roles = new HashMap<>();
    this.rolesIds = new HashSet<>();
    user.getRoles().forEach(role -> {
      this.roles.put(role.getId(), role.getNome());
      this.rolesIds.add(role.getId());
    });

  }

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

}
