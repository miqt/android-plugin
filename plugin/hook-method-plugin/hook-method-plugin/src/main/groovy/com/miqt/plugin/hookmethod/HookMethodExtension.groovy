package com.miqt.plugin.hookmethod

import com.miqt.asm.method_hook.Extension

public class HookMethodExtension extends Extension {
    public List<HookTarget> hookTargets = new ArrayList<>();
    //方法hook调用实现类
    public String handler = "com.miqt.hookplugin.HookHandler";

    public HookMethodExtension() {
        hookTargets.add(new HookTarget("annotationHookMethod").setAnnotation("Lcom/miqt/pluginlib/annotation/HookMethod;"));
        hookTargets.add(new HookTarget("annotationHookMethodInherited").setAnnotation("Lcom/miqt/pluginlib/annotation/HookMethodInherited;"));
    }



    @Override
    public String getExtensionName() {
        return "hook_method";
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HookMethodExtension{");
        sb.append(", hookTargets=").append(hookTargets);
        sb.append(", impl='").append(handler).append('\'');
        sb.append(", enable=").append(enable);
        sb.append(", runVariant='").append(runVariant).append('\'');
        sb.append(", injectJar=").append(injectJar);
        sb.append(", buildLog=").append(buildLog);
        sb.append('}');
        return sb.toString();
    }
}
