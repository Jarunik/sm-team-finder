package org.slos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slos.splinterlands.domain.AllowedCards;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.LevelLimit;
import org.slos.splinterlands.domain.QuestMode;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.util.ToJson;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamRequest implements ToJson {
    private String playerTop;
    private String playerBottom;
    private String gameRules;
    private Integer mana;
    private Set<ColorType> notColors;
    private LevelLimit levelLimit;
    private Integer topTeamCount;
    private Integer bottomTeamCount;
    @JsonProperty("tournamentRule")
    private AllowedCards allowedCards;
    private QuestMode questMode;
    private String knownTeam;
    private Integer battlesPerMatchup;
    private Boolean sendCollectionTeam = false;
    private Boolean isTournamentGame = false;

    public TeamRequest() {
    }

    @JsonCreator
    public TeamRequest(@JsonProperty("playerTop") String playerTop,
                       @JsonProperty("playerBottom") String playerBottom,
                       @JsonProperty("gameRules") String gameRules,
                       @JsonProperty("mana") Integer mana,
                       @JsonProperty("notColors") Set<ColorType> notColors,
                       @JsonProperty("levelLimit") LevelLimit levelLimit,
                       @JsonProperty("topTeamCount") Integer topTeamCount,
                       @JsonProperty("bottomTeamCount") Integer bottemTeamCount,
                       @JsonProperty("tournamentRule") AllowedCards allowedCards,
                       @JsonProperty("questMode") QuestMode questMode,
                       @JsonProperty("knownTeam") String knownTeam,
                       @JsonProperty("battlesPerMatchup") Integer battlesPerMatchup,
                       @JsonProperty("sendCollectionTeam") Boolean sendCollectionTeam) {
        this.playerTop = playerTop;
        this.playerBottom = playerBottom;
        this.gameRules = gameRules;
        this.mana = mana;
        this.notColors = notColors;
        this.levelLimit = levelLimit;
        this.topTeamCount = topTeamCount;
        this.bottomTeamCount = bottemTeamCount;
        if (allowedCards != null) {
            this.isTournamentGame = true;
        }
        this.allowedCards = allowedCards == null ? AllowedCards.all() : allowedCards;
        this.questMode = questMode;
        this.knownTeam = knownTeam;
        this.battlesPerMatchup = battlesPerMatchup;
        this.sendCollectionTeam = sendCollectionTeam;
    }

    public String getPlayerTop() {
        return playerTop;
    }

    public String getPlayerBottom() {
        return playerBottom;
    }

    public String getGameRules() {
        return gameRules;
    }

    public Integer getMana() {
        return mana;
    }

    public Set<ColorType> getNotColors() {
        return notColors;
    }

    public LevelLimit getLevelLimit() {
        return levelLimit;
    }

    public AllowedCards getAllowedCards() {
        return allowedCards;
    }

    public QuestMode getQuestMode() {
        return questMode;
    }

    public void setQuestMode(QuestMode questMode) {
        this.questMode = questMode;
    }

    public void setAllowedCards(AllowedCards allowedCards) {
        this.allowedCards = allowedCards;
    }

    public void setPlayerTop(String playerTop) {
        this.playerTop = playerTop;
    }

    public void setPlayerBottom(String playerBottom) {
        this.playerBottom = playerBottom;
    }

    public void setGameRules(String gameRules) {
        this.gameRules = gameRules;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setNotColors(Set<ColorType> notColors) {
        this.notColors = notColors;
    }

    public void setLevelLimit(LevelLimit levelLimit) {
        this.levelLimit = levelLimit;
    }

    public Integer getTopTeamCount() {
        return topTeamCount;
    }

    public Integer getBottomTeamCount() {
        return bottomTeamCount;
    }

    public void setTopTeamCount(int topTeamCount) {
        this.topTeamCount = topTeamCount;
    }

    public void setBottomTeamCount(int bottomTeamCount) {
        this.bottomTeamCount = bottomTeamCount;
    }

    public String getKnownTeam() {
        return knownTeam;
    }

    public Integer getBattlesPerMatchup() {
        return battlesPerMatchup;
    }

    public void setBattlesPerMatchup(Integer battlesPerMatchup) {
        this.battlesPerMatchup = battlesPerMatchup;
    }

    public Boolean getSendCollectionTeam() {
        if (sendCollectionTeam == null) {
            return false;
        }

        return sendCollectionTeam;
    }

    public Boolean getTournamentGame() {
        return isTournamentGame;
    }

    public void setTournamentGame(Boolean tournamentGame) {
        isTournamentGame = tournamentGame;
    }

    @JsonIgnore
    public Set<GameRuleType> getRuleset() {
        if (gameRules != null) {
            String splitOn = "|";
            String[] rulez = gameRules.split(Pattern.quote(splitOn));
            Set<GameRuleType> rules = new HashSet<>();

            for (String rule : rulez) {
                if (rule.equals("Up Close & Personal") || rule.equals("Up Close and Personal") || rule.equals("Up Close %26 Personal")) {
                    rules.add(GameRuleType.UP_CLOSE_AND_PERSONAL);
                }
                else {
                    String ruleUpper = rule.toUpperCase();
                    ruleUpper = ruleUpper.replaceAll(" ", "_");

                    rules.add(GameRuleType.valueOf(ruleUpper));
                }
            }

            return rules;
        }
        return new HashSet<>();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
