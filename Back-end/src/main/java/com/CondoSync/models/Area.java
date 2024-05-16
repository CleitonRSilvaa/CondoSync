package com.CondoSync.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
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
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "area_id", nullable = false)
    private UUID id;

    @NotBlank(message = "O nome é obrigatorio")
    private String name;

    @NotBlank(message = "O descrição é obrigatoria")
    private String description;

    @NotNull(message = "O preço é obrigatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
    private BigDecimal price;

    private boolean status;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    private List<Image> images;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    private List<Horario> horarios;

    @CreationTimestamp
    private Instant creation;

    @UpdateTimestamp
    private Instant upudate;

    @Override
    public String toString() {
        return "Area [creation=" + creation + ", description=" + description + ", horarios=" + horarios + ", id=" + id
                + ", images=" + images + ", name=" + name + ", price=" + price + ", status=" + status + ", upudate="
                + upudate + "]";
    }

}
