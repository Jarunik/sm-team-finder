package org.slos;

import org.slos.battle.monster.BattleAttribute;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BattleAttributeValueTests {

    @Test
    public void itShouldSetBaseAndBuffValues() {
        BattleAttribute battleAttribute = new BattleAttribute(10);

        assertEquals(Integer.valueOf(10), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(10), battleAttribute.getValue());
    }

    @Test
    public void itShouldRemovePartialValue() {
        BattleAttribute battleAttribute = new BattleAttribute(10, false);

        battleAttribute.removeFromValue(5);

        assertEquals(Integer.valueOf(10), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(5), battleAttribute.getValue());
    }

    @Test
    public void itShouldRemovePartialValueWhenOneChecked() {
        BattleAttribute battleAttribute = new BattleAttribute(10, true);

        battleAttribute.removeFromValue(5);

        assertEquals(Integer.valueOf(10), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(5), battleAttribute.getValue());
    }

    @Test
    public void itShouldAddBuffAmount() {
        BattleAttribute battleAttribute = new BattleAttribute(10, true);

        battleAttribute.addToBuffValue(5);

        assertEquals(Integer.valueOf(15), battleAttribute.getValue());
        assertEquals(Integer.valueOf(5), battleAttribute.getBuffValue());
        assertEquals(Integer.valueOf(10), battleAttribute.getBaseValue());
    }

    @Test
    public void itShouldRemoveFromBuffAmount() {
        BattleAttribute battleAttribute = new BattleAttribute(10, true);
        battleAttribute.addToBuffValue(10);

        battleAttribute.removeFromBuffValue(5);

        assertEquals(Integer.valueOf(15), battleAttribute.getValue());
        assertEquals(Integer.valueOf(5), battleAttribute.getBuffValue());
        assertEquals(Integer.valueOf(10), battleAttribute.getBaseValue());
    }

    @Test
    public void itShouldRemoveFromBuffAmountWhenNotOneChecked() {
        BattleAttribute battleAttribute = new BattleAttribute(10, false);
        battleAttribute.addToBuffValue(10);

        battleAttribute.removeFromBuffValue(5);

        assertEquals(Integer.valueOf(15), battleAttribute.getValue());
        assertEquals(Integer.valueOf(5), battleAttribute.getBuffValue());
        assertEquals(Integer.valueOf(10), battleAttribute.getBaseValue());
    }

    @Test
    public void ItShouldNotAllowBuffToLowerToZeroIfOneChecked() {
        BattleAttribute battleAttribute = new BattleAttribute(1, true);

        battleAttribute.removeFromBuffValue(10);

        assertEquals(Integer.valueOf(1), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(1), battleAttribute.getValue());
    }

    @Test
    public void ItShouldIncreaseIfRemovingNegativeBuff() {
        BattleAttribute battleAttribute = new BattleAttribute(1, true);
        battleAttribute.addToBuffValue(-1);

        battleAttribute.removeFromBuffValue(-1);

        assertEquals(Integer.valueOf(1), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(1), battleAttribute.getValue());
    }

    @Test
    public void ItShouldIncreaseIfRemovingNegativeBuff2() {
        BattleAttribute battleAttribute = new BattleAttribute(2, true);
        battleAttribute.removeFromBuffValue(1);
        assertEquals(Integer.valueOf(1), battleAttribute.getValue());

        battleAttribute.addToBuffValue(1);

        assertEquals(Integer.valueOf(2), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(2), battleAttribute.getValue());
    }

    @Test
    public void ItShouldNotIncreaseIfDecreasedPastOneMinimum() {
        BattleAttribute battleAttribute = new BattleAttribute(2, true);
        battleAttribute.addToBuffValue(-1);
        battleAttribute.addToBuffValue(-1);
        battleAttribute.addToBuffValue(-1);

        battleAttribute.addToBuffValue(1);

        assertEquals(Integer.valueOf(2), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(1), battleAttribute.getValue());
    }



    @Test
    public void multipleBuffsShouldCancelOutWhenNegativeBalanceFirst() {
        BattleAttribute battleAttribute = new BattleAttribute(2, true);
        System.out.println("Value: " + battleAttribute.getValue());
        battleAttribute.addToBuffValue(-1);
        System.out.println("Value: " + battleAttribute.getValue());
        battleAttribute.addToBuffValue(-1);
        System.out.println("Value: " + battleAttribute.getValue());
        battleAttribute.addToBuffValue(-1);
        System.out.println("Value: " + battleAttribute.getValue());

        battleAttribute.addToBuffValue(1);
        System.out.println("Value: " + battleAttribute.getValue());
        battleAttribute.addToBuffValue(1);
        System.out.println("Value: " + battleAttribute.getValue());
        battleAttribute.addToBuffValue(1);
        System.out.println("Value: " + battleAttribute.getValue());

        assertEquals(Integer.valueOf(2), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(2), battleAttribute.getValue());
    }

    @Test
    public void itShouldRemoveFromBuffFirstThenValue() {
        BattleAttribute battleAttribute = new BattleAttribute(5, true);
        battleAttribute.addToBuffValue(1);

        battleAttribute.removeFromValue(3);

        assertEquals(Integer.valueOf(3), battleAttribute.getValue());
    }

    @Test
    public void itShouldAddToBaseValueFine() {
        BattleAttribute battleAttribute = new BattleAttribute(100, true);

        battleAttribute.addToBaseValue(1);

        assertEquals(Integer.valueOf(101), battleAttribute.getValue());
    }

    @Test
    public void ItShouldSetValueToZeroIfRemovingWithNegativeBuffWhenOneChecked() {
        BattleAttribute battleAttribute = new BattleAttribute(10, true);
        battleAttribute.removeFromBuffValue(5);

        battleAttribute.removeFromValue(5);

        assertEquals(Integer.valueOf(10), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(0), battleAttribute.getValue());
    }

    @Test
    public void ItShouldSetValueToZeroIfRemovingWithNegativeBuff() {
        BattleAttribute battleAttribute = new BattleAttribute(10, false);
        battleAttribute.removeFromBuffValue(5);

        battleAttribute.removeFromValue(5);

        assertEquals(Integer.valueOf(10), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(0), battleAttribute.getValue());
    }

    @Test
    public void ItShouldRemoveBuffToCorrectValueWhenOneChecked() {
        BattleAttribute battleAttribute = new BattleAttribute(1, true);
        battleAttribute.removeFromBuffValue(1);
        assertEquals(Integer.valueOf(1), battleAttribute.getValue());

        battleAttribute.addToBuffValue(1);

        assertEquals(Integer.valueOf(1), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(0), battleAttribute.getBuffValue());
        assertEquals(Integer.valueOf(1), battleAttribute.getValue());
    }

    @Test
    public void ItShouldRemoveMultipleBuffValuesToCorrectValueWhenOneChecked() {
        BattleAttribute battleAttribute = new BattleAttribute(1, true);
        battleAttribute.removeFromBuffValue(1);
        battleAttribute.removeFromBuffValue(1);
        battleAttribute.removeFromBuffValue(1);

        battleAttribute.addToBuffValue(1);

        assertEquals(Integer.valueOf(1), battleAttribute.getBaseValue());
        assertEquals(Integer.valueOf(1), battleAttribute.getValue());
    }
}
