package com.miqt.strmixlib;

import com.miqt.strmixlib.annotation.IgnoreStrMix;
import com.miqt.strmixlib.annotation.IgnoreStrRep;

import java.util.HashMap;
import java.util.Map;

@IgnoreStrMix
@IgnoreStrRep
public class SampleStrRepImpl implements IStrRep {

    @Override
    public boolean overflow(String data, Map<String, String> referenceTable, String key) {
        if(referenceTable == null){
            return true;
        }
        if(!referenceTable.containsKey(data)){
            return true;
        }
        return data != null && data.length() * 4 / 3 >= 65535;
    }

    @Override
    public boolean checkReferenceTable(Map<String, String> referenceTable, String key) {
        if (referenceTable == null || referenceTable.size() == 0) {
            return true;
        }
        HashMap<String, String> copyofMap = new HashMap<>();
        for (Map.Entry<String, String> entry : referenceTable.entrySet()) {
            copyofMap.put(entry.getValue(), entry.getKey());
        }

        return referenceTable.size() == copyofMap.size();
    }

    @Override
    public String rep(String data, Map<String, String> referenceTable, String key) {
        if (referenceTable == null) {
            return data;
        }
        if (referenceTable.containsKey(data)) {
            return referenceTable.get(data);
        }
        return data;
    }

    @Override
    public String back(String data, Map<String, String> referenceTable, String key) {
        if (referenceTable == null) {
            return data;
        }
        if (referenceTable.containsKey(data)) {
            return referenceTable.get(data);
        }
        return data;
    }
}
