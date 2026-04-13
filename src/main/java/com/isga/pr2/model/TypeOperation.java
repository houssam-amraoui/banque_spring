package com.isga.pr2.model;

public enum TypeOperation {
    DEPOT("Dépôt"),
    RETRAIT("Retrait");

    private final String libelle;

    TypeOperation(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
