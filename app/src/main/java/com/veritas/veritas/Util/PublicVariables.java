package com.veritas.veritas.Util;

public class PublicVariables {
    private static final String[] modes = new String[] {
            "Fun", "Soft", "Hot", "Extreme", "Madness"
    };

    private static final String[] games = new String[] {
            "Truth", "Dare", "NeverEver"
    };

    public static String[] getModes() {
        return modes;
    }

    public static String[] getGames() {
        return games;
    }
}
