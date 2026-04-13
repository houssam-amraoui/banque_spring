package com.isga.pr2.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comptes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_compte", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ComptesBancaires {

    @Id
    @Column(name = "numero_compte", length = 20)
    private String numeroCompte;

    private double solde;

    private LocalDate dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    private Client client;

    @OneToMany(mappedBy = "compte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Operation> operations = new ArrayList<>();

    // Retourne le type lisible du compte
    public abstract String getTypeCompte();

    // Retourne les infos spécifiques au type
    public abstract String getInfosSpecifiques();

    public double calculerSolde() {
        return operations.stream()
                .mapToDouble(op -> op.getType() == TypeOperation.DEPOT ? op.getMontant() : -op.getMontant())
                .sum() + getSoldeInitial();
    }

    protected double getSoldeInitial() {
        return solde;
    }
}
