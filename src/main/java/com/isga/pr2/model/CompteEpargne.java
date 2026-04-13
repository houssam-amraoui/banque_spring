package com.isga.pr2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("EPARGNE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompteEpargne extends ComptesBancaires {

    private double tauxInteret;

    public CompteEpargne(String numeroCompte, double solde, java.time.LocalDate dateCreation,
                         Client client, double tauxInteret) {
        super(numeroCompte, solde, dateCreation, client, new java.util.ArrayList<>());
        this.tauxInteret = tauxInteret;
    }

    @Override
    public String getTypeCompte() {
        return "Compte Épargne";
    }

    @Override
    public String getInfosSpecifiques() {
        return "Taux d'intérêt : " + String.format("%.2f", tauxInteret) + "%";
    }
}
