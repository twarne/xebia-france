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

import fr.xebia.exercice.ActionValorisateur;
import fr.xebia.exercice.OptionValorisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ProceduralMarketdataRepository marketdataRepository;
    private final ActionValorisateur actionValorisateur;
    private final OptionValorisateur optionValorisateur;

    @Autowired
    PortfolioService(PortfolioRepository portfolioRepository, ProceduralMarketdataRepository marketdataRepository, ActionValorisateur actionValorisateur, OptionValorisateur optionValorisateur) {
        this.portfolioRepository = portfolioRepository;
        this.marketdataRepository = marketdataRepository;
        this.actionValorisateur = actionValorisateur;
        this.optionValorisateur = optionValorisateur;
    }

    public double valorise(Integer idPortfolio) {
        Portfolio portfolio = portfolioRepository.load(idPortfolio);

        double valoPortfolio = 0;
        for (Map.Entry<Titre, Integer> entry : portfolio.getTitres().entrySet()) {
            Titre titre = entry.getKey();
            Integer nb = entry.getValue();
            valoPortfolio += nb * valorise(titre);
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
        double prixAction = marketdataRepository.getFixing(option.sousJacent.id);
        double valoAction = actionValorisateur.valoriseAction(prixAction);

        double vol = marketdataRepository.getVolatilite(option.sousJacent.id);
        double taux = marketdataRepository.getTaux(option.id);
        return optionValorisateur.valorise(option.dateExercice, option.prixExercice, valoAction, vol, taux);
    }

    private double valoriseAction(Action action) {
        double prixAction = marketdataRepository.getFixing(action.id);
        return actionValorisateur.valoriseAction(prixAction);
    }
}
