package org.slos;

import org.slos.battle.abilities.Ability;
import org.slos.battle.abilities.AbilityFactory;
import org.slos.battle.abilities.AbilityType;
import org.slos.battle.monster.BattleAttribute;
import org.slos.battle.monster.MonsterBattleStats;
import org.slos.splinterlands.collection.Collection;
import org.slos.splinterlands.collection.CollectionCard;
import org.slos.splinterlands.domain.monster.ColorType;
import org.slos.splinterlands.domain.monster.DamageType;
import org.slos.splinterlands.domain.monster.MonsterDetails;
import org.slos.splinterlands.domain.monster.MonsterDetailsList;
import org.slos.splinterlands.domain.monster.MonsterStats;
import org.slos.splinterlands.domain.monster.MonsterType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MonsterBattleStatsService {
    private MonsterDetailsList monsterDetailsList;
    private AbilityFactory abilityFactory;

    @Autowired
    public MonsterBattleStatsService(MonsterDetailsList monsterDetailsList, AbilityFactory abilityFactory) {
        this.monsterDetailsList = monsterDetailsList;
        this.abilityFactory = abilityFactory;
    }

    private Set<Integer> summoners = new HashSet<>();
    private Set<Integer> monsters = new HashSet<>();
    private Set<Integer> legendaries = new HashSet<>();

    public List<MonsterBattleStats> convertToActiveStats(Collection collection) {
        List<MonsterBattleStats> monsterBattleStats = new ArrayList<>();

        for (CollectionCard collectionCard : collection.getCards()) {
            monsterBattleStats.add(convert(collectionCard));
        }

        return monsterBattleStats;
    }

    public MonsterBattleStats convert(CollectionCard collectionCard) {
        Integer cardId = collectionCard.getCardDetailId();
        int level = collectionCard.getLevel();

        return getMonsterBattleStats(cardId, level);
    }

    public List<MonsterDetails> getMonsterBattleStatsForSplinter(ColorType colorType, Collection collection) {
        List<MonsterBattleStats> monsterBattleStatsListForSplinter = new ArrayList<>();
        List<MonsterDetails> detailsListOfColor = Arrays.stream(monsterDetailsList.getMonsterDetailsList())
                .filter(monsterDetails1 -> monsterDetails1.getColor().equals(colorType))
                .collect(Collectors.toList());

        return detailsListOfColor;
    }

    public MonsterDetails getMonsterDetals(int cardId) {
        return Arrays.stream(monsterDetailsList.getMonsterDetailsList())
                .filter(monsterDetails1 -> monsterDetails1.getId().equals(cardId))
                .findFirst().get();
    }

    public MonsterStats getMonsterBattleStats(int cardId) {
        MonsterDetails monsterDetails = Arrays.stream(monsterDetailsList.getMonsterDetailsList())
                .filter(monsterDetails1 -> monsterDetails1.getId().equals(cardId))
                .findFirst().get();
        return monsterDetails.getMonsterStats();
    }

    public MonsterBattleStats getMonsterBattleStats(int cardId, int level) {
        MonsterDetails monsterDetails = Arrays.stream(monsterDetailsList.getMonsterDetailsList())
                .filter(monsterDetails1 -> monsterDetails1.getId().equals(cardId))
                .findFirst().get();
        MonsterStats monsterStats = monsterDetails.getMonsterStats();
        MonsterType type = monsterDetails.getType();

        return getActiveMonsterStats(cardId, level, type, monsterStats);
    }

    public MonsterBattleStats getActiveMonsterStats(int cardId, int level, MonsterType type, MonsterStats monsterStats) {
        if (level == 0) {
            level = 1;
        }
        Integer mana = monsterStats.getMana()[level - 1];
        Integer armor = monsterStats.getArmor()[level - 1];
        Integer health = monsterStats.getHealth()[level - 1];
        Integer speed = monsterStats.getSpeed()[level - 1];
        List<Ability> abilities = null;

        if (type == MonsterType.SUMMONER) {
            abilities = getSummonerAbilities(monsterStats, level);
        }
        else {
            abilities = getAbilities(monsterStats, level);
        }

        Map<DamageType, BattleAttribute> damages = getAttackDamages(monsterStats, level);

        return new MonsterBattleStats(cardId, mana, type, damages, armor, health, speed, abilities);
    }

    public Map<DamageType, BattleAttribute> getAttackDamages(MonsterStats monsterStats, int level) {
        Map<DamageType, BattleAttribute> damages = new HashMap<>();

        Integer rangedDamage = monsterStats.getRanged()[level-1];
        Integer attackDamage = monsterStats.getAttack()[level-1];
        Integer magicDamage =  monsterStats.getMagic()[level-1];

        if (rangedDamage != null && rangedDamage > 0) {
            damages.put(DamageType.RANGED, new BattleAttribute(rangedDamage, true));
        }
        if (attackDamage != null && attackDamage > 0) {
            damages.put(DamageType.ATTACK, new BattleAttribute(attackDamage, true));
        }
        if (magicDamage != null && magicDamage > 0) {
            damages.put(DamageType.MAGIC, new BattleAttribute(magicDamage, true));
        }

        return damages;
    }

    public boolean isSummoner(CollectionCard collectionCard) {
        if (summoners.size() == 0) {
            for (MonsterDetails monsterDetails : monsterDetailsList.getSummoners()) {
                summoners.add(Integer.valueOf(monsterDetails.getId()));
            }
        }

        return summoners.contains(Integer.valueOf(collectionCard.getMonsterId()));
    }

    public boolean isLegendary(CollectionCard collectionCard) {
        if (legendaries.size() == 0) {
            for (MonsterDetails monsterDetails : monsterDetailsList.getMonsterDetailsList()) {
                if (monsterDetails.getRarity().equals(4)) {
                    legendaries.add(Integer.valueOf(monsterDetails.getId()));
                }
            }
        }

        return legendaries.contains(Integer.valueOf(collectionCard.getMonsterId()));

    }

    public boolean isMonster(CollectionCard collectionCard) {
        if (monsters.size() == 0) {
            for (MonsterDetails monsterDetails : monsterDetailsList.getMonsters()) {
                monsters.add(monsterDetails.getId());
            }
        }
        return monsters.contains(collectionCard.getMonsterId());
    }

    private List<Ability> getSummonerAbilities(MonsterStats monsterStats, int level) {
        List<Ability> abilityTypes = new ArrayList<>();

        if (monsterStats.getRanged()[0] != 0) {
            abilityTypes.add(abilityFactory.getSummonerAbility(AbilityType.SUMMONER_RANGED, monsterStats.getRanged()[0]));
        }
        if (monsterStats.getAttack()[0] != 0) {
            abilityTypes.add(abilityFactory.getSummonerAbility(AbilityType.SUMMONER_ATTACK, monsterStats.getAttack()[0]));
        }
        if (monsterStats.getMagic()[0] != 0) {
            abilityTypes.add(abilityFactory.getSummonerAbility(AbilityType.SUMMONER_MAGIC, monsterStats.getMagic()[0]));
        }
        if (monsterStats.getHealth()[0] != 0) {
            abilityTypes.add(abilityFactory.getSummonerAbility(AbilityType.SUMMONER_HEALTH, monsterStats.getHealth()[0]));
        }
        if (monsterStats.getArmor()[0] != 0) {
            abilityTypes.add(abilityFactory.getSummonerAbility(AbilityType.SUMMONER_ARMOR, monsterStats.getArmor()[0]));
        }
        if (monsterStats.getSpeed()[0] != 0) {
            abilityTypes.add(abilityFactory.getSummonerAbility(AbilityType.SUMMONER_SPEED, monsterStats.getSpeed()[0]));
        }

        for (int i = 0; i < level; i++) {
            for (AbilityType ability : monsterStats.getAbilities()[i]) {
                if (ability != null) {
                    abilityTypes.add(abilityFactory.getAbility(ability));
                }
            }
        }

        return abilityTypes;
    }

    private List<Ability> getAbilities(MonsterStats monsterStats, int level) {
        List<AbilityType> abilityTypes = new ArrayList<>();
        int levelIndex = 0;
        for (AbilityType[] abilityTypes1 : monsterStats.getAbilities()) {
            if (levelIndex < level) {
                for (AbilityType abilityType : abilityTypes1) {
                    if (abilityType != null) {
                        abilityTypes.add(abilityType);
                    }
                }
                levelIndex++;
            }
        }
        return getAbilityFromType(abilityTypes);
    }

    private List<Ability> getAbilityFromType(List<AbilityType> abilityTypeList) {
        List<Ability> abilities = new ArrayList<>();

        for (AbilityType abilityType : abilityTypeList) {
            abilities.add(abilityFactory.getAbility(abilityType));
        }

        return abilities;
    }

    private DamageType getAttackType(MonsterStats monsterStats) {
        if ((Arrays.stream(monsterStats.getAttack()).filter(v -> v != null && v > 0).count()) > 0) {
            return DamageType.ATTACK;
        }
        if ((Arrays.stream(monsterStats.getRanged()).filter(v -> v != null && v > 0).count()) > 0) {
            return DamageType.RANGED;
        }
        if ((Arrays.stream(monsterStats.getMagic()).filter(v -> v != null && v > 0).count()) > 0) {
            return DamageType.MAGIC;
        }

        return DamageType.NONE;
    }

    private Integer getAttackValue(MonsterStats monsterStats, DamageType damageType, int level) {
        if (damageType == DamageType.RANGED) {
            return monsterStats.getRanged()[level-1];
        }
        else if (damageType == DamageType.ATTACK) {
            return monsterStats.getAttack()[level-1];
        }
        else if (damageType == DamageType.MAGIC) {
            return monsterStats.getMagic()[level-1];
        }

        return 0;
    }

    public Integer getCardId(String uid) {
        String[] parsed = uid.split("-");

        if (parsed.length == 3) {
            return Integer.valueOf(parsed[1]);
        }
        else {
            return null;
        }
    }
}