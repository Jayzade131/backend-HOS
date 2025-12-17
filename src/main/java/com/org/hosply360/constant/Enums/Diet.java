package com.org.hosply360.constant.Enums;

public enum Diet {
    SOFT_DIET("Soft Diet"),
    NORMAL_DIET("Normal Diet"),
    LIQUID_DIET("Liquid Diet"),
    RT_FEED("RT Feed"),
    HOME_DIET("Home Diet"),
    NIL_BY_MOUTH("Nil By Mouth");

    private final String displayName;
    Diet(String displayName) {this.displayName = displayName;}
    public String getName() {return displayName;}
}