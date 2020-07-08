package org.slos.splinterlands.collection;

import org.slos.domain.RealizedCollectionCard;

import java.util.Comparator;

public class LevelComparator implements Comparator<RealizedCollectionCard> {
    @Override
    public int compare(RealizedCollectionCard cardOne, RealizedCollectionCard cardTwo) {
        if (cardOne.getCollectionCard().getLevel() > cardTwo.getCollectionCard().getLevel()) {
            return 1;
        }
        if (cardOne.getCollectionCard().getLevel() < cardTwo.getCollectionCard().getLevel()) {
            return -1;
        }

        return 0;
    }
}