package com.block.callblocker.blocksilent.model;

/**
 * Created by sourabh on 28/3/17.
 * This class contains the enum values for blocking types.
 */

public enum CallBlockNumberType {
    STARTS_WITH,
    CONTAINS,
    ENDS_WITH;

    public String value() {
        return name();
    }

//    public static CallBlockNumberType fromValue(String v) {
//        return valueOf(v);
//    }
}

