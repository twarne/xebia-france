package fr.xebia.exercice.spring;

import org.springframework.beans.factory.annotation.Autowired;

public class WebControllerVersionSpring {

    @Autowired
    private PortfolioRepository portfolioRepository;

    public double valorisePortfolio(Integer idPortfolio) {
        Portfolio portfolio = portfolioRepository.load(idPortfolio);
        return portfolio.valorise();
    }
}
