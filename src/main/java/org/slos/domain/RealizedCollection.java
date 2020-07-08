package org.slos.domain;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RealizedCollection {
    private String player;
    private List<RealizedCollectionCard> cards;
    private Set<Integer> cardOwnershipSet = new HashSet<>();

    public Optional<RealizedCollectionCard> getHighestLevelCardOf(Integer id) {
        List<RealizedCollectionCard> cardsOfMonster = cards.stream().filter(realizedCollectionCard -> realizedCollectionCard.getCollectionCard().getMonsterId().equals(id)).collect(Collectors.toList());

        if ((cardsOfMonster == null) || (cardsOfMonster.size() == 0)) {
            return Optional.empty();
        }

        Optional<RealizedCollectionCard> maxCardByLevel = cardsOfMonster.stream().max(new RealizedCollectionByLevelComparator());

        return maxCardByLevel;
    }

    public RealizedCollection(String player, List<RealizedCollectionCard> cards) {
        this.player = player;
        this.cards = cards;

        cards.stream()
                .forEach(card -> cardOwnershipSet.add(card.getCollectionCard().getMonsterId()));
    }

    public String getPlayer() {
        return player;
    }

    public List<RealizedCollectionCard> getCards() {
        return cards;
    }

    public boolean hasCard(Integer id) {
        return cardOwnershipSet.contains(id);
    }
    
    @Override
    public String toString() {
        return "RealizedCollection{" +
                "player='" + player + '\'' +
                ", cards=" + cards +
                '}';
    }
}

class RealizedCollectionByLevelComparator implements Comparator<RealizedCollectionCard> {
    @Override
    public int compare(RealizedCollectionCard o1, RealizedCollectionCard o2) {
        return o2.getCollectionCard().getLevel().compareTo(o1.getCollectionCard().getLevel());
    }
}
