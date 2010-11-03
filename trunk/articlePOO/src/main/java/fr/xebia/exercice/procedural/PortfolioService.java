package fr.xebia.exercice.procedural;

import fr.xebia.exercice.MarketdataRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private MarketdataRepository marketdataRepository;

    public double valorise(Integer idPortfolio) {
        Portfolio portfolio = portfolioRepository.load(idPortfolio);

        double valoPortfolio = 0;
        for (Titre titre : portfolio.getTitres()) {
            valoPortfolio += valorise(titre);
        }

        return valoPortfolio;
    }

    public double valorise(Titre titre) {

        switch (titre.getTypeTitre()) {
            case Action:
                return valoriseAction((Action)titre);
            case Option:
                return valoriseOption((Option)titre);
            default:
                throw new IllegalArgumentException("Type de titre non géré: " + titre.getTypeTitre());
        }
    }

    private double valoriseOption(Option option) {
        double vol = marketdataRepository.getVolatilite(option.sousJacent.id);
        double taux = marketdataRepository.getTaux(option.id);
        return valoriseOption(option.dateExercice, option.prixExercice, option.sousJacent, vol, taux);
    }

    private double valoriseOption(Date dateExercice, double prixExercice, Action sousJacent, double vol, double taux) {
        // implementation de la valo
        return 0;
    }

    private double valoriseAction(Action action) {
        return marketdataRepository.getFixing(action.id);
    }
}
