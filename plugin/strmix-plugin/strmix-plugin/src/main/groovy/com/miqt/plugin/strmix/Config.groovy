package com.miqt.plugin.strmix

import com.miqt.asm.method_hook.Extension;
import org.json.simple.JSONObject

class Config extends Extension{

    boolean enableMix
    boolean enableRep
    String key
    String mixImpl
    String repImpl
    Map<String, String> repTable = [:]

    Config() {
        injectJar = true
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("enableMix", enableMix);
            jsonObject.put("enableRep", enableRep);
            jsonObject.put("key", key);
            jsonObject.put("mixImpl", mixImpl);
            jsonObject.put("repImpl", repImpl);
            jsonObject.put("repTable", repTable.size());
        } catch (Throwable e) {
            //JSONException
        }
        return jsonObject;
    }

    @Override
    String getExtensionName() {
        return "str_mix"
    }
}