package com.veritas.veritas.Util;

public class PublicVariables {

    public static final String MODE_FUN = "Fun";
    public static final String MODE_SOFT = "Soft";
    public static final String MODE_HOT = "Hot";
    public static final String MODE_EXTREME = "Extreme";
    public static final String MODE_MADNESS = "Madness";

    private static final String[] modes = new String[] {
            "Fun", "Soft", "Hot", "Extreme", "Madness"
    };

    public static final String TRUTH = "truth";
    public static final String DARE = "dare";
    public static final String NEVEREVER = "neverEver";

    private static final String[] games = new String[] {
            TRUTH, DARE, NEVEREVER
    };

    public static String[] getModes() {
        return modes;
    }

    public static String[] getGames() {
        return games;
    }
}
