package fr.xebia.exercice.procedural;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Portfolio {

    private List<Titre> titres = new ArrayList<Titre>();

    public Portfolio(Titre... titres) {
        this.titres = Arrays.asList(titres);
    }

    public Iterable<Titre> getTitres() {
        return titres;
    }
}
