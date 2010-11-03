package fr.xebia.exercice.spring;

import fr.xebia.exercice.MarketdataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;

@Configurable
class Option implements Titre {

    @Autowired
    MarketdataRepository marketdataRepository;

    final Integer id;
    final Date dateExercice;
    final double prixExercice;
    final Action sousJacent;

    public Option(Integer id, double prixExercice, Date dateExercice, Action sousJacent) {
        this.id = id;
        this.prixExercice = prixExercice;
        this.dateExercice = dateExercice;
        this.sousJacent = sousJacent;
    }

    @Override
    public double valorise() {

        double vol = marketdataRepository.getVolatilite(sousJacent.id);
        double taux = marketdataRepository.getTaux(this.id);
        return valorise(dateExercice, prixExercice, sousJacent, vol, taux);
    }

    private double valorise(Date dateExercice, double prixExercice, Action sousJacent, double vol, double taux) {
        // implementation de la valo
        return 0;
    }
}
