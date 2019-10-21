package Redmine.redmineapi.internal;

import Redmine.redmineapi.Include;

public class Joiner {
    // TODO add unit tests
    public static String join(String delimToUse, Include... include) {
        String delim = "";
        StringBuilder sb = new StringBuilder();
        for (Include i : include) {
            sb.append(delim).append(i);
            delim = delimToUse;
        }
        return sb.toString();
    }

}
