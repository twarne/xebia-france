package fr.xebia.exercice.procedural;

import org.springframework.beans.factory.annotation.Autowired;

public class WebControllerVersionProcedurale {

    @Autowired
    private PortfolioService portfolioService;

    public double valorisePortfolio(Integer idPortfolio) {
        return portfolioService.valorise(idPortfolio);
    }
}
