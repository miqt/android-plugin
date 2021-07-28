package com.miqt.strmixlib;

import com.miqt.strmixlib.annotation.IgnoreStrMix;
import com.miqt.strmixlib.annotation.IgnoreStrRep;
import com.miqt.strmixlib.json.JSONException;
import com.miqt.strmixlib.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@IgnoreStrMix
@IgnoreStrRep
public class StrRepWrapper {

    private static IStrRep impl = new SampleStrRepImpl();

    public static boolean overflow(String data, Map<String, String> referenceTable, String key) {
        return impl.overflow(data, referenceTable, key);
    }

    public static boolean checkReferenceTable(Map<String, String> referenceTable, String key) {
        return impl.checkReferenceTable(referenceTable, key);
    }

    public static String rep(String data, Map<String, String> referenceTable, String key) {
        return impl.rep(data, referenceTable, key);
    }

    public static String back(String data, Map<String, String> referenceTable, String key) {
        return impl.back(data, referenceTable, key);
    }

    public static String back(String data) {
        return impl.back(data, getReferenceTable(), getKey());
    }

    private static String getKey() {
        return StrRepConstans_v1.key;
    }

    private static Map<String, String> map;

    private static Map<String, String> getReferenceTable() {
        if (map != null) {
            return map;
        }
        String decodeJson;
        try {
            decodeJson = new String(Base64.decode(StrRepConstans_v1.RepTableRaw, Base64.NO_WRAP), "utf-8");
        } catch (UnsupportedEncodingException e) {
            decodeJson = new String(Base64.decode(StrRepConstans_v1.RepTableRaw, Base64.NO_WRAP));
        }
        map = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(decodeJson);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                Object next = iterator.next();
                if (next instanceof String) {
                    map.put(jsonObject.optString((String) next), (String) next);
                }
            }
        } catch (Throwable e) {
        }

        return map;
    }
}
