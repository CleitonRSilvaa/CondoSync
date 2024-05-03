package com.CondoSync.models.DTOs;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserSubscriptionDTO {

    @NotBlank(message = "O id do usuario é obrigatorio")
    private UUID user_id;

    @NotBlank(message = "O endpoint é obrigatorio")
    @Column(unique = true)
    private String endpoint;

    private String expirationTime;

    @NotBlank(message = "As keys são obrigatorio")
    private Keys keys;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Keys {
        @NotBlank(message = "O p256dh é obrigatorio")
        private String p256dh;
        @NotBlank(message = "O auth é obrigatorio")
        private String auth;
    }

}
