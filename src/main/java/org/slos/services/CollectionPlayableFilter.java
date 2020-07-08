package org.slos.services;

import org.slos.TeamRequestProcessStep;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.domain.RealizedCollection;
import org.slos.domain.RealizedCollectionCard;
import org.slos.domain.TeamRequest;
import org.slos.domain.TeamRequestContext;
import org.slos.splinterlands.domain.Foil;
import org.slos.splinterlands.domain.GameRuleType;
import org.slos.splinterlands.domain.TournamentRule;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterDetails;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.slos.splinterlands.settings.SMSettings;
import org.slos.splinterlands.settings.SteemMonstersSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionPlayableFilter implements TeamRequestProcessStep
{
    @Autowired
    SteemMonstersSettingsService steemMonstersSettingsService;

    private final Logger logger = LoggerFactory.getLogger(CollectionPlayableFilter.class);

    public TeamRequestContext processStep(TeamRequestContext teamRequestContext) {
        logger.info("Started collection filtering.");
        logger.info("Starting collection count: " + teamRequestContext.getPlayerCollection().getCards().size());
        TeamRequest teamRequest = teamRequestContext.getTeamRequest();

        String player = teamRequest.getPlayerTop();
        String opponent = teamRequest.getPlayerBottom();

        teamRequestContext.setPlayerCollection(filterOutUnplayable(teamRequestContext.getPlayerCollection(), player, teamRequest));
        teamRequestContext.setOpponentCollection(filterOutUnplayable(teamRequestContext.getOpponentCollection(), opponent, teamRequest));

        logger.info("Collection card filtered count: " + teamRequestContext.getPlayerCollection().getCards().size());

        logger.info("Finished collection filtering.");
        return teamRequestContext;
    }

    private RealizedCollection filterOutUnplayable(RealizedCollection collection, String player, TeamRequest teamRequest) {
        List<RealizedCollectionCard> collectionCards = collection.getCards().stream()
                .filter(realizedCollectionCard -> checkAllowedColor(realizedCollectionCard, teamRequest.getNotColors()))
                .filter(realizedCollectionCard -> checkDeligated(realizedCollectionCard, player))
                .filter(realizedCollectionCard -> checkCooldown(realizedCollectionCard, player, steemMonstersSettingsService.getSettings()))
                .filter(realizedCollectionCard -> realizedCollectionCard.getCollectionCard().getMarketId() == null)
                .filter(realizedCollectionCard -> checkValidCardForGameRules(realizedCollectionCard, teamRequest) == true)
                .collect(Collectors.toList());

        RealizedCollection filteredCollection = new RealizedCollection(collection.getPlayer(), collectionCards);
        if (teamRequest.getAllowedCards() != null) {
            filteredCollection = filterForTournamentRules(filteredCollection, teamRequest.getAllowedCards().getType());
            filteredCollection = filterForEdition(filteredCollection, teamRequest.getAllowedCards().getEditions());
            filteredCollection = filterForFoil(filteredCollection, teamRequest.getAllowedCards().getFoil());
        }

        return filteredCollection;
    }

    private boolean checkAllowedColor(RealizedCollectionCard collectionCard, Set<ColorType> disallowedColors) {
        return !disallowedColors.contains(collectionCard.getMonsterDetails().getColor());
    }

    private boolean checkDeligated(RealizedCollectionCard collectionCard, String player) {
        if (collectionCard.getCollectionCard().getDelegatedTo() == null) {
            return true;
        }

        return (collectionCard.getCollectionCard().getDelegatedTo().equals(player));
    }

    private boolean checkCooldown(RealizedCollectionCard collectionCard, String player, SMSettings smSettings) {
        if (collectionCard.getCollectionCard().getLastTransferredBlock() == null) {
//            System.out.println("CD [true]: " + collectionCard.getCollectionCard().getMonsterId() + " - " + collectionCard.getCollectionCard().getUid() + " - Never transfered");
            return true;
        }

        if (player.equals(collectionCard.getCollectionCard().getLastUsedPlayer())) {
//            System.out.println("CD [true]: " + collectionCard.getCollectionCard().getMonsterId() + " - " + collectionCard.getCollectionCard().getUid() + " - Last played by self.");
            return true;
        }

        if (collectionCard.getCollectionCard().getLastUsedPlayer() == null) {
//            System.out.println("CD [true]: " + collectionCard.getCollectionCard().getMonsterId() + " - " + collectionCard.getCollectionCard().getUid() + " - Card never played.");
            return true;
        }

        int currentBlock = smSettings.getLastBlock();
//        int cooldownPeriod = smSettings.getTransfer_cooldown_blocks();
        int cooldownPeriod = 150000;

        boolean checkCooldown = ((currentBlock - collectionCard.getCollectionCard().getLastTransferredBlock()) > cooldownPeriod);
//        boolean checkCooldown = (currentBlock > (collectionCard.getCollectionCard().getLastTransferredBlock() + cooldownPeriod));
//        System.out.println("CD [" + checkCooldown + "]: " + collectionCard.getCollectionCard().getMonsterId() + " - " + collectionCard.getCollectionCard().getUid() + " - " + currentBlock + " > " + collectionCard.getCollectionCard().getLastTransferredBlock() + " + " + cooldownPeriod + " (" + (collectionCard.getCollectionCard().getLastTransferredBlock() + cooldownPeriod) + ")");

        return checkCooldown;
    }

    private boolean checkValidCardForGameRules(RealizedCollectionCard card, TeamRequest teamRequest) {
        Set<GameRuleType> rules = teamRequest.getRuleset();
        int id = card.getCollectionCard().getMonsterId();
        MonsterBattleStats monsterBattleStats = card.getMonsterBattleStats();
        MonsterDetails monsterDetails = card.getMonsterDetails();

        if ((monsterBattleStats.getType().equals(MonsterType.SUMMONER))) {
            return true;
        }


        if ((rules.contains(GameRuleType.TAKING_SIDES)) && (monsterDetails.getColor().equals(ColorType.GRAY))) {
            return false;
        }
        if ((rules.contains(GameRuleType.RISE_OF_THE_COMMONS)) && (monsterDetails.getRarity() > 2)) {
            return false;
        }
        if ((rules.contains(GameRuleType.EVEN_STEVENS)) && (monsterDetails.getMonsterStats().getMana()[0] % 2 != 0)) {
            return false;
        }
        if ((rules.contains(GameRuleType.ODD_ONES_OUT)) && (monsterDetails.getMonsterStats().getMana()[0] % 2 == 0)) {
            return false;
        }
        if (rules.contains(GameRuleType.LOST_LEGENDARIES) && (monsterDetails.getRarity().equals(4))) {
            return false;
        }
        if (rules.contains(GameRuleType.LITTLE_LEAGUE) && (monsterDetails.getMonsterStats().getMana()[0] > 4)) {
            return false;
        }
        if ((rules.contains(GameRuleType.BROKEN_ARROWS) && (monsterBattleStats.isOfDamageType(DamageType.RANGED)))) {
            return false;
        }
        if ((rules.contains(GameRuleType.KEEP_YOUR_DISTANCE) && (monsterBattleStats.isOfDamageType(DamageType.ATTACK)))) {
            return false;
        }
        if ((rules.contains(GameRuleType.LOST_MAGIC) && (monsterBattleStats.isOfDamageType(DamageType.MAGIC)))) {
            return false;
        }
        if ((rules.contains(GameRuleType.UP_CLOSE_AND_PERSONAL)) && (!monsterBattleStats.isOfDamageType(DamageType.ATTACK))) {
            return false;
        }


        if (teamRequest.getLevelLimit().getId() > 2) {
            if (rules.contains(GameRuleType.BROKEN_ARROWS)) {
                switch (id) {
                    case 14:
                    case 32:
                        return false;
                }
            }

            if (rules.contains(GameRuleType.KEEP_YOUR_DISTANCE)) {
                switch (id) {
                    case 131:
                    case 119:
                    case 91:
                    case 118:
                        return false;
                }
            }
        }
        else {
            if (rules.contains(GameRuleType.UP_CLOSE_AND_PERSONAL)) {
                switch (id) {
                    case 131:
                    case 119:
                    case 91:
                    case 118:
                        return false;
                }
            }
        }

        return true;
    }

    private RealizedCollection filterForFoil(RealizedCollection collection, Foil foil) {
        if ((foil == null) || (foil == Foil.ALL)) {
            return collection;
        }

        if (foil.equals(Foil.GOLD_ONLY)) {
            List<RealizedCollectionCard> filtered = collection.getCards().stream()
                    .filter(collectionCard -> collectionCard.getCollectionCard().getGold())
                    .collect(Collectors.toList());

            return new RealizedCollection(collection.getPlayer(), filtered);
        }

        throw new RuntimeException("Not supported filtering on foil type: " + foil);

    }

    private RealizedCollection filterForEdition(RealizedCollection collection, Set<Integer> editions) {
        if ((editions == null) || (editions.size() == 0)) {
            return collection;
        }

        List<RealizedCollectionCard> filtered = collection.getCards().stream()
                .filter(collectionCard -> editions.contains(collectionCard.getCollectionCard().getEdition()))
                .collect(Collectors.toList());

        return new RealizedCollection(collection.getPlayer(), filtered);

    }

    private RealizedCollection filterForTournamentRules(RealizedCollection collection, TournamentRule tournamentRule) {
        if ((tournamentRule == null) || ((tournamentRule == TournamentRule.ALL_PLAYABLE))) {
//            System.out.println("All Playable");
            return collection;
        }

        List<RealizedCollectionCard> filtered;

        if (tournamentRule == TournamentRule.NO_LEGENDARY_SUMMONERS) {
            logger.info("No Legendary Summoners");

            filtered = collection.getCards().stream()
                    .filter(realizedCollectionCard -> !((realizedCollectionCard.getMonsterDetails().getType().equals(MonsterType.SUMMONER)) && (realizedCollectionCard.getMonsterDetails().getRarity().equals(4))))
                    .collect(Collectors.toList());

            return new RealizedCollection(collection.getPlayer(), filtered);
        }
        if (tournamentRule == TournamentRule.NO_LEGENDARIES) {
            logger.info("No Legendary");
            filtered = collection.getCards().stream()
                    .filter(collectionCard -> !collectionCard.getMonsterDetails().getRarity().equals(4))
                    .collect(Collectors.toList());

            return new RealizedCollection(collection.getPlayer(), filtered);
        }

        return collection;
    }
}
