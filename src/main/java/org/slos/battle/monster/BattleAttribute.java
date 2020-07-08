package org.slos.battle.monster;

import org.slos.util.ToJson;

public class BattleAttribute implements ToJson {
    private int baseValue;
    private Integer value;
    private Integer baseBuffValue = 0;
    private Integer buffValue = 0;
    private boolean oneCheck;

    public BattleAttribute(int value, boolean oneCheck) {
        this.oneCheck = oneCheck;
        this.baseValue = value;
        this.value = value;
    }

    public BattleAttribute(int value) {
        this(value, true);
    }

    public void addToBuffValue(Integer buffAmountToAdd) {
        if (buffAmountToAdd < 0) {
            removeFromBuffValue(buffAmountToAdd * -1);
            return;
        }

        baseBuffValue += buffAmountToAdd;
        buffValue += buffAmountToAdd;

        if (buffValue > baseBuffValue) {
            buffValue = baseBuffValue;
        }

        if (buffValue < 0) {
            buffValue = 0;
        }
    }

//    public void p() {
//        System.out.println(String.format("VB: %s - V: %s - BB: %s - BV: %s - Value: %s", baseValue, value, baseBuffValue, buffValue, getValue()));
//    }

    public void removeFromBuffValue(Integer buffAmountToRemove) {
        if (buffAmountToRemove < 0) {
            addToBuffValue(buffAmountToRemove * -1);
            return;
        }

//        boolean didRemoveFromBuff = false;
//        if (buffValue > 0) {
//            didRemoveFromBuff = true;
//        }

        baseBuffValue -= buffAmountToRemove;
//        buffValue -= buffAmountToRemove;

        if (buffValue > baseBuffValue) {
            buffValue = baseBuffValue;
        }

        if (buffValue < 0) {
            buffValue = 0;
        }
    }

    public Integer getValue() {
        int returnValue = Math.min(value, baseValue) + Math.min(buffValue, baseBuffValue);

        if (isOneCheck() && returnValue <= 0) {
            if (value > 0) {
                return 1;
            }
        }

        if (returnValue < 0) {
            return 0;
        }

        return returnValue;
    }

    public boolean isOneCheck() {
        return oneCheck;
    }

    public void removeAll() {
        value = 0;
        buffValue = 0;
        baseBuffValue = 0;
    }

    public void removeFromValue(Integer valueAmountToRemove) {
        int removedFromBuff = 0;
        int startValue = getValue();

        if (buffValue > 0) {
            if (buffValue < valueAmountToRemove) {
                removedFromBuff = buffValue;
                buffValue = 0;
            }
            else {
                buffValue -= valueAmountToRemove;
                removedFromBuff = valueAmountToRemove;
            }
        }

        int toRemoveFromValue = valueAmountToRemove - removedFromBuff;
        value -= toRemoveFromValue;

        if (startValue - toRemoveFromValue <= 0) {
            value = 0;
        }
    }

    public void addToValue(Integer valueAmountToAdd) {
        if (value == baseValue) {
            if (buffValue == baseBuffValue) {
                return;
            }

            buffValue += valueAmountToAdd;
            if (buffValue > baseBuffValue) {
                buffValue = baseBuffValue;
            }
        }
        else if (value + valueAmountToAdd <= baseValue) {
            value += valueAmountToAdd;
        }
        else {
            int amountAddedToValue = baseValue - value;
            value += amountAddedToValue;

            buffValue += valueAmountToAdd - amountAddedToValue;

            if (buffValue > baseBuffValue) {
                buffValue = baseBuffValue;
            }
        }
    }

    public void addToBaseValue(Integer valueAmountToAdd) {
        baseValue += valueAmountToAdd;
        value += valueAmountToAdd;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getBaseValue() {
        return baseValue;
    }

    public Integer getBuffBaseValue() {
        return baseBuffValue;
    }

    public void setBaseValue(int baseValue) {
        this.baseValue = baseValue;
    }

    public Integer getBuffValue() {
        return buffValue;
    }

    public void setBuffValue(Integer buffValue) {
        this.buffValue = buffValue;
    }

    public void setBuffBaseAmount(Integer buffAmount) {
        this.baseBuffValue = buffAmount;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Integer) {
            return value.equals(other);
        }

        else if (other instanceof BattleAttribute) {
            BattleAttribute otherAdjustableInteger = (BattleAttribute) other;

            return value.equals(otherAdjustableInteger.value);
        }

        return false;
    }

    @Override
    public String toString() {
        return "" + getValue();
    }
}
