package org.slos.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slos.Team;
import org.slos.rating.ColorVsColorRatingResults;
import org.slos.util.ToJson;

import java.util.ArrayList;
import java.util.List;

public class TeamResponse implements ToJson {
    private List<TeamRank> teamRanks;
    private Boolean returnCollectionIds;
    private ColorVsColorRatingResults colorVsColorRatingResults;
    private Integer teamCount;

    public TeamResponse(List<TeamRank> teamRanks, Boolean returnCollectionIds, ColorVsColorRatingResults colorVsColorRatingResults, Integer teamCount) {
        this.teamRanks = teamRanks;
        this.returnCollectionIds = returnCollectionIds;
        this.colorVsColorRatingResults = colorVsColorRatingResults;
        this.teamCount = teamCount;
    }

    public void setTeamRanks(List<TeamRank> teamRanks) {
        this.teamRanks = teamRanks;
    }

    public List<TeamRank> getTeamRanks() {
        return teamRanks;
    }

    @Override
    public String toString() {
        if (returnCollectionIds) {
            List<CollectionTeam> collectionTeams = new ArrayList<>();

            for (TeamRank teamRank : teamRanks) {
                Team team = teamRank.getTeam();
                if ((team != null) && (team.getCards() != null) && (team.getCards().size() > 0) && (team.getCards().get(0) != null)) {
                    String summoner = team.getCards().get(0).getCollectionId();
                    List<String> monsters = new ArrayList<>();

                    String[] cardIDs = team.getId().split("-");
                    for (int i = 1; i < team.getCards().size(); i++) {
                        monsters.add(team.getCards().get(i).getCollectionId());
                    }

                    collectionTeams.add(new CollectionTeam(summoner, monsters));
                }
            }
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                List<CollectionTeam> responseTeams = new ArrayList<>();

                for (int i = 0; i < teamCount && i < collectionTeams.size(); i++) {
                    responseTeams.add(collectionTeams.get(i));
                }

                CollectionResponse collectionResponse = new CollectionResponse(responseTeams, colorVsColorRatingResults);
                String toJson = objectMapper.writeValueAsString(collectionResponse);
                return toJson;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String toJson = objectMapper.writeValueAsString(teamRanks);
                return toJson;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class CollectionResponse implements ToJson {
    @JsonProperty("teams")
    private List<CollectionTeam> team;
    @JsonProperty("colorVsColorRatingResults")
    private ColorVsColorRatingResults colorVsColorRatingResults;

    public CollectionResponse(List<CollectionTeam> team, ColorVsColorRatingResults colorVsColorRatingResults) {
        this.team = team;
        this.colorVsColorRatingResults = colorVsColorRatingResults;
    }

    public List<CollectionTeam> getTeam() {
        return team;
    }

    public void setTeam(List<CollectionTeam> team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public ColorVsColorRatingResults getColorVsColorRatingResults() {
        return colorVsColorRatingResults;
    }

    public void setColorVsColorRatingResults(ColorVsColorRatingResults colorVsColorRatingResults) {
        this.colorVsColorRatingResults = colorVsColorRatingResults;
    }
}

class CollectionTeam {
    @JsonProperty("summoner")
    private String summoner;
    @JsonProperty("monsters")
    private List<String> monsters;

    public CollectionTeam(String summoner, List<String> monsters) {
        this.summoner = summoner;
        this.monsters = monsters;
    }

    public String getSummoner() {
        return summoner;
    }

    public void setSummoner(String summoner) {
        this.summoner = summoner;
    }

    public List<String> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<String> monsters) {
        this.monsters = monsters;
    }
}