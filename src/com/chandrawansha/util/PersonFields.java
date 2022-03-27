package com.chandrawansha.util;

public enum PersonFields {
    FIRST_NAME(0),
    LAST_NAME(1),
    ADDRESS(2),
    PHONE_NUMBER(3),
    EMAIL(4),
    NIC_NUMBER(5);

    private final int index;

    PersonFields(int i) {
        index  = i;
    }

    // getter for index
    public int getIndex() {
        return index;
    }
}
