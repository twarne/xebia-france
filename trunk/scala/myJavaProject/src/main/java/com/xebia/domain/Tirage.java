package com.xebia.domain;

/**
 * Created by IntelliJ IDEA.
 * User: Nicolas Jozwiak
 */
public class Tirage {

    private Integer id;
    private String nom;
    private Double montant;
    private Devise devise;

    public Tirage(Integer id, String nom, Double montant, Devise devise) {
        this.id = id;
        this.nom = nom;
        this.montant = montant;
        this.devise = devise;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Devise getDevise() {
        return devise;
    }

    public void setDevise(Devise devise) {
        this.devise = devise;
    }
}
