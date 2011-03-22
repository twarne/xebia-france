package com.xebia.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nicolas Jozwiak
 */
public class ProduitFinancier {

    private Integer id;
    private String reference;
    private String nomBanque;
    private Double montantPlafond;
    private Devise devise;
    private List<Tirage> tirages = new LinkedList<Tirage>();

    public ProduitFinancier(Integer id, String reference, String nomBanque, Double montantPlafond, Devise devise, List<Tirage> tirages) {
        this.id = id;
        this.reference = reference;
        this.nomBanque = nomBanque;
        this.montantPlafond = montantPlafond;
        this.devise = devise;
        this.tirages = tirages;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNomBanque() {
        return nomBanque;
    }

    public void setNomBanque(String nomBanque) {
        this.nomBanque = nomBanque;
    }

    public Double getMontantPlafond() {
        return montantPlafond;
    }

    public void setMontantPlafond(Double montantPlafond) {
        this.montantPlafond = montantPlafond;
    }

    public Devise getDevise() {
        return devise;
    }

    public void setDevise(Devise devise) {
        this.devise = devise;
    }

    public List<Tirage> getTirages() {
        return tirages;
    }

    public void setTirages(List<Tirage> tirages) {
        this.tirages = tirages;
    }
}
