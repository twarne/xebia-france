package fr.xebia.exercice.spring;

import fr.xebia.exercice.MarketdataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
class Action implements Titre {

    @Autowired
    MarketdataRepository marketdataRepository;

    final Integer id;

    public Action(Integer id) {
        this.id = id;
    }

    @Override
    public double valorise() {
        return marketdataRepository.getFixing(id);
    }
}
