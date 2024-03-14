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
}
