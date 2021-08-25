package com.miqt.plugin.jiagu;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.gradle.internal.InternalScope;
import com.android.build.gradle.internal.pipeline.ExtendedContentType;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.miqt.asm.method_hook.BasePlugin;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.jar.JarEntry;

public class JiaGuPlugin extends BasePlugin<JiaGuExtension> {

    @Override
    public String getName() {
        return "jiagu_plugin";
    }

    @Override
    public JiaGuExtension initExtension() {
        return new JiaGuExtension();
    }
    @Override
    public byte[] transform(byte[] classBytes, File classFile) {
        getLogger().log(classFile.getAbsolutePath());
        return classBytes;
    }

    @Override
    public byte[] transformJar(byte[] classBytes, File jarFile, JarEntry entry) {
        getLogger().log(jarFile.getAbsolutePath());
        return classBytes;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }


}
