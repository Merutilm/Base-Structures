package kr.merutilm.base.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class StringUtils {
    private StringUtils() {

    }

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            sb.append(Character.toString(AdvancedMath.doubleRandom(1) > 0.5 ? (int) AdvancedMath.random(65, 91) : (int) AdvancedMath.random(97, 122)));
        }
        return sb.toString();
    }

    public static String commaForThousands(int value) {
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    public static int getStringPixelLength(String s, int fontSize) {

        return (int) (0.6 * s.length() * fontSize);
    }

}
