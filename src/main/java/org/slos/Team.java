package org.slos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.util.ToJson;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team implements ToJson {
    private String id;
    private List<SimpleCard> cards;
    private ColorType colorType;
    private Float rank;
    private String source;

    public Team(String id, List<SimpleCard> cards, ColorType colorType, Float rank, String source) {
        this.id = id;
        this.cards = cards;
        this.colorType = colorType;
        this.rank = rank;
        this.source = source;
    }

    public Team(List<SimpleCard> cards, ColorType colorType, Float rank, String source) {
        this.cards = cards;
        this.colorType = colorType;
        this.rank = rank;
        this.id = toMinimalId();
        this.source = source;
    }

    public ColorType getSecondaryColor() {
        Optional<SimpleCard> secondaryColorCard = getCards().stream().filter(simpleCard -> simpleCard.getColorType() != ColorType.GOLD && simpleCard.getColorType() != ColorType.GRAY).findFirst();

        if (secondaryColorCard.isPresent()) {
            return secondaryColorCard.get().getColorType();
        }

        return ColorType.GOLD;
    }

    public boolean hasCard(int id) {
        if (cards == null) {
            return false;
        }

        for (SimpleCard simpleCard : cards) {
            if (simpleCard.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public String getId() {
        return id;
    }

    public List<SimpleCard> getCards() {
        return cards;
    }

    public ColorType getColorType() {
        return colorType;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String toSimpleString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (SimpleCard card : cards) {
            stringBuilder.append("[");
            stringBuilder.append(card.getId());
            stringBuilder.append("/"+card.getLevel());
            stringBuilder.append("]");
        }

        return stringBuilder.toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toNamesString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (SimpleCard card : cards) {
            stringBuilder.append("[");
            stringBuilder.append(card.getName());
            stringBuilder.append("]");
        }

        return stringBuilder.toString();
    }

    public Float getRank() {
        return rank;
    }

    public void setRank(Float rank) {
        this.rank = rank;
    }

    public String getSource() {
        return source;
    }

    public String toMinimalId() {
        StringBuilder stringBuilder = new StringBuilder();

        for (SimpleCard card : cards) {
            stringBuilder.append(card.getId());
            stringBuilder.append("-");
        }


        return stringBuilder.subSequence(0, stringBuilder.length() - 1).toString();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((Team)obj).id);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id='" + id + '\'' +
                ", cards=" + cards +
                ", colorType=" + colorType +
                ", rank='" + rank + '\'' +
                '}';
    }
}
