package com.miqt.plugin.hookmethod;

import com.miqt.asm.method_hook.Extension;


import java.util.ArrayList;
import java.util.List;

public class HookMethodExtension extends Extension {
    //类名称白名单
    public List<String> classWhiteListRegex = new ArrayList<>();
    //方法hook调用实现类
    public String impl = "";


    @Override
    public String toString() {
        return "HookMethodExtension{" +
                "classWhiteListRegex=" + classWhiteListRegex +
                ", impl='" + impl + '\'' +
                ", enable=" + enable +
                ", runVariant=" + runVariant +
                ", injectJar=" + injectJar +
                ", buildLog=" + buildLog +
                '}';
    }

    @Override
    public String getExtensionName() {
        return "hook_method";
    }
}
