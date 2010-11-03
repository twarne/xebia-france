package fr.xebia.exercice.spring;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class PortfolioRepository {

    private Map<Integer, Portfolio> portfolios = new HashMap<Integer, Portfolio>() {{
        put(1, new Portfolio(new Action(1), new Option(2, 1000, new Date(), new Action(3))));
    }};

    public Portfolio load(Integer idPortfolio) {
        return new Portfolio(new Action(1), new Option(2, 1000, new Date(), new Action(3)));
//        return portfolios.get(idPortfolio);
    }
}
