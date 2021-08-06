package com.miqt.plugin.hookmethod;

import com.miqt.asm.method_hook.Extension;


import java.util.ArrayList;
import java.util.List;

public class HookMethodExtension extends Extension {
    //类名称白名单
    public List<String> classWhiteListRegex = new ArrayList<>();
    public List<HookTarget> hookTargets = new ArrayList<>();
    //方法hook调用实现类
    public String impl = "";

    public HookMethodExtension() {
//        this.extensions.create("defaultConfig", DefaultConfig, "defaultConfig")
        hookTargets.add(new HookTarget().setAnnotation("Lcom/miqt/pluginlib/annotation/HookMethod;"));
        hookTargets.add(new HookTarget().setAnnotation("Lcom/miqt/pluginlib/annotation/HookMethodInherited;"));
    }

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


    public class DefaultConfig {
        String applicationId
        int minSdkVersion
        int targetSdkVersion
        public DefaultConfig(String name) {
            println "name = " + name
        }
    }
}
