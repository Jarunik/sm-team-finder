package org.slos.splinterlands.collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionCard {

    private String player;
    private String uid;
    private Integer cardDetailId;
    private Integer xp;
    private Boolean gold;
    private Integer edition;
    private String marketId;
    private Float buyPrice;
    private Integer lastUsedBlock;
    private Integer lastTransferredBlock;
    private Integer alphaXp;
    private String delegatedTo;
    private String delegationTx;
    private String skin;
    private Integer level;
    private String lastUsedPlayer;

    @JsonCreator
    public CollectionCard(@JsonProperty("player") String player, @JsonProperty("uid") String uid, @JsonProperty("card_detail_id") Integer cardDetailId, @JsonProperty("xp") Integer xp, @JsonProperty("gold") Boolean gold, @JsonProperty("edition") Integer edition, @JsonProperty("market_id") String marketId, @JsonProperty("buy_price") Float buyPrice, @JsonProperty("last_used_block") Integer lastUsedBlock, @JsonProperty("last_transferred_block") Integer lastTransferredBlock, @JsonProperty("alpha_xp") Integer alphaXp, @JsonProperty("delegated_to") String delegatedTo, @JsonProperty("delegation_tx") String delegationTx, @JsonProperty("skin") String skin, @JsonProperty("level") Integer level, @JsonProperty("last_used_player") String lastUsedPlayer) {
        this.player = player;
        this.uid = uid;
        this.cardDetailId = cardDetailId;
        this.xp = xp;
        this.gold = gold;
        this.edition = edition;
        this.marketId = marketId;
        this.buyPrice = buyPrice;
        this.lastUsedBlock = lastUsedBlock;
        this.lastTransferredBlock = lastTransferredBlock;
        this.alphaXp = alphaXp;
        this.delegatedTo = delegatedTo;
        this.delegationTx = delegationTx;
        this.skin = skin;
        this.level = level;
        this.lastUsedPlayer = lastUsedPlayer;
    }

    public String getPlayer() {
        return player;
    }

    public String getUid() {
        return uid;
    }

    public Integer getCardDetailId() {
        return cardDetailId;
    }

    public Integer getXp() {
        return xp;
    }

    public Boolean getGold() {
        return gold;
    }

    public Integer getEdition() {
        return edition;
    }

    public String getMarketId() {
        return marketId;
    }

    public Float getBuyPrice() {
        return buyPrice;
    }

    public Integer getLastUsedBlock() {
        return lastUsedBlock;
    }

    public Integer getLastTransferredBlock() {
        return lastTransferredBlock;
    }

    public Integer getAlphaXp() {
        return alphaXp;
    }

    public String getDelegatedTo() {
        return delegatedTo;
    }

    public String getDelegationTx() {
        return delegationTx;
    }

    public String getSkin() {
        return skin;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getMonsterId() {
        return cardDetailId;
    }

    public String getLastUsedPlayer() {
        return lastUsedPlayer;
    }

    @Override
    public int hashCode() {
        return cardDetailId;
    }

    @Override
    public boolean equals(Object obj) {
        return ((CollectionCard) obj).getMonsterId().equals(getMonsterId());
    }

    @Override
    public String toString() {
        return "CollectionCard{" +
                "player='" + player + '\'' +
                ", uid='" + uid + '\'' +
                ", cardDetailId=" + cardDetailId +
                ", xp=" + xp +
                ", gold=" + gold +
                ", edition=" + edition +
                ", marketId='" + marketId + '\'' +
                ", buyPrice=" + buyPrice +
                ", lastUsedBlock=" + lastUsedBlock +
                ", lastTransferredBlock=" + lastTransferredBlock +
                ", alphaXp=" + alphaXp +
                ", delegatedTo='" + delegatedTo + '\'' +
                ", delegationTx='" + delegationTx + '\'' +
                ", skin='" + skin + '\'' +
                ", level=" + level +
                '}';
    }
}
