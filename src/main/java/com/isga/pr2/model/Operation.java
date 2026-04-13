package com.isga.pr2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le type d'opération est obligatoire")
    private TypeOperation type;

    @Min(value = 1, message = "Le montant doit être supérieur à 0")
    private double montant;

    private String description;

    private LocalDateTime dateOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id", nullable = false)
    @ToString.Exclude
    private ComptesBancaires compte;

    @PrePersist
    public void prePersist() {
        if (dateOperation == null) {
            dateOperation = LocalDateTime.now();
        }
    }
}
