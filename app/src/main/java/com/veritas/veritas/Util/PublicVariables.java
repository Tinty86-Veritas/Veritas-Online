package com.veritas.veritas.Util;

public class PublicVariables {
    private static final String[] modes = new String[] {
            "Fun", "Soft", "Hot", "Extreme", "Madness"
    };

    private static final String[] games = new String[] {
            "Truth", "Dare", "NeverEver"
    };

    public static final String TRUTH = "truth";
    public static final String DARE = "dare";
    public static final String NEVEREVER = "neverEver";

    public static String[] getModes() {
        return modes;
    }

    public static String[] getGames() {
        return games;
    }
}
