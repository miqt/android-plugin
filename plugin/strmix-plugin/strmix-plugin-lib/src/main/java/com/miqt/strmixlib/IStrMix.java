package com.miqt.strmixlib;

import java.util.Map;

public interface IStrMix {
    boolean overflow(String data, Map<String, String> referenceTable, String key);
    String encode(String data,String key);
    String decode(String data,String key);
}
