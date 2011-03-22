package com.xebia.service

import collection.mutable.{HashMap, Buffer}
import scala.collection.JavaConversions._
import com.xebia.domain.{Tirage, Devise, ProduitFinancier}

/**
 * Created by IntelliJ IDEA.
 * User: Nicolas Jozwiak
 */

class FinanceServiceScala {

  /**
   * Retourne la liste des produits financiers pour la devise concernée.
   */
  def getProduitsFinanciersParDevise(produitsFinanciers: java.util.List[ProduitFinancier], devise: Devise) = {
    produitsFinanciers filter (_.getDevise equals devise);
  }

  /**
   * Retourne une map avec le total des montants des tirages de tous les produits financiers par devise.
   */
  def getTotalMontantParDevise(produitsFinanciers: java.util.List[ProduitFinancier]) = {

    def sommeTirage(tirages: Iterable[Tirage]): Double = tirages match {
      case head :: tail => head.getMontant.doubleValue + sommeTirage(tail)
      case _ => 0.0
    }

    var mapResultat = new HashMap[Devise, Double];

    def mapDeviseMontant(mapTirage: Map[Devise, Double]) = {

      mapTirage foreach {m => mapResultat getOrElseUpdate (m._1, mapTirage.getOrElse(m._1, 0.0) + m._2.doubleValue)}
    }

    for (produitFinancier <- asScalaIterable(produitsFinanciers);
         mapTirages = asScalaIterable(produitFinancier.getTirages) groupBy (_.getDevise) mapValues {tirage => sommeTirage(tirage)}
    ) yield mapDeviseMontant(mapTirages);


    mapResultat;
  }

}