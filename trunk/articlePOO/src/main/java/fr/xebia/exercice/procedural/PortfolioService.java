/*
 * Copyright 2008-2009 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
