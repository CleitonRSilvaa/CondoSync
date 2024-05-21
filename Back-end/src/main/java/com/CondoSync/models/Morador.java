package com.CondoSync.models;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.CondoSync.components.ValidateUserException;
import com.CondoSync.models.DTOs.MoradorDTO;

import jakarta.persistence.CascadeType;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "O RG é obrigatorio")
    @Column(length = 9, nullable = true)
    @Size(min = 9, max = 9, message = "O RG deve ter 9 caracteres")
    private String rg;

    @NotNull(message = "A data de nascimento é obrigatorio")
    @Column(nullable = false)
    private Date dataNascimento;

    @Column(length = 5, nullable = true)
    private String bloco;

    @NotBlank(message = "O apartamento é obrigatorio")
    @Column(length = 11, nullable = false)
    private String apartamento;

    @Size(max = 20, message = "a torre deve ser menor que 20")
    private String torre;

    @Column(length = 11, nullable = true)
    private String celular;

    @Email
    @Column(length = 100, nullable = false, unique = true)
    @NotBlank(message = "O email é obrigatorio")
    private String email;

    @NotBlank(message = "O nome é obrigatorio")
    @Column(length = 100, nullable = false)
    private String nome;

    @Column(nullable = false, columnDefinition = "boolean default true", name = "status")
    @ColumnDefault("true")
    private boolean ativo;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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

    public Morador(MoradorDTO moradorDTO) {
        this.cpf = moradorDTO.getCpf();
        this.rg = moradorDTO.getRg();
        this.bloco = moradorDTO.getBloco();
        this.dataNascimento = moradorDTO.getDataNascimento();
        this.apartamento = moradorDTO.getApartamento();
        this.torre = moradorDTO.getTorre();
        this.celular = moradorDTO.getCelular();
        this.email = moradorDTO.getEmail();
        this.nome = moradorDTO.getNomeCompleto();
        this.ativo = moradorDTO.isStatus();
    }

    public Set<Long> getRolesIds() {
        Set<Long> rolesIds = new HashSet<>();
        for (Role role : user.getRoles()) {
            rolesIds.add(role.getId());
        }
        return rolesIds;
    }

    public void setDataNascimento(Date dataNascimento) {
        if (dataNascimento == null) {
            throw new ValidateUserException("A data de nascimento não pode ser nula", null);
        }
        LocalDate dataNasc = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dataNascimento));
        LocalDate hoje = LocalDate.now();
        Period idade = Period.between(dataNasc, hoje);

        if (idade.getYears() < 18) {
            throw new ValidateUserException("A pessoa deve ter pelo menos 18 anos completos.", null);
        }
        this.dataNascimento = dataNascimento;
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
