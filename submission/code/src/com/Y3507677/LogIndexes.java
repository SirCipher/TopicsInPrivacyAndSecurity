package com.Y3507677;

public enum LogIndexes {

    IP(0), DATE(1), URI(2), STATUS(3), SIZE(4);

    public int index;

    LogIndexes(int index) {
        this.index = index;
    }
}