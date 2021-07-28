package com.miqt.strmixlib;

import java.util.Map;

public interface IStrRep {

    boolean overflow(String data, Map<String, String> referenceTable, String key);

    boolean checkReferenceTable(Map<String, String> referenceTable, String key);

    String rep(String data, Map<String, String> referenceTable, String key);

    String back(String data, Map<String, String> referenceTable, String key);
}
