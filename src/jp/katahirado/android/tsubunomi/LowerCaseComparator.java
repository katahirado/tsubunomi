package jp.katahirado.android.tsubunomi;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class LowerCaseComparator implements Comparator<String> {
    @Override
    public int compare(String s, String s1) {
        return s.compareToIgnoreCase(s1);
    }
}

