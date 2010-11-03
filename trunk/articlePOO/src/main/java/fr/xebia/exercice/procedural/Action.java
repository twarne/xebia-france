package fr.xebia.exercice.procedural;

class Action implements Titre {

    final Integer id;

    public Action(Integer id) {
        this.id = id;
    }

    @Override
    public TypeTitre getTypeTitre() {
        return TypeTitre.Action;
    }
}
