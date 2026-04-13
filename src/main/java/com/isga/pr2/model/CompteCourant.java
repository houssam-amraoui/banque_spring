package com.isga.pr2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("COURANT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompteCourant extends ComptesBancaires {

    private double decouvertAutorise;

    public CompteCourant(String numeroCompte, double solde, java.time.LocalDate dateCreation,
                         Client client, double decouvertAutorise) {
        super(numeroCompte, solde, dateCreation, client, new java.util.ArrayList<>());
        this.decouvertAutorise = decouvertAutorise;
    }

    @Override
    public String getTypeCompte() {
        return "Compte Courant";
    }

    @Override
    public String getInfosSpecifiques() {
        return "Découvert autorisé : " + String.format("%.2f", decouvertAutorise) + " MAD";
    }
}
