package fr.xebia.exercice.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Portfolio {

    private List<Titre> titres = new ArrayList<Titre>();

    public Portfolio(Titre... titres) {
        this.titres = Arrays.asList(titres);
    }

    public double valorise() {
        double val = 0;
        for (Titre titre : titres) {
            val += titre.valorise();
        }
        return val;
    }
}
