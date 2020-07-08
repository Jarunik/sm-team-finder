package org.slos.services.strategy;

import java.math.BigInteger;
import java.util.Arrays;

public class CountTheCoinsStrategy {

    private static BigInteger countChanges(int amount, int[] coins){
        final int n = coins.length;

        int cycle = 0;

        for (int c : coins) {
            if (c <= amount && c >= cycle) {
                cycle = c + 1;
            }
        }

        cycle *= n;
        BigInteger[] table = new BigInteger[cycle];
        Arrays.fill(table, 0, n, BigInteger.ONE);
        Arrays.fill(table, n, cycle, BigInteger.ZERO);

        int pos = n;
        for (int s = 1; s <= amount; s++) {
            for (int i = 0; i < n; i++) {

                if (i == 0 && pos >= cycle)
                    pos = 0;
                if (coins[i] <= s) {
                    final int q = pos - (coins[i] * n);
                    table[pos] = (q >= 0) ? table[q] : table[q + cycle];
                }
                if (i != 0) {
                    table[pos] = table[pos].add(table[pos - 1]);
                }
                pos++;
                System.out.println("Position: " + pos);
                System.out.println("i: " + i + " - " + coins[i]);
            }
        }

        return table[pos - 1];
    }

    public static void main(String[] args) {
        final int[][] coinsUsEu = {
                {3, 3, 4, 2, 5, 7, 4, 5, 6, 10, 8, 3, 6, 4, 4, 5, 4, 7, 5, 10, 8}
        };

        for (int[] coins : coinsUsEu) {
            System.out.println(countChanges(13, coins) + "\n");
        }
    }
}
