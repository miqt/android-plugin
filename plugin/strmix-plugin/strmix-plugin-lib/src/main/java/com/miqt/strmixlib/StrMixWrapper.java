package com.miqt.strmixlib;

import com.miqt.strmixlib.annotation.IgnoreStrMix;
import com.miqt.strmixlib.annotation.IgnoreStrRep;

import java.util.Map;

@IgnoreStrMix
@IgnoreStrRep
public class StrMixWrapper {
    private static IStrMix impl = new SampleStrMixImpl();

    public static boolean overflow(String data, Map<String, String> referenceTable, String key) {
        return impl.overflow(data, referenceTable, key);
    }

    public static String encode(String data, String key) {
        String result = impl.encode(data, key);
        System.out.println("[mix][key = "+key+"]["+data+"]-->["+result+"]");
        return result;
    }

    public static String decode(String data, String key) {
        return impl.decode(data, key);
    }

    public static String decode(String data) {
        return impl.decode(data, StrMixConstans_v1.key);
    }
}
