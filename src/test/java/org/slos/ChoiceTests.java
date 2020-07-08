package org.slos;

import org.slos.battle.decision.Choice;
import org.slos.battle.decision.ChoiceGate;
import org.slos.battle.decision.ChoiceGateFactory;
import org.slos.battle.decision.MasterChoiceContext;
import org.slos.battle.decision.strategy.ChoiceStrategyMode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class ChoiceTests {

    @Test
    public void itShouldDecideUsingHisory() {
        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        ChoiceGateFactory choiceGateFactory = masterChoiceContext.getChoiceGateFactory();

        ChoiceGate<Boolean> choiceGate = choiceGateFactory.buildGate(new ChoiceGateFactory.Configuration<Boolean>()
                .setGateId("Test")
                .setChoiceMode(ChoiceStrategyMode.WEIGHT_ENFORCED)
                .addChoice(new Choice(1, 2, new Boolean(true)))
                .addChoice(new Choice(0, 2, new Boolean(false)))
        );

        for (int i = 0; i < 100; i++) {
            System.out.println("i: " + i);
            assertTrue(choiceGate.getResult());
            assertFalse(choiceGate.getResult());
        }
    }

    @Test
    public void itShouldGetNewGateForNestedDecisions() {
        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        ChoiceGateFactory choiceGateFactory = masterChoiceContext.getChoiceGateFactory();

        ChoiceGate gate1 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate2 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        assertNotSame(gate1, gate2);
    }

    @Test
    public void itShouldStartOverWithPreviousHistory() {
        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        ChoiceGateFactory choiceGateFactory = masterChoiceContext.getChoiceGateFactory();

        ChoiceGate gate1 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate2 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate3 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        masterChoiceContext.reset();
        ChoiceGate gateA = getGateAndExecuteToExpect("TEST", false, choiceGateFactory);
        ChoiceGate gateB = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gateC = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        assertSame(gate1, gateA);
        assertNotSame(gate2, gateB);
        assertNotSame(gate2, gateC);
        assertNotSame(gate3, gateC);
        masterChoiceContext.reset();
        ChoiceGate gateI = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gateII = getGateAndExecuteToExpect("TEST", false, choiceGateFactory);
        ChoiceGate gateIII = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        assertSame(gate1, gateI);
        assertSame(gate2, gateII);
        masterChoiceContext.reset();
        ChoiceGate gateX = getGateAndExecuteToExpect("TEST", false, choiceGateFactory);
        ChoiceGate gateY = getGateAndExecuteToExpect("TEST", false, choiceGateFactory);
        ChoiceGate gateZ = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        assertSame(gateI, gateX);
    }

    @Test
    public void itShouldHandleMultipleDifferentGates() {
        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        ChoiceGateFactory choiceGateFactory = masterChoiceContext.getChoiceGateFactory();

        ChoiceGate gate1 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate2 = getGateAndExecuteToExpect("TEST2", true, choiceGateFactory);
        ChoiceGate gate3 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate4 = getGateAndExecuteToExpect("TEST2", true, choiceGateFactory);

        masterChoiceContext.reset();

        ChoiceGate gate12 = getGateAndExecuteToExpect("TEST", false, choiceGateFactory);
        ChoiceGate gate22 = getGateAndExecuteToExpect("TEST2", true, choiceGateFactory);
        ChoiceGate gate32 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate42 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        assertSame(gate1, gate12);
        assertNotSame(gate2, gate12);

        masterChoiceContext.reset();

        ChoiceGate gate13 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate23 = getGateAndExecuteToExpect("TEST2", false, choiceGateFactory);
        ChoiceGate gate33 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate43 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        assertSame(gate1, gate13);
        assertSame(gate2, gate23);

        masterChoiceContext.reset();

        ChoiceGate gate14 = getGateAndExecuteToExpect("TEST", false, choiceGateFactory);
        ChoiceGate gate24 = getGateAndExecuteToExpect("TEST2", false, choiceGateFactory);
        ChoiceGate gate34 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate44 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        assertSame(gate22, gate24);

        masterChoiceContext.reset();

        ChoiceGate gate15 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        ChoiceGate gate25 = getGateAndExecuteToExpect("TEST2", true, choiceGateFactory);
        ChoiceGate gate35 = getGateAndExecuteToExpect("TEST", false, choiceGateFactory);
        ChoiceGate gate45 = getGateAndExecuteToExpect("TEST", true, choiceGateFactory);
        assertSame(gate3, gate35);
    }

    @Test
    public void itShouldntPersistPreviousChoices() {
        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        ChoiceGateFactory choiceGateFactory = masterChoiceContext.getChoiceGateFactory();

        Object one = new Object();
        Object two = new Object();
        Set firstSet = new HashSet();
        firstSet.add(one);
        firstSet.add(two);

        ChoiceGate<Object> testGate = createChoiceGateWithChoices("TEST3", choiceGateFactory, new Choice<Object>(1, 1, one), new Choice(2, 1, two));
        Object one1 = testGate.getResult();
        Object two1 = testGate.getResult();
        assertTrue(firstSet.contains(one1));
        assertTrue(firstSet.contains(two1));
        masterChoiceContext.reset();

        Object oneA = new Object();
        Object twoA = new Object();
        Set secondSet = new HashSet();
        secondSet.add(oneA);
        secondSet.add(twoA);
        ChoiceGate<Object> testGate2 = createChoiceGateWithChoices("TEST3", choiceGateFactory, new Choice<Object>(1, 1, oneA), new Choice(2, 1, twoA));
        Object oneB = testGate2.getResult();
        Object twoB = testGate2.getResult();
        assertFalse(secondSet.contains(one1));
        assertFalse(secondSet.contains(two1));
        assertFalse(firstSet.contains(oneB));
        assertFalse(firstSet.contains(twoB));
        assertTrue(secondSet.contains(oneB));
        assertTrue(secondSet.contains(twoB));
        masterChoiceContext.reset();

        Object oneX = new Object();
        Object twoX = new Object();
        Set thirdSet = new HashSet();
        thirdSet.add(oneX);
        thirdSet.add(twoX);
        ChoiceGate<Object> testGate3 = createChoiceGateWithChoices("TEST3", choiceGateFactory, new Choice<Object>(1, 1, oneX), new Choice(2, 1, twoX));
        Object oneY = testGate3.getResult();
        Object twoY = testGate3.getResult();
        assertTrue(thirdSet.contains(oneY));
        assertTrue(thirdSet.contains(twoY));
        assertFalse(secondSet.contains(oneY));
        assertFalse(secondSet.contains(twoY));
        assertFalse(firstSet.contains(oneY));
        assertFalse(firstSet.contains(twoY));
        masterChoiceContext.reset();

        System.out.println(masterChoiceContext.toJson());
    }

    @Test
    public void itShouldHandleArrayPermutationResultsX() {
        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        ChoiceGateFactory choiceGateFactory = masterChoiceContext.getChoiceGateFactory();

        List<String> choices = new ArrayList<>();
        choices.add("One");
        choices.add("Two");
        choices.add("Three");

        ChoiceGate permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(" -- BREAK --");
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        masterChoiceContext.reset();
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        System.out.println(" -- BREAK --");
    }

    @Test
    public void itShouldHandleArrayPermutationResults() {
        MasterChoiceContext masterChoiceContext = new MasterChoiceContext();
        ChoiceGateFactory choiceGateFactory = masterChoiceContext.getChoiceGateFactory();

        List<String> choices = new ArrayList<>();
        choices.add("One");
        choices.add("Two");
        choices.add("Three");

        ChoiceGate permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);

        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(" -- CHECK --");
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        System.out.println(" -- CHECK --");

        masterChoiceContext.reset();

        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        permutationChoiceGate = getPermutationGate("Permutation", choices, choiceGateFactory);
        System.out.println(permutationChoiceGate.getResult());
        System.out.println(" -- CHECK --");
    }

    private ChoiceGate getPermutationGate(String id, List<String> choices, ChoiceGateFactory choiceGateFactory) {
        ChoiceGateFactory.Configuration<Choice<String>> configuration = new ChoiceGateFactory.Configuration<Boolean>()
                .setGateId(id)
                .setChoiceMode(ChoiceStrategyMode.RANDOM_WEIGHTED)
                .returnPermutationOf(choices);

        return choiceGateFactory.buildGate(configuration);
    }

    private ChoiceGate createChoiceGateWithChoices(String id, ChoiceGateFactory choiceGateFactory, Choice... choices) {
        ChoiceGateFactory.Configuration configuration = new ChoiceGateFactory.Configuration<Boolean>()
                .setGateId(id)
                .setChoiceMode(ChoiceStrategyMode.RANDOM_WEIGHTED);

        for (Choice choice : choices) {
            configuration.addChoice(choice);
        }

        ChoiceGate<Boolean> choiceGate = choiceGateFactory.buildGate(configuration);

        return choiceGate;
    }

    private ChoiceGate getGateAndExecuteToExpect(String id, Boolean expect, ChoiceGateFactory choiceGateFactory) {
        return getGateAndExecuteToExpect(id, expect, choiceGateFactory, ChoiceStrategyMode.WEIGHT_ENFORCED);
    }
    private ChoiceGate getGateAndExecuteToExpect(String id, Boolean expect, ChoiceGateFactory choiceGateFactory, ChoiceStrategyMode choiceStrategyMode) {
        ChoiceGate<Boolean> choiceGate = choiceGateFactory.buildGate(new ChoiceGateFactory.Configuration<Boolean>()
                .setGateId(id)
                .setChoiceMode(choiceStrategyMode)
                .addChoice(new Choice(1, 1, new Boolean(true)))
                .addChoice(new Choice(0, 1, new Boolean(false)))
        );

//        assertEquals(expect, choiceGate.getResult());

        return choiceGate;
    }
}
