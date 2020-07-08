package org.slos.splinterlands.domain.monster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum ColorType {

    @JsonProperty("Gray")
    GRAY("Gray", 0),
    @JsonProperty("Red")
    RED("Red", 1),
    @JsonProperty("Blue")
    BLUE("Blue", 2),
    @JsonProperty("Green")
    GREEN("Green", 3),
    @JsonProperty("White")
    WHITE("White", 4),
    @JsonProperty("Black")
    BLACK("Black", 5),
    @JsonProperty("Gold")
    GOLD("Gold", 6);

    private String id;
    private Integer intId;

    ColorType(String id, Integer intId) {
        this.id = id;
        this.intId = intId;
    }

    public String id() {
        return id;
    }

    public Integer intId() {
        return intId;
    }

    @JsonCreator
    public static ColorType forColor(String color) {
        for(ColorType c: values()) {
            if(c.id().equals(color)) {
                return c;
            }
        }
        return null;
    }
}
