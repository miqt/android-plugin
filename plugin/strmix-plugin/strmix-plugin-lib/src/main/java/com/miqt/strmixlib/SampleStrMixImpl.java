package com.miqt.strmixlib;

import com.miqt.strmixlib.annotation.IgnoreStrMix;
import com.miqt.strmixlib.annotation.IgnoreStrRep;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@IgnoreStrMix
@IgnoreStrRep
public class SampleStrMixImpl implements IStrMix {

    @Override
    public boolean overflow(String data, Map<String, String> referenceTable, String key) {
        return data != null && data.length() * 4 / 3 >= 65535;
    }

    @Override
    public String encode(String data, String key) {
        if(data == null){
            return data;
        }
        byte[] result = Base64.encode(data.getBytes(),Base64.NO_WRAP);
        try {
            return new String(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return new String(result);
        }
    }

    @Override
    public String decode(String data, String key) {
        if(data == null){
            return data;
        }
        byte[] result = Base64.decode(data.getBytes(),Base64.NO_WRAP);
        try {
            return new String(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return new String(result);
        }
    }
}
