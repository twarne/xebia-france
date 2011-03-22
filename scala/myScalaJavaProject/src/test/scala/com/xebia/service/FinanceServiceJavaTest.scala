package com.xebia.service

import com.xebia.domain.{Tirage, ProduitFinancier, Devise}
import org.scalatest.junit.{AssertionsForJUnit, ShouldMatchersForJUnit}
import java.util.LinkedList
import org.junit._

@Test
class FinanceServiceJavaTest extends ShouldMatchersForJUnit {
  var produitsFinanciers = new LinkedList[ProduitFinancier]();
  var financeService = new FinanceService();

  @Before
  def init() {
    genereProduitsFinanciers();
  }

  def genereProduitsFinanciers() {

    var tirages = new LinkedList[Tirage]();
    tirages.add(new Tirage(1, "tirage_1", 25000.0, Devise.EUR));
    tirages.add(new Tirage(2, "tirage_2", 15000.0, Devise.USD));
    tirages.add(new Tirage(3, "tirage_3", 20000.0, Devise.EUR));
    tirages.add(new Tirage(4, "tirage_4", 50000.0, Devise.USD));

    produitsFinanciers.add(new ProduitFinancier(1, "reference_1", "Xebia", 1000000.0, Devise.EUR, tirages));
    produitsFinanciers.add(new ProduitFinancier(2, "reference_2", "Xebia", 1500000.0, Devise.USD, tirages));
  }

  @Test
  def testGetProduitsFinanciersParDevise() {
    var financiersParDevise = financeService.getProduitsFinanciersParDevise(produitsFinanciers, Devise.EUR);

    financiersParDevise should have size (1);

    financiersParDevise.get(0).getReference should be("reference_1");
  }

  @Test
  def testGetTotalMontantParDevise() {
    val totalMontantParDevise = financeService.getTotalMontantParDevise(produitsFinanciers);

    totalMontantParDevise should have size (2);

    totalMontantParDevise should contain key (Devise.EUR);
    totalMontantParDevise should contain value (90000.0);

    totalMontantParDevise should contain key (Devise.USD);
    totalMontantParDevise should contain value (130000.0);

  }
}