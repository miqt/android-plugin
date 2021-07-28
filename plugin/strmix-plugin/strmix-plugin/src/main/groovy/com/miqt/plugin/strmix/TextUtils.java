package com.miqt.plugin.strmix;

public final class TextUtils {

    private TextUtils() {
    }


    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }


    public static boolean isEmptyAfterTrim(String str) {
        return str == null || str.length() == 0 || str.trim().length() == 0;
    }
}
