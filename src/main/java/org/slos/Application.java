package org.slos;

import org.slos.battle.abilities.AbilityFactory;
import org.slos.domain.TeamRequestContextFactory;
import org.slos.permission.ActivePlayPermissionService;
import org.slos.permission.ActivePlayService;
import org.slos.permission.MaxRatingService;
import org.slos.permission.PermissionToPlayService;
import org.slos.permission.MaxRankPermissionService;
import org.slos.permission.SafeToPlayLookupService;
import org.slos.permission.TimeDelayPermission;
import org.slos.permission.configuration.ConfigurationService;
import org.slos.permission.configuration.TimeDelayConfiguration;
import org.slos.query.DynamoDBRecordService;
import org.slos.ranking.RankingService;
import org.slos.rating.RatingContextFactory;
import org.slos.services.AddChickenService;
import org.slos.services.CollectionPlayableFilter;
import org.slos.services.DelayService;
import org.slos.services.QuestTeamSelector;
import org.slos.services.RankedYieldTeamSelector;
import org.slos.services.RealizedCollectionAdapter;
import org.slos.services.ReflectiveTeamBuilderService;
import org.slos.services.SetColorsByHistoricalRatio;
import org.slos.services.SetTeamsBasedOnColorPerformance;
import org.slos.services.SpecificStratigySelector;
import org.slos.services.SuggestedTeamsFilter;
import org.slos.services.SuggestedTeamsSimulationService;
import org.slos.services.TeamByRankSorter;
import org.slos.services.TeamCountFilter;
import org.slos.services.TeamRankToActualTeamTransformer;
import org.slos.services.TeamResponseTransformer;
import org.slos.services.TeamResultsCountFilter;
import org.slos.services.TeamResultsSorter;
import org.slos.services.TeamSuggestionSorter;
import org.slos.services.TeamInformationService;
import org.slos.services.strategy.BestPlacementForColorStrategy;
import org.slos.services.strategy.BestRatingThenPlacementStrategy;
import org.slos.services.strategy.PermutationStrategy;
import org.slos.services.strategy.TeamBuildingStrategy;
import org.slos.splinterlands.AddBackupCollection;
import org.slos.services.AddBackupTeams;
import org.slos.splinterlands.RunSimilarTeams;
import org.slos.splinterlands.SplinterlandsClient;
import org.slos.splinterlands.TeamInfoService;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;
import org.slos.splinterlands.domain.monster.MonsterDetailsListService;
import org.slos.splinterlands.history.PlayerBattleHistoryService;
import org.slos.splinterlands.settings.SteemMonstersSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {
    @Autowired AppConfig appConfig;

    @Bean
    public SpecificStratigySelector specificStratigySelector() {
        return new SpecificStratigySelector();
    }

    @Bean
    public DynamoDBRecordService dynamoDBRecordService() {
        return new DynamoDBRecordService();
    }

    @Bean
    public PlayerBattleHistoryService playerBattleHistoryService() {
        return new PlayerBattleHistoryService();
    }

    @Bean
    public MaxRankPermissionService ratingRankService() {
        return new MaxRankPermissionService();
    }

    @Bean
    public MaxRatingService maxRatingService() {
        return new MaxRatingService();
    }

    @Bean
    public AddChickenService addChickenService() {
        return new AddChickenService();
    }

    @Bean
    public SetTeamsBasedOnColorPerformance setTeamsBasedOnColorPerformance() {
        return new SetTeamsBasedOnColorPerformance();
    }

    @Bean
    public PermutationStrategy permutationStrategy() {
        return new PermutationStrategy();
    }

    @Bean
    public SetColorsByHistoricalRatio setColorsByHistoricalRatio() {
        return new SetColorsByHistoricalRatio();
    }

    @Bean
    public TimeDelayConfiguration getTimeDelayConfiguration() {
        return new TimeDelayConfiguration();
    }

    @Bean
    public TimeDelayPermission timeDelayPermission() {
        return new TimeDelayPermission();
    }

    @Bean
    public DelayService delayService() {
        return new DelayService();
    }

    @Bean
    public ConfigurationService configurationService() {
        return new ConfigurationService();
    }

    @Bean
    public ActivePlayService activePlayService() {
        return new ActivePlayService();
    }

    @Bean
    public ActivePlayPermissionService masterSwitchService() {
        return new ActivePlayPermissionService();
    }

    @Bean
    public BestPlacementForColorStrategy bestPlacementForColorStrategy() {
        return new BestPlacementForColorStrategy();
    }

    @Bean
    public BestRatingThenPlacementStrategy bestRatingThenPlacementStrategy() {
        return new BestRatingThenPlacementStrategy();
    }

    @Bean
    public List<TeamBuildingStrategy> teamBuildingStrategies() {
        List<TeamBuildingStrategy> teamBuildingStrategies = new ArrayList<>();

        teamBuildingStrategies.add(bestPlacementForColorStrategy());
        teamBuildingStrategies.add(bestRatingThenPlacementStrategy());
        teamBuildingStrategies.add(permutationStrategy());

        return teamBuildingStrategies;
    }

    @Bean
    public RatingContextFactory ratingContextFactory() {
        return new RatingContextFactory();
    }

    @Bean
    public ReflectiveTeamBuilderService reflectiveTeamBuilderService(){
        return new ReflectiveTeamBuilderService();
    }

    @Bean
    public RunSimilarTeams runSimilarTeams() {
        return new RunSimilarTeams();
    }

    @Bean
    public AddBackupTeams addOpponentBackupTeams() {
        return new AddBackupTeams();
    }

    @Bean
    public AddBackupCollection addBackupCollection() {
        return new AddBackupCollection();
    }

    @Bean
    public QuestTeamSelector questTeamSelector() {
        return new QuestTeamSelector();
    }

    @Bean
    public RankedYieldTeamSelector rankedYieldTeamSelector() {
        return new RankedYieldTeamSelector();
    }

    @Bean
    public TeamResultsCountFilter teamResultsCountFilter() {
        return new TeamResultsCountFilter();
    }

    @Bean
    public SuggestedTeamsFilter suggestedTeamsColorFilter() {
        return new SuggestedTeamsFilter();
    }

    @Bean
    public TeamResultsSorter TeamResultsSorter() {
        return new TeamResultsSorter();
    }

    @Bean
    public TeamByRankSorter teamByRankSorter() {
        return new TeamByRankSorter();
    }

    @Bean
    public TeamRankToActualTeamTransformer teamRankToActualTeamTransformer() {
        return new TeamRankToActualTeamTransformer();
    }

    @Bean
    public TeamCountFilter teamCountFilter() {
        return new TeamCountFilter();
    }

    @Bean
    public RankingService rankingService() {
        return new RankingService();
    }

    @Bean
    public TeamSuggestionToActualCardsService teamSuggestionToActualCardsService() {
        return new TeamSuggestionToActualCardsService();
    }

    @Bean
    public RealizedCollectionAdapter realizedCollectionAdapter() {
        return new RealizedCollectionAdapter();
    }

    @Bean
    public TeamInformationService teamsSelector() {
        return new TeamInformationService();
    }

    @Bean
    public TeamResponseTransformer teamResponseTransformer() {
        return new TeamResponseTransformer();
    }

    @Bean
    public SuggestedTeamsSimulationService suggestedTeamsSimulationService() {
        return new SuggestedTeamsSimulationService();
    }

    @Bean
    public AbilityFactory abilityFactory() {
        return new AbilityFactory();
    }

    @Bean
    public MonsterDetailsList monsterDetailsList(MonsterDetailsListService monsterDetailsListService) {
        MonsterDetailsList monsterDetailsList = monsterDetailsListService.getMonsterDetailsList();

        return monsterDetailsList;
    }

    @Bean
    public MonsterBattleStatsService monsterBattleStatsService(MonsterDetailsList monsterDetailsList, AbilityFactory abilityFactory) {
        return new MonsterBattleStatsService(monsterDetailsList, abilityFactory);
    }

    @Bean
    public CollectionPlayableFilter collectionFilter() {
        return new CollectionPlayableFilter();
    }

    @Bean
    public SteemMonstersSettingsService steemMonstersSettingsService() {
        return new SteemMonstersSettingsService();
    }

    @Bean
    public TeamInfoService teamListService(@Value("${defaults.teamsUrl}") String url) {
        return new TeamInfoService(url);
    }

    @Bean
    public TeamRequestContextFactory teamRequestContextFactory() {
        return new TeamRequestContextFactory();
    }

    @Bean
    public TeamSuggestionSorter teamSuggestionSorter() {
        return new TeamSuggestionSorter();
    }

    @Bean
    public SplinterlandsClient splinterlandsClient() {
        return new SplinterlandsClient();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PermissionToPlayService permissionToPlayService() {
        return new PermissionToPlayService();
    }

    @Bean
    public SafeToPlayLookupService safeToPlayLookupService(@Value("${defaults.safetyLevel:10}") Integer safetyLevel, @Value("${defaults.safetyTime:20}") Integer safetyTime) {
        return new SafeToPlayLookupService(safetyLevel, safetyTime);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
