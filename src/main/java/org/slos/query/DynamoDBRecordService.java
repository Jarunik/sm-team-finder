package org.slos.query;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;
import org.slos.domain.TeamRank;
import org.slos.splinterlands.TeamInfoResponse;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;
import org.slos.splinterlands.domain.monster.MonsterDetailsListService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamoDBRecordService {

    private final static MonsterDetailsList monsterDetailList;
    private Map<String, Integer> winCount = new HashMap<>();
    private Map<String, Integer> loseCount = new HashMap<>();
    private Map<String, Float> winWeight = new HashMap<>();
    private Map<String, Float> loseWeight = new HashMap<>();
    private Map<String, String> teamColor = new HashMap<>();
    private Map<String, Integer> playCount = new HashMap<>();
    private Map<String, Integer> colorVsColorResults = new HashMap<>();
    private List<QueryTeamRank> opponentHistory = new ArrayList<>();

    Integer queryRunTime = null;
    Integer rankingRunTime = null;

    static {
        monsterDetailList = new MonsterDetailsListService().getMonsterDetailsList();
    }

    public static void main(String... args) throws Exception {
        System.out.println("Done");
    }

    private TeamInfoResponse rankTeams() throws Exception {
        List<QueryTeamRank> results = new ArrayList<>();
        for (String teamId : winWeight.keySet()) {

            Float winWeightValue = winWeight.get(teamId);
            Float loseWeightValue = loseWeight.get(teamId);
            if (loseWeightValue == null) {
                loseWeightValue = 0f;
            }

            int weight = 2;

            float rank = (winWeightValue + weight) / (loseWeightValue + weight);
            if ((playCount.get(teamId) != null) && (playCount.get(teamId) == 1)) {
                rank = (rank * .95f);
            }

            rank *= getUntaimedFactor(teamId);

            int totalPlayed = playCount.get(teamId);

            results.add(new QueryTeamRank(teamId, teamColor.get(teamId), rank, totalPlayed, winCount.get(teamId), loseCount.get(teamId)));
        }

        if (results.size() > 1000) {
            results = results.stream().filter(result -> result.getRank() > .8).collect(Collectors.toList());
        }

        Collections.sort(results, new TeamRankComparator());
        ObjectMapper objectMapper = new ObjectMapper();

        String teamList = objectMapper.writeValueAsString(results);
        String colorStats = objectMapper.writeValueAsString(colorVsColorResults);
        String opponentHistoryString = objectMapper.writeValueAsString(opponentHistory);
        String serviceStats = "{\"rankingRunTime\":" + rankingRunTime + ",\"queryRunTime\":" + queryRunTime +"}";

        List<TeamRank> teamRanks = convertToTeamRanks(results);
        List<TeamRank> opponentHistory = new ArrayList<>();
        TeamInfoResponse teamInfoResponse = new TeamInfoResponse(teamRanks, colorVsColorResults, opponentHistory, null);

        return teamInfoResponse;

//        return teamInfoResponse;
//        String result = "{\"teams\": " + teamList + ",\"serviceStats\":" + serviceStats + ",\"colorStats\":" + colorStats +",\"opponentHistory\":" + opponentHistoryString + "}";
//        return objectMapper.readValue(result, TeamInfoResponse.class);
    }

    private List<TeamRank> convertToTeamRanks(List<QueryTeamRank> results) {
        List<TeamRank> teamRanks = new ArrayList<>();

        //TeamRank(@JsonProperty("Color") ColorType color, @JsonProperty("Id") String id, @JsonProperty("Rank") Float rank, @JsonProperty("TotalPlayed") Integer totalPlayed,
        // @JsonProperty("Wins") Integer wins, @JsonProperty("Losses") Integer losses, Team team) {
        for (QueryTeamRank queryTeamRank : results) {
            if (queryTeamRank.getId() != null) {
                teamRanks.add(new TeamRank(ColorType.forColor(queryTeamRank.getColor()), queryTeamRank.getId(), queryTeamRank.getRank(), queryTeamRank.getTotalPlayed(), queryTeamRank.getTotalPlayed(),
                        queryTeamRank.getWins(), queryTeamRank.getLosses() + "", null));
            }
        }
        return teamRanks;
    }

    private Float getRankFactor(BattleRecord battleRecord) {
        if (battleRecord.getPlayerRating() >= 4200) {
            return 1.2f;
        }

        if (battleRecord.getPlayerRating() >= 3400) {
            return 1.1f;
        }

        return 1f;
    }

    private Float getUntaimedFactor(String teamId) {

        String[] cardIds = teamId.split("-");
        if (cardIds.length < 2) {
            return 1f;
        }

        Float weight = 1f;

        for (String cardId : cardIds) {
            if ((cardId != null) && (!cardId.equals("null")) && monsterDetailList.getDetails(Integer.parseInt(cardId))!= null) {
                String editions = monsterDetailList.getDetails(Integer.parseInt(cardId)).getEditions();
                if (editions != null && editions.contains("4")) {
                    weight += .2f;
                    break;
                }
            }
        }

        return weight;
    }

    private Float getPlayerFactor(BattleRecord battleRecord) {
        Set<String> goodPlayers = new HashSet<>();
        goodPlayers.add("bji1203");
        goodPlayers.add("goldfashioned");
        goodPlayers.add("glory7");
        goodPlayers.add("giker");
        goodPlayers.add("jacekw");
        goodPlayers.add("steallion");
        goodPlayers.add("imperfect-one");
        goodPlayers.add("twinner");
        goodPlayers.add("toocurious");
        goodPlayers.add("bubke");
        goodPlayers.add("coolbowser");
        goodPlayers.add("schneegecko");
        goodPlayers.add("mellofello");
        goodPlayers.add("hossainbd");
        goodPlayers.add("themightyvolcano");
        goodPlayers.add("pacolimited");
        goodPlayers.add("asuraa");
        goodPlayers.add("smk2000");
        goodPlayers.add("caraxes");
        goodPlayers.add("frosta");
        goodPlayers.add("faiyazmahmud");

        if (goodPlayers.contains(battleRecord.getPlayerId())) {
            return 2f;
        }

        return 1f;
    }

    public void setRecordWeights(List<BattleRecord> list, Map<String, Float> weightMap, League playerLeague, Long currentBlock, boolean isForWinner) {
        for (BattleRecord battleRecord : list) {
            Float staleFactor = getStaleFactor(currentBlock, battleRecord);
            Float leagueFactor = getLeagueDistanceFactor(playerLeague, battleRecord);
            Float teamLevelFactor = getTeamLevelFactor(battleRecord);

            Float weight = 1 * staleFactor * leagueFactor * teamLevelFactor;

            if (isForWinner) {
                weight = weight * getRankFactor(battleRecord);
                weight = weight * getPlayerFactor(battleRecord);
            }

            String teamId = battleRecord.getId();
            if (weightMap.get(teamId) == null) {
                weightMap.put(teamId, weight);
            }
            else {
                Float weightFactorTotal = weightMap.get(teamId) + weight;
                weightMap.put(teamId, weightFactorTotal);
            }
        }
    }

    private Float getTeamLevelFactor(BattleRecord battleRecord) {
        Float differenceValue = battleRecord.getTeamDifferenceWeight();

        return 1 + (differenceValue);
    }

    private Float getLeagueDistanceFactor(League playerLeague, BattleRecord battleRecord) {
        League battleRecordLeague = League.getFromRating(battleRecord.getPlayerRating());
        int leagueDifference = League.getLeagueDistance(playerLeague, battleRecordLeague);
        return LeagueFactor.getWeight(leagueDifference);
    }

    private Float getStaleFactor(Long currentBlock, BattleRecord battleRecord) {
        Integer weeksOld = figureAgeInWeeksFromBlock(currentBlock, battleRecord.getBlock());
        return StaleFactor.getWeight(weeksOld);
    }

    private Integer figureAgeInWeeksFromBlock(Long currentBlock, Integer fromBlock) {
        Long diff = currentBlock - fromBlock;
        if (diff < 0) {
            diff = 0l;
        }
        //604800 seconds in a week, / 3 = 201600
        return Math.toIntExact(diff / 201600);
    }

    public TeamInfoResponse getResults(int mana, String ruleset, League league, Long currentBlock, String opponent) throws Exception {
        List<BattleRecord> winnerRecords = new ArrayList<>();
        List<BattleRecord> loserRecords = new ArrayList<>();

        queryAndProcessForSpecificRuleset(mana, ruleset, league, currentBlock, null, winnerRecords, loserRecords, opponent);

//        if (ruleset.contains("|")) {
//            String[] rulesetSplit = ruleset.split("\\|");
//            String reverseRuleset = rulesetSplit[1] + "|" + rulesetSplit[0];
//            queryAndProcessForSpecificRuleset(mana, reverseRuleset, league, currentBlock, null, winnerRecords, loserRecords, opponent);
//        }

        return rankTeams();
    }

    private String sortRuleset(String ruleset) {
        if (!ruleset.contains("|")) {
            return ruleset;
        }

        final StringBuffer sortedRulest = new StringBuffer();

        Arrays.stream(ruleset.split("\\|")).sorted().forEach(s -> sortedRulest.append(s + "|"));

        return sortedRulest.substring(0, sortedRulest.length() -1);
    }

    private void queryAndProcessForSpecificRuleset(int mana, String ruleset, League league, Long currentBlock, String connectionUrl, List<BattleRecord> winnerRecords, List<BattleRecord> loserRecords, String opponent) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("SM-Battle");

        String sortedRuleset = sortRuleset(ruleset);

        System.out.println(":v_id = " + mana + "|" + sortedRuleset);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("BattleLookupHash = :v_id")
                .withValueMap(new ValueMap()
                        .withString(":v_id", mana + "|" + sortedRuleset));

        Long startQuery = System.currentTimeMillis();
        ItemCollection<QueryOutcome> items = table.query(spec);
        Long endQuery = System.currentTimeMillis();
        queryRunTime = (int)((endQuery - startQuery) / 1000);

        Iterator<Item> iterator = items.iterator();

        int winnerBlock;
        String winnerColor;
        int loserBlock;
        String loserColor;

        Long ratingStart = System.currentTimeMillis();
        Item item = null;

        ObjectMapper objectMapper = new ObjectMapper();

        while (iterator.hasNext()) {
            try {
                item = iterator.next();

                String winnerPlayerId = item.getString("Winner"); //Note: Database always put winner in player 1
                winnerBlock = item.getInt("BlockNumber");
                int winnerSummoner = item.getInt("WinnerSummoner");
                String winnerTeamId = getTeamId(item, winnerSummoner, "Winner");

                String loserPlayerId = item.getString("Loser");
                loserBlock = item.getInt("BlockNumber");

                BattleTeamRecord winningTeam = objectMapper.readValue(item.getJSON("WinningTeam"), BattleTeamRecord.class);
                winnerColor = monsterDetailList.getDetails(winnerSummoner).getColor().id();

                BattleTeamRecord losingTeam = objectMapper.readValue(item.getJSON("LosingTeam"), BattleTeamRecord.class);
                int loserSummoner = item.getInt("LoserSummoner");
                loserColor = monsterDetailList.getDetails(loserSummoner).getColor().id();

                String loserTeamId = getTeamId(item, loserSummoner, "Loser");
                int winnerRank = item.getInt("WinnerRank");

                tallyColorVsColorCount(winnerColor, loserColor);

                Float winnerTeamDifferenceWeight = getTeamDifferenceWeight(winningTeam, losingTeam);

                winnerRecords.add(new BattleRecord(winnerTeamId, winnerBlock, winnerColor, winnerRank, winnerTeamDifferenceWeight, winnerPlayerId));
                loserRecords.add(new BattleRecord(loserTeamId, loserBlock, loserColor, winnerRank, winnerTeamDifferenceWeight * -1, loserPlayerId));
                addCount(playCount, winnerTeamId);
                addCount(playCount, loserTeamId);
                addCount(winCount, winnerTeamId);
                addCount(loseCount, loserTeamId);
                teamColor.put(winnerTeamId, winnerColor);

                if (winnerPlayerId.equals(opponent)) {
                    opponentHistory.add(new QueryTeamRank(winnerTeamId, winnerColor, 1000f, null, null, null));
                } else if (loserPlayerId.equals(opponent)) {
                    opponentHistory.add(new QueryTeamRank(loserTeamId, loserColor, 1000f, null, null, null));
                }
                }
            catch (Exception e) {
                System.out.println("Bad item: " + item);
            }
        }

        setRecordWeights(winnerRecords, winWeight, league, currentBlock, true);
        setRecordWeights(loserRecords, loseWeight, league, currentBlock, false);


        Long ratingEnd = System.currentTimeMillis();
        rankingRunTime = (int) ((ratingEnd - ratingStart) / 1000);
        System.out.println("Rating run time: " + rankingRunTime);
    }

    private Integer tallyColorVsColorCount(String winnerColor, String loserColor) {
        String colorVsColorKey = winnerColor + "|" + loserColor;
        Integer winCount = colorVsColorResults.get(colorVsColorKey);
        if (winCount == null) {
            winCount = 1;
        }
        else {
            winCount += 1;
        }
        colorVsColorResults.put(colorVsColorKey, winCount);
        return winCount;
    }

    private void addCount(Map<String, Integer> map, String teamId) {
        if (map.get(teamId) == null) {
            map.put(teamId, 1);
        }
        else {
            Integer newPlayCount = map.get(teamId) + 1;
            map.put(teamId, newPlayCount);
        }
    }

    private Map<League, List<BattleRecord>> sortByLeague(List<BattleRecord> battleRecords) {
        Map<League, List<BattleRecord>> sortedMap = new HashMap<>();

        for (League league : League.values()) {
            sortedMap.put(league, new ArrayList<>());
        }

        for (BattleRecord battleRecord : battleRecords) {
            League league = League.getFromRating(battleRecord.getRating().intValue());
            List<BattleRecord> battleRecordListForLeague = sortedMap.get(league);
            battleRecordListForLeague.add(battleRecord);
        }

        return sortedMap;
    }

    private Float getTeamDifferenceWeight(BattleTeamRecord winnerTeam, BattleTeamRecord loserTeam) {
        Float averageWinnerMonsterFactor = getMonsterWeightFactor(winnerTeam, "Winner");
        Float averageLoserMonsterFactor = getMonsterWeightFactor(loserTeam, "Loser");

        Float explodedWeight = (float)(averageWinnerMonsterFactor) - (averageLoserMonsterFactor);
        return ((explodedWeight) / 120) * 10;
    }

    private Float getMonsterWeightFactor(BattleTeamRecord winnerTeam, String playerId) {
        Float factor = 0f;
        Integer monsterCount = 0;
        for (CardRecord cardRecord : winnerTeam.getMonsters()) {
            int monsterId = cardRecord.getMonsterId();

            Integer monsterLevel = cardRecord.getMonsterLevel();
            Integer monsterRarity = monsterDetailList.getDetails(monsterId).getRarity();
            factor += LevelFactor.getBaseWeight(monsterRarity, monsterLevel);
            monsterCount++;
        }

        if (monsterCount == 0) {
            return .0001f;
        }

        return (factor / monsterCount);
    }

    private String getTeamId(Item item, int summoner, String player) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(summoner + "-");

        for (int i = 0; i < 6; i++) {
            String monster = item.getString(player + "Monster" + (i+1));
            if ((monster != null) && (!monster.equals("null"))) {
                stringBuffer.append(monster);

                if ((i < 5) && (item.getString(player + "Monster" + (i+2)) != null) && (!item.getString(player + "Monster" + (i+2)).equals("null"))) {
                    stringBuffer.append("-");
                }
            }
        }

        return stringBuffer.toString();
    }

    class TeamRankComparator implements Comparator<QueryTeamRank> {
        @Override
        public int compare(QueryTeamRank o1, QueryTeamRank o2) {
            return Float.compare(o2.getRank(), o1.getRank());
        }
    }
}
