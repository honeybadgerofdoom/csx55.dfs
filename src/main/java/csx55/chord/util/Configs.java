package csx55.chord.util;

import java.text.DecimalFormat;

public class Configs {

    public static final int TABLE_SIZE = 32;

    public static String formatNumber(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }

}
