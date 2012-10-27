package com.eddiedunn.util;

import java.util.Comparator;

public class TreeMapStringLengthIntegerValueComparator implements Comparator<String> {

	int base;
    public TreeMapStringLengthIntegerValueComparator(String base) {
        this.base = base.length();
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        int dist1 = Math.abs(a.length() - base);
        int dist2 = Math.abs(b.length() - base);

        return dist1 - dist2;
    }
}

