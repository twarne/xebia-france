package com.xebia.service;

import com.xebia.domain.Devise;
import com.xebia.domain.ProduitFinancier;
import com.xebia.domain.Tirage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nicolas Jozwiak
 */
public class FinanceService {


    /**
     * @param produitsFinanciers la liste des produits financiers sur lesquels nous voulons récupérer les produits par devise.
     * @param devise             la devise pour laquelle nous souhaitons récupérer les produits financiers
     * @return la liste des produits financiers pour la devise concernée. Liste vide si pas de produits pour la devise.
     */
    public List<ProduitFinancier> getProduitsFinanciersParDevise(List<ProduitFinancier> produitsFinanciers, Devise devise) {
        List<ProduitFinancier> listProduitParDevise = new LinkedList<ProduitFinancier>();
        for (ProduitFinancier produitFinancier : produitsFinanciers) {
            if (produitFinancier.getDevise().equals(devise)) {
                listProduitParDevise.add(produitFinancier);
            }
        }

        return listProduitParDevise;

    }

    /**
     * @param produitsFinanciers la liste des produits financiers pour lesquels nous récuperons les tirages.
     * @return une map avec le total des montants des tirages de tous les produits financiers par devise.
     */
    public Map<Devise, Double> getTotalMontantParDevise(List<ProduitFinancier> produitsFinanciers) {
        Map<Devise, Double> mapMontantParDevise = new HashMap<Devise, Double>();

        for (ProduitFinancier produitFinancier : produitsFinanciers) {
            List<Tirage> listTirages = produitFinancier.getTirages();
            for (Tirage tirage : listTirages) {
                Devise devise = tirage.getDevise();
                Double montantCourant = tirage.getMontant();
                if (!mapMontantParDevise.containsKey(devise)) {
                    mapMontantParDevise.put(devise, montantCourant);
                } else {
                    Double montantTotal = mapMontantParDevise.get(devise);
                    mapMontantParDevise.put(devise, montantCourant + montantTotal);
                }
            }
        }

        return mapMontantParDevise;
    }
}
