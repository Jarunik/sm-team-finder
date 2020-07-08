package org.slos.splinterlands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slos.domain.TeamRank;
import org.slos.query.DynamoDBRecordService;
import org.slos.query.League;
import org.slos.splinterlands.domain.LevelLimit;
import org.slos.splinterlands.settings.SteemMonstersSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TeamInfoService {
    @Autowired private RestTemplate restTemplate;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private SteemMonstersSettingsService steemMonstersSettingsService;
    @Autowired private DynamoDBRecordService dynamoDBRecordService;
    private final String URL;

    public static final String TEAM_INFO_SERVICE_SOURCE = "TeamInfoService";

    private final Logger logger = LoggerFactory.getLogger(TeamInfoService.class);

    public TeamInfoService(String url) {
        this.URL = url;
    }

    public TeamInfoResponse getTeamsFromDynamoSource(int mana, String ruleset, int rank, LevelLimit levelLimit, String opponent) throws Exception {
        logger.info("-- USING DYNAMODB SERVICE --");
        System.out.println("-- USING DYNAMODB SERVICE --");
        League league = League.getFromId(rank);
        Long currentBlock = (long) steemMonstersSettingsService.getSettings().getLastBlock();
        //(int mana, String ruleset, League league, Long currentBlock, String opponent)
        return dynamoDBRecordService.getResults(mana, ruleset, League.getFromId(levelLimit.getId()), currentBlock , opponent);
    }

    public TeamInfoResponse getTeamsInfo(int mana, String ruleset, int rank, LevelLimit levelLimit, String player, String opponent) {
        Set<String> testers = new HashSet<>();
        testers.add("account-name");

//        if (URL.equals("dynamoDB") || testers.contains(player)) {
//            try {
//                TeamInfoResponse response = getTeamsFromDynamoSource(mana, ruleset, rank, levelLimit, opponent);
//                System.out.println("Returining teams [" + ruleset + "]: " + response.getTeams().size());
//                return response;
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        else {
            return getTeamsFromURL(mana, ruleset, rank, levelLimit, opponent);
//        }
    }

    public TeamInfoResponse getTeamsFromURL(int mana, String ruleset, int rank, LevelLimit levelLimit, String opponent) {
        String url;
        String response;
        url = URL + "?mana=" + mana + "&ruleset=" + ruleset.replaceAll("Up Close & Personal", "Up Close " + "%26" + " Personal") + "&rank=" + rank;
        url = url + "&currentBlock=" + steemMonstersSettingsService.getSettings().getLastBlock() + "&league=" + levelLimit.getStringValue() + "&opponent=" + opponent;
        logger.info("Calling: " + url);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL).query(
                "mana=" + mana +
                "&ruleset=" + ruleset.replaceAll("Up Close & Personal", "Up%20Close%20%26%20Personal").replaceAll(" ", "%20").replaceAll("\\|", "%7C") +
                "&rank=" + rank +
                "&currentBlock=" + steemMonstersSettingsService.getSettings().getLastBlock() +
                "&league=" + levelLimit.getStringValue() +
                "&opponent=" + opponent);
        UriComponents components = builder.build(true);
        URI uri = components.toUri();
        response = restTemplate.getForObject(uri, String.class);
        logger.info("Finished calling: " + url);

        try {
            TeamInfoResponse teamRanks = objectMapper.readValue(response, TeamInfoResponse.class);
            teamRanks.getTeams().stream().forEach(teamRank -> teamRank.setSource(TEAM_INFO_SERVICE_SOURCE));

            return teamRanks;

        } catch (Exception e) {
            try {
                TeamRank[] teamRanks = objectMapper.readValue(response, TeamRank[].class);

                return new TeamInfoResponse(Arrays.asList(teamRanks), null, null, null);
            }
            catch (Exception ee) {
                throw new RuntimeException(ee);
            }
        }
    }
}
