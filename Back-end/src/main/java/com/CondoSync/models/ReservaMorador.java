package com.CondoSync.models;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "reservas_moradores")
public class ReservaMorador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_morador_id")
    private Integer id;

    @NotNull(message = "O morador é obrigatorio")
    @ManyToOne
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador morador;

    @NotNull(message = "A reserva é obrigatorio")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @NotNull(message = "A area é obrigatorio")
    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Override
    public String toString() {
        return "ReservaMorador [id=" + id + ", morador=" + morador.getEmail() + ", reserva=" + reserva.getId()
                + ", area=" + area + "]";
    }

}
