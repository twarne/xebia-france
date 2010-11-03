package fr.xebia.exercice.procedural;

interface Titre {

    TypeTitre getTypeTitre();

    enum TypeTitre {
        Action, Option
    }
}
