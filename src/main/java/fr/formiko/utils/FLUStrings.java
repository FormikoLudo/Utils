package fr.formiko.utils;

public class FLUStrings {
    private FLUStrings() {} // hide constructor
    public static String addAtTheEndIfNeeded(String string, String toAdd) {
        if (string == null) {
            string = "";
        }
        if (toAdd == null) {
            toAdd = "";
        }
        return string.endsWith(toAdd) ? string : string + toAdd;
    }

    public static String removeAtTheEndIfNeeded(String string, String toRemove) {
        if (string == null) {
            string = "";
        }
        if (toRemove == null) {
            toRemove = "";
        }
        return string.endsWith(toRemove) ? string.substring(0, string.length() - toRemove.length()) : string;
    }

    public static String addAtTheBeginningIfNeeded(String string, String toAdd) {
        if (string == null) {
            string = "";
        }
        if (toAdd == null) {
            toAdd = "";
        }
        return string.startsWith(toAdd) ? string : toAdd + string;
    }

    public static String removeAtTheBeginningIfNeeded(String string, String toRemove) {
        if (string == null) {
            string = "";
        }
        if (toRemove == null) {
            toRemove = "";
        }
        return string.startsWith(toRemove) ? string.substring(toRemove.length()) : string;
    }
}
