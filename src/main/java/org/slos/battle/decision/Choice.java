package org.slos.battle.decision;

public class Choice<Result> {

    private final int weight;
    private final Result result;
    private final int id;

    public Choice(int id, int weight, Result result) {
        if (weight <= 0) {
            throw new IllegalStateException("A choice weight must be greater than 0.");
        }
        this.id = id;
        this.weight = weight;
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public int getWeight() {
        return weight;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "weight=" + weight +
                ", result=" + result +
                ", id='" + id + '\'' +
                '}';
    }
}
