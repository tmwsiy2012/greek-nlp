package com.eddiedunn.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

public class TreeMapArrayListLengthValueComparator implements Comparator<String> {

	Map<String,ArrayList<String>> base;
    public TreeMapArrayListLengthValueComparator(Map<String,ArrayList<String>> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a).size() >= base.get(b).size()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
