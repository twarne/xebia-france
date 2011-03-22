package com.xebia.service;

import com.xebia.domain.Devise;
import com.xebia.domain.ProduitFinancier;
import com.xebia.domain.Tirage;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

/**
 * Created by IntelliJ IDEA.
 * User: Nicolas Jozwiak
 */
public class FinanceServiceTest {

    private List<ProduitFinancier> produitsFinanciers;
    private FinanceService financeService = new FinanceService();


    private List<ProduitFinancier> genereProduitsFinanciers() {

        List<Tirage> tirages = new LinkedList<Tirage>();
        tirages.add(new Tirage(1, "tirage_1", 25000.0, Devise.EUR));
        tirages.add(new Tirage(2, "tirage_2", 15000.0, Devise.USD));
        tirages.add(new Tirage(3, "tirage_3", 20000.0, Devise.EUR));
        tirages.add(new Tirage(4, "tirage_4", 50000.0, Devise.USD));

        produitsFinanciers = new LinkedList<ProduitFinancier>();
        produitsFinanciers.add(new ProduitFinancier(1, "reference_1", "Xebia", 1000000.0, Devise.EUR, tirages));
        produitsFinanciers.add(new ProduitFinancier(2, "reference_2", "Xebia", 1500000.0, Devise.USD, tirages));

        return produitsFinanciers;
    }

    @Before
    public void init() {
        produitsFinanciers = genereProduitsFinanciers();
    }

    @Test
    public void testGetProduitsFinanciersParDevise() {
        List<ProduitFinancier> produitsParDevise = financeService.getProduitsFinanciersParDevise(produitsFinanciers, Devise.EUR);

        assertThat(produitsParDevise).hasSize(1).onProperty("reference").contains("reference_1");
    }

    @Test
    public void testGetTotalMontantParDevise() throws Exception {
        Map<Devise, Double> totalMontantParDevise = financeService.getTotalMontantParDevise(produitsFinanciers);

        assertThat(totalMontantParDevise).hasSize(2).includes(entry(Devise.EUR, 90000.0), entry(Devise.USD, 130000.0));
    }
}
