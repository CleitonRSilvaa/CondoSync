package com.CondoSync.models;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity()
@Table(name = "moradores")
public final class Morador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "morador_id")
    private UUID id;

    @NotBlank(message = "O CPF é obrigatorio")
    @Column(length = 11, nullable = false, unique = true)
    private String cpf;

    @Column(length = 5, nullable = true)
    private String bloco;

    @NotBlank(message = "O apartamento é obrigatorio")
    @Column(length = 11, nullable = false)
    private String apartamento;

    @Column(length = 11, nullable = true)
    private String celular;

    @Email
    @Column(length = 100, nullable = false, unique = true)
    @NotBlank(message = "O email é obrigatorio")
    private String email;

    @NotBlank(message = "O nome é obrigatorio")
    @Column(length = 100, nullable = false)
    private String nome;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "morador", fetch = FetchType.LAZY)
    private List<ReservaMorador> reservas;

    @OneToMany(mappedBy = "morador", fetch = FetchType.LAZY)
    private List<OcorrenciaMorador> ocorrencias;

    @CreationTimestamp
    private Instant creation;

    @UpdateTimestamp
    private Instant upudate;

    public Set<Long> getRolesIds() {
        Set<Long> rolesIds = new HashSet<>();
        for (Role role : user.getRoles()) {
            rolesIds.add(role.getId());
        }
        return rolesIds;
    }

    @Override
    public String toString() {
        return "Morador{" +
                "id=" + id +
                ", cpf='" + cpf + '\'' +
                ", bloco='" + bloco + '\'' +
                ", apartamento='" + apartamento + '\'' +
                ", celular='" + celular + '\'' +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", user=" + user +
                ", reservas=" + reservas +
                ", ocorrencias=" + ocorrencias +
                ", creation=" + creation +
                ", upudate=" + upudate +
                '}';
    }
}
