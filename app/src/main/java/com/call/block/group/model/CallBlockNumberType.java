package com.call.block.group.model;

/**
 * Created by sourabh on 28/3/17.
 */

public enum CallBlockNumberType {
    STARTS_WITH,
    CONTAINS,
    ENDS_WITH;

    public String value() {
        return name();
    }

    public static CallBlockNumberType fromValue(String v) {
        return valueOf(v);
    }
}

