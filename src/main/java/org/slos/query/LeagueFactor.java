package org.slos.query;

public class LeagueFactor {

    public static float getWeight(int value) {
        if (value > leaguesOff.length) {
            return leaguesOff[leaguesOff.length - 1];
        }

        return leaguesOff[value];
    }

    private static final float[] leaguesOff = new float[] {
            1,  // 1 // league difference
            .7f,// 2
            .5f,// 3
            .3f,// 4
            .2f,// 5
            .1f // 6
    };
}
