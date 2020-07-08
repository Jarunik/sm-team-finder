package org.slos;

import org.slos.ranking.RankingService;
import org.junit.jupiter.api.Test;


//@RunWith(SpringRunner.class)
//@FunctionalSpringBootTest
//@AutoConfigureWebTestClient
public class RankServiceTests implements TestHelper {

//    @Autowired
    private RankingService rankingService;


    @Test
    public void itShouldRank() {

//        int topOne[][] = {
//                {110, 2},
//                {82, 3},
//                {17, 5},
//                {83, 6},
//                {98, 3},
//                {19, 5},
//                {64, 3}};
//        int topTwo[][] = {
//                {70, 4},
//                {13, 4},
//                {14, 4},
//                {15, 4},
//                {17, 4},
//                {18, 4},
//                {19, 4}};
//        int topThree[][] = {
//                {70, 4},
//                {23, 4},
//                {24, 4},
//                {25, 4},
//                {26, 4},
//                {28, 4},
//                {29, 4}};
//        int topFour[][] = {
//                {110, 3},
//                {82, 2},
//                {6, 4},
//                {8, 4},
//                {66, 4},
//                {1, 5},
//                {10, 3}};
//        int topFive[][] = {
//                {16, 6},
//                {82, 3},
//                {64, 6},
//                {91, 7},
//                {28, 6},
//                {85, 3},
//                {102, 2},
//                {131, 3}};
//        int topSix[][] = {
//                {70, 4},
//                {23, 4},
//                {24, 4},
//                {25, 4},
//                {26, 4},
//                {28, 4},
//                {29, 4}};
//
//        int bottomOne[][] = {
//                {110, 2},
//                {82, 2},
//                {64, 8},
//                {95, 6},
//                {91, 10},
//                {32, 6},
//                {24, 10},
//                {4, 6}};
//        int bottomTwo[][] = {
//                {49, 8},
//                {131, 8},
//                {99, 8},
//                {45, 10},
//                {47, 10},
//                {51, 8}};
//        int bottomThree[][] = {
//                {110, 2},
//                {82, 2},
//                {6, 4},
//                {8, 4},
//                {66, 4},
//                {1, 5},
//                {10, 3}};
//        int bottomFour[][] = {
//                {110, 2},
//                {82, 3},
//                {17, 5},
//                {83, 6},
//                {98, 3},
//                {19, 5},
//                {64, 3}};
//        int bottomFive[][] = {
//                {110, 4},
//                {13, 4},
//                {14, 4},
//                {15, 4},
//                {17, 4},
//                {18, 4},
//                {19, 4}};
//        int bottomSix[][] = {
//                {70, 4},
//                {23, 4},
//                {24, 4},
//                {25, 4},
//                {26, 4},
//                {28, 4},
//                {29, 4}};
//
//        List<Team> topTeam = new ArrayList<>();
//        topTeam.add(new Team(topOne));
//        topTeam.add(new Team(topTwo));
//        topTeam.add(new Team(topThree));
//        topTeam.add(new Team(topFour));
//        topTeam.add(new Team(topTwo));
//        topTeam.add(new Team(topThree));
//        topTeam.add(new Team(topFour));
//        topTeam.add(new Team(topFive));
//        topTeam.add(new Team(topFive));
//        topTeam.add(new Team(topFive));
//
//        List<Team> bottomTeam = new ArrayList<>();
//        bottomTeam.add(new Team(bottomOne));
//        bottomTeam.add(new Team(bottomTwo));
//        bottomTeam.add(new Team(bottomThree));
//        bottomTeam.add(new Team(bottomFour));
//        bottomTeam.add(new Team(bottomFive));
//        bottomTeam.add(new Team(bottomThree));
//        bottomTeam.add(new Team(bottomFour));
//        bottomTeam.add(new Team(bottomFive));
//        bottomTeam.add(new Team(bottomFive));
//        bottomTeam.add(new Team(bottomFive));
//        bottomTeam.add(new Team(bottomOne));
//        bottomTeam.add(new Team(bottomTwo));
//        bottomTeam.add(new Team(bottomThree));
//        bottomTeam.add(new Team(bottomFour));
//        bottomTeam.add(new Team(bottomFive));
//        bottomTeam.add(new Team(bottomThree));
//        bottomTeam.add(new Team(bottomFour));
//        bottomTeam.add(new Team(bottomFive));
//        bottomTeam.add(new Team(bottomFive));
//        bottomTeam.add(new Team(bottomFive));


//        List<Team> topTeam = new ArrayList<>();
//        topTeam.add(new Team(topSix));
//        List<Team> bottomTeam = new ArrayList<>();
//        bottomTeam.add(new Team(bottomSix));

        long start = System.currentTimeMillis();
//        System.out.println("Best deck: " + rankingService.getBestDeckFor(topTeam, bottomTeam, new ArrayList<>(), 250).toJson());
        System.out.println("Time: " + (System.currentTimeMillis() - start) + "ms");
    }
}
