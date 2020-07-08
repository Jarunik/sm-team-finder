package org.slos.flow;

import org.slos.domain.TeamRequest;
import org.slos.domain.TeamRequestContextFactory;
import org.slos.services.AddChickenService;
import org.slos.services.CollectionPlayableFilter;
import org.slos.services.DelayService;
import org.slos.services.QuestTeamSelector;
import org.slos.services.RankedYieldTeamSelector;
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
import org.slos.splinterlands.AddBackupCollection;
import org.slos.services.AddBackupTeams;
import org.slos.splinterlands.RunSimilarTeams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.messaging.MessageChannel;

@Configuration
@IntegrationComponentScan
@EnableIntegration
public class TeamFinderFlow {
    @Autowired
    TeamSuggestionSorter teamSuggestionSorter;
    @Autowired
    TeamRequestContextFactory teamRequestContextFactory;
    @Autowired
    CollectionPlayableFilter collectionPlayableFilter;
    @Autowired
    SuggestedTeamsSimulationService suggestedTeamsSimulationService;
    @Autowired
    TeamResponseTransformer teamResponseTransformer;
    @Autowired
    TeamInformationService teamInformationService;
    @Autowired TeamCountFilter teamCountFilter;
    @Autowired TeamRankToActualTeamTransformer teamRankToActualTeamTransformer;
    @Autowired TeamByRankSorter teamByRankSorter;
    @Autowired TeamResultsSorter teamResultsSorter;
    @Autowired SuggestedTeamsFilter suggestedTeamsFilter;
    @Autowired TeamResultsCountFilter teamResultsCountFilter;
    @Autowired RankedYieldTeamSelector rankedYieldTeamSelector;
    @Autowired QuestTeamSelector questTeamSelector;
    @Autowired
    AddBackupCollection addBackupCollection;
    @Autowired AddBackupTeams addBackupTeams;
    @Autowired
    RunSimilarTeams runSimilarTeams;
    @Autowired ReflectiveTeamBuilderService reflectiveTeamBuilderService;
    @Autowired DelayService delayService;
    @Autowired SetColorsByHistoricalRatio setColorsByHistoricalRatio;
    @Autowired SetTeamsBasedOnColorPerformance setTeamsBasedOnColorPerformance;
    @Autowired AddChickenService addChickenService;
    @Autowired SpecificStratigySelector specificStratigySelector;

    @Bean
    public IntegrationFlow startGameIntegrationFlow() {
        return IntegrationFlows.from(teamRequestChannel())
                // Incoming TeamRequest Object
                .transform(teamRequestContextFactory::getTeamRequestContext)
                // TeamRequestContext Object
                .transform(collectionPlayableFilter::processStep)
                // Filtered Collection
                .transform(addBackupCollection::processStep)
                // Add backup collection for opponent
                .transform(suggestedTeamsFilter::processStep)
                // Remove invalid suggestions
                .transform(addChickenService::processStep)
                // Add chicken in back
                .transform(teamRankToActualTeamTransformer::processStep)
                // Converted team suggestions to actual teams
                .transform(setColorsByHistoricalRatio::processStep)
                // Set teams to historical play count ratios
                .transform(addBackupTeams::processStep)
                // Add in backup teams
                .transform(reflectiveTeamBuilderService::processStep)
                // Add new teams
                .transform(teamByRankSorter::processStep)
                // Rank teams
                .transform(setTeamsBasedOnColorPerformance::processStep)
                // Set player teams by color stats
                .transform(teamCountFilter::processStep)
                // Set team count
                .transform(rankedYieldTeamSelector::processStep)
                // Check for yield
                .transform(suggestedTeamsSimulationService::processStep)
                // Completed Simulations
                .transform(teamResultsSorter::processStep)
                // Sorted by team results
                .transform(runSimilarTeams::processStep)
                // Add top team permutations
                .transform(teamResultsSorter::processStep)
                // Sorted by team results
                .transform(questTeamSelector::processStep)
                // Select quest team if on quest and is good
//                .transform(specificStratigySelector::processStep)
                // Run custom team selector against specific individuals
                .transform(teamResultsCountFilter::processStep)
                // Limit results to configured amount
                .transform(delayService::processStep)
                // Delay if configured to do so
                .transform(teamResponseTransformer::transform)
                // TeamResponse Object
                .transform(message -> message.toString())
                // TeamRanks as String JSON
                .channel(teamResponseChannel())
                // Sent back to requestor
                .get();
    }

    @Bean
    public HttpRequestHandlingMessagingGateway httpGateway() {
        HttpRequestHandlingMessagingGateway gateway = new HttpRequestHandlingMessagingGateway(true);
        RequestMapping mapping = new RequestMapping();
        mapping.setMethods(HttpMethod.POST);
        mapping.setPathPatterns("/getBestTeam");
        gateway.setRequestMapping(mapping);
        gateway.setRequestChannel(teamRequestChannel());
        gateway.setReplyChannel(teamResponseChannel());
        gateway.setRequestPayloadTypeClass(TeamRequest.class);

        return gateway;
    }

    @Bean
    public MessageChannel collectionChannel() {
        return new QueueChannel();
    }

    @Bean
    public MessageChannel teamResponseChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel teamRequestChannel() {
        return new DirectChannel();
    }
}
