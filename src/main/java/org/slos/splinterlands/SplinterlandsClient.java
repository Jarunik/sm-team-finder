package org.slos.splinterlands;

import org.slos.TeamSuggestionToActualCardsService;
import org.slos.splinterlands.collection.Collection;
import org.slos.splinterlands.settings.SMSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestTemplate;

public class SplinterlandsClient {
    @Value("${api.apiUrl}") private String apiUrl;
    @Value("${api.getCardDetails}") private String getCardDetails;
    @Value("${api.settings}") private String settings;
    @Value("${api.playersLeaderboard}") private String playersLeaderboard;
    @Value("${api.findCards}") private String findCards;
    @Value("${api.getUpcomingTournaments}") private String getUpcomingTournaments;
    @Value("${api.getUpcomingTournamentsUser}") private String getUpcomingTournamentsUser;
    @Value("${api.getInprogressTournaments}") private String getInprogressTournaments;
    @Value("${api.getInprogressTournamentsUser}") private String getInprogressTournamentsUser;
    @Value("${api.getCompletedTournaments}") private String getCompletedTournaments;
    @Value("${api.getCompletedTournamentsUser}") private String getCompletedTournamentsUser;
    @Value("${api.getTournament}") private String getTournament;
    @Value("${api.getCollection}") private String getCollection;
    @Value("${api.getPlayerDetails}") private String getPlayerDetails;
    @Value("${api.getPlayerQuests}") private String getPlayerQuests;
    @Value("${api.getCardStats}") private String getCardStats;
    @Value("${api.getBattleHistory}") private String getBattleHistory;
    @Value("${api.getBattleResult}") private String getBattleResult;
    @Value("${api.safetyCheckUrl}") String safetyCheckUrl;

//    @Autowired RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(TeamSuggestionToActualCardsService.class);

    public ResponseEntity<String> getSafetyCheck() {
        return makeGetCall(safetyCheckUrl);
    }

    public ResponseEntity<String> getCardDetails() {
        return makeGetCall(apiUrl + getCardDetails);
    }

    public SMSettings getSettings() {
        return makeGetCall(apiUrl + settings, SMSettings.class);
    }

    public ResponseEntity<String> playersLeaderboard() {
        return makeGetCall(apiUrl + playersLeaderboard);
    }

    public ResponseEntity<String> findCards() {
        return makeGetCall(apiUrl + findCards);
    }

    public ResponseEntity<String> getUpcomingTournaments() {
        return makeGetCall(apiUrl + getUpcomingTournaments);
    }

    public ResponseEntity<String> getUpcomingTournamentsUser() {
        return makeGetCall(apiUrl + getUpcomingTournamentsUser);
    }

    public ResponseEntity<String> getInprogressTournaments() {
        return makeGetCall(apiUrl + getInprogressTournaments);
    }

    public ResponseEntity<String> getInprogressTournamentsUser() {
        return makeGetCall(apiUrl + getInprogressTournamentsUser);
    }

    public ResponseEntity<String> getCompletedTournaments() {
        return makeGetCall(apiUrl + getCompletedTournaments);
    }

    public ResponseEntity<String> getCompletedTournamentsUser() {
        return makeGetCall(apiUrl + getCompletedTournamentsUser);
    }

    public ResponseEntity<String> getTournament() {
        return makeGetCall(apiUrl + getTournament);
    }

    public Collection getCollection(String id) {
        return makeGetCall(String.format(apiUrl + getCollection, id), Collection.class);
    }

    public ResponseEntity<String> getPlayerDetails() {
        return makeGetCall(apiUrl + getPlayerDetails);
    }

    public ResponseEntity<String> getPlayerQuests() {
        return makeGetCall(apiUrl + getPlayerQuests);
    }

    public ResponseEntity<String> getCardStats() {
        return makeGetCall(apiUrl + getCardStats);
    }

    public ResponseEntity<String> getBattleHistory(String id) {
        return makeGetCall(String.format(apiUrl + getBattleHistory, id));
    }

    public ResponseEntity<String> getBattleResult() {
        return makeGetCall(apiUrl + getBattleResult);
    }

    private ResponseEntity<String> makeGetCall(String url) {
        return makeGetCall(url, 0);
    }

    private ResponseEntity<String> makePostCall(String url, Object body) {
        return makePostCall(url, body, 0);
    }

    private void printResponse(String response) {
        if (response.length() > 1000) {
            logger.info(response.substring(0, 1000) + "...");
        }
        else {
            logger.info(response);
        }
    }

    private <T> T makeGetCall(String url, Class<T> clazz) {
        return makeGetCall(url, 0, clazz);
    }

    private void logRequestTime(Long start, String message) {
        Long now = System.currentTimeMillis();

        logger.info("Finished " + message + (now - start));
    }

    private <T> T makeGetCall(String url, Integer retryCount, Class<T> clazz) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Long start = System.currentTimeMillis();

            logger.info("Calling [Get]: " + url);
            ResponseEntity<T> response = restTemplate.getForEntity(url, clazz);
            if (response.getStatusCode() == HttpStatus.OK) {
//                printResponse("Response: " + response.getBody());
                logRequestTime(start,"[Get] " + url +": ");
                return response.getBody();
            }
            if (retryCount > 0) {
//                logger.info("Returned with status code: " + response.getStatusCode());
                logRequestTime(start,"[Get] " + url + ": ");
                return makeGetCall(url, retryCount--, clazz);
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception on call to: " + url, e);
        }
    }

    private ResponseEntity<String> makePostCall(String url, Object body, Integer retryCount) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Long start = System.currentTimeMillis();
            logger.info("Calling [Post]: " + url);
            logger.info("With Body: " + body);

            ResponseEntity<String> response = restTemplate.postForEntity(url, body, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<String> responseBody = response;

//                printResponse("Response: " + responseBody.getBody());
                logRequestTime(start,"[Post] " + url + ": ");
                return responseBody;
            }
            if (retryCount > 0) {
                return makePostCall(url, body, retryCount--);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception on call to: " + url, e);
        }
    }

    @Retryable
    private ResponseEntity<String> makeGetCall(String url, Integer retryCount) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Long start = System.currentTimeMillis();
            logger.info("Calling [Get]: " + url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
//                printResponse("Response: " + response.getBody());
                logRequestTime(start,"[Get] " + url + ": ");
                return response;
            }
            if (retryCount > 0) {
//                logger.info("Returned with status code: " + response.getStatusCode());
                return makeGetCall(url, retryCount--);
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception on call to: " + url, e);
        }
    }
}
