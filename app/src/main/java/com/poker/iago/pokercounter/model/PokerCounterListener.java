package com.poker.iago.pokercounter.model;


public interface PokerCounterListener {

    void updateCounter();

    void levelFinish();

    void levelChange();

}
