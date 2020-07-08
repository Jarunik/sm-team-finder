package org.slos.query;

import java.util.HashSet;
import java.util.Set;

public enum League {
    NOVICE (1, 0, 99, "Novice"),
    BRONZE (2, 100, 999, "Bronze"),
    SILVER (3, 1000,1899, "Silver"),
    GOLD (4, 1900,2799, "Gold"),
    DIAMOND (5, 2800,3699, "Diamond"),
    CHAMPION(6, 3700, 100000, "Champion");

    private int id;
    private int lowRange;
    private int highRange;
    private String name;

    League(int id, int lowRange, int highRange, String name) {
        this.id = id;
        this.lowRange = lowRange;
        this.highRange = highRange;
        this.name = name;
    }

    public static League fromName(String name) {
        for (League league : League.values()) {
            if (league.name.equals(name)) {
                return league;
            }
        }
        return null;
    }

    public static Integer getLeagueDistance(League leagueOne, League leagueTwo) {
        return Math.abs(leagueOne.id - leagueTwo.id);
    }

    public Set<League> getLeaguesFromDistance(League league, int distance) {
        Set<League> leagues = new HashSet<>();

        League up = getFromId(league.id + distance);
        if (up != null) {
            leagues.add(up);
        }

        League down = getFromId(league.id - distance);
        if (down != null) {
            leagues.add(down);
        }

        return leagues;
    }

    public static League getFromId(int id) {
        for (League league : League.values()) {
            if (league.id == id) {
                return league;
            }
        }
        return null;
    }

    public static League getFromRating(int rating) {

        for (League league : League.values()) {
            if ((league.lowRange <= rating) && (league.highRange >= rating)) {
                return league;
            }
        }
        return null;
    }

    public int getLowRange(League league) {
        return lowRange;
    }

    public int getHighRange(League league) {
        return highRange;
    }
}
