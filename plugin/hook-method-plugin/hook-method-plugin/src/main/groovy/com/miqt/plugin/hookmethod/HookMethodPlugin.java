package com.miqt.plugin.hookmethod;

import com.android.build.api.transform.TransformInvocation;
import com.miqt.asm.method_hook.BasePlugin;

import org.apache.http.util.TextUtils;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.util.jar.JarEntry;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;

public class HookMethodPlugin extends BasePlugin<HookMethodExtension> {

    NamedDomainObjectContainer<HookTarget> hookTargets;
    @Override
    public HookMethodExtension initExtension() {
        return new HookMethodExtension();
    }

    @Override
    public void apply(@NotNull Project project) {
        super.apply(project);
        ExtensionAware aware = (ExtensionAware) getExtension();
        // 创建一个容器
        hookTargets = project.container(HookTarget.class);
        // 将容器添加为 extension
        aware.getExtensions().add("hookTargets", hookTargets);
        project.afterEvaluate(project1 -> {
            if (getExtension().handler == null || "".equals(getExtension().handler)) {
                return;
            }
            getExtension().hookTargets.addAll(hookTargets);
            GenHandlerClass.genHandlerClass(getExtension(),project);
        });
    }



    @Override
    public byte[] transform(byte[] classBytes, File classFile) {
        String name = classFile.getName();
        if (!TextUtils.isEmpty(getExtension().handler) &&
                classFile.getAbsolutePath().contains(getExtension().handler.replace(".", File.separator))) {
            return classBytes;
        }
        if (name.endsWith(".class") && !name.startsWith("R$") &&
                !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
            getLogger().log("[class]" + classFile.getName());
            return processClass(classBytes);
        }
        return classBytes;
    }


    @Override
    public byte[] transformJar(byte[] classBytes, File jarFile, JarEntry entry) {
        //如果是impl类，直接跳过
        if (!TextUtils.isEmpty(getExtension().handler) &&
                entry.getName().contains(getExtension().handler.replace(".", "/"))) {
            return classBytes;
        }
        //跳过自己的类库
        if(entry.getName().contains("com/miqt/pluginlib/")){
            return classBytes;
        }
        //注解+正则判断是否插桩
        return processClass(classBytes);
    }

    private byte[] processClass(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        MethodHookVisitor cv = new MethodHookVisitor(cw, this);
        cr.accept(cv, EXPAND_FRAMES);
        return cw.toByteArray();
    }

    @Override
    public String getName() {
        return "hook-method-plugin";
    }
}
