package com.CondoSync.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdatePasswordDTO {

  @NotBlank(message = "Senha atual é obrigatória")
  @Size(min = 6, max = 20, message = "Senha atual deve ter entre 6 e 20 caracteres")
  private String senhaAtual;
  @Size(min = 6, max = 20, message = "Nova senha deve ter entre 6 e 20 caracteres")
  @NotBlank(message = "Nova senha é obrigatória")
  private String novaSenha;
  @NotBlank(message = "Confirmação da nova senha é obrigatória")
  @Size(min = 6, max = 20, message = "Confirmação da nova senha deve ter entre 6 e 20 caracteres")
  private String confirmacaoNovaSenha;

}
