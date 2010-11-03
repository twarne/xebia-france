package fr.xebia.exercice.procedural;

import java.util.Date;

class Option implements Titre {

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
    public TypeTitre getTypeTitre() {
        return TypeTitre.Option;
    }
}
