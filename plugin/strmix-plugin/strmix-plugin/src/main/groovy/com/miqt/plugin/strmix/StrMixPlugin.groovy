package com.miqt.plugin.strmix

import com.miqt.asm.method_hook.BasePlugin
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.concurrent.Callable
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

class StrMixPlugin extends BasePlugin<Config> {


    @Override
    Config initExtension() {
        return new Config()
    }

    @Override
    byte[] transform(byte[] classBytes, File classFile) {
        if (getExtension().isEnableRep()) {
            classBytes = repStrMix(getExtension(), classBytes)
        }
        if (getExtension().isEnableMix()) {
            classBytes = allStrMix(getExtension(), classBytes)
        }
        return classBytes
    }

    @Override
    byte[] transformJar(byte[] classBytes, File jarFile, JarEntry entry) {
        return classBytes
    }

    @Override
    boolean isRemoveJarEntry(JarFile jarFile, JarEntry entry) {
        if (jarFile.name.contains("strmix-plugin-lib")) {
            if (entry.name.contains("StrMixConstans_v1") || entry.name.contains("StrRepConstans_v1")) {
                getLogger().log("skip:" + entry.name)
                return true;
            }
        }
        return super.isRemoveJarEntry(jarFile, entry)
    }
    boolean generated = false;

    @Override
    void appendClass(File dest) {
        super.appendClass(dest)
        if (generated) return
        generated = true
        File mix = new File(dest.getAbsolutePath() + "/com/miqt/strmixlib/StrMixConstans_v1.class");
        File rep = new File(dest.getAbsolutePath() + "/com/miqt/strmixlib/StrRepConstans_v1.class");
        FileUtils.touch(mix)
        FileUtils.touch(rep)
        mix.bytes = ClassGenerate.generateMix(getExtension())
        rep.bytes = ClassGenerate.generateRep(getExtension())
        getLogger().log("generated " + mix.getPath() + " success!")
        getLogger().log("generated " + rep.getPath() + " success!")
    }

    @Override
    String getName() {
        return "StrMix"
    }


    private byte[] allStrMix(Config config, byte[] bytes) {
        try {
            ClassReader cr = new ClassReader(bytes)
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
            ClassVisitor cv = new StrMixClassVisitor(config, project, cw)

            cr.accept(cv, EXPAND_FRAMES)

            byte[] code = cw.toByteArray()
            return code
        } catch (Throwable e) {
            getLogger().log(e)
        }
        return bytes
    }

    private byte[] repStrMix(Config config, byte[] bytes) {
        try {
            ClassReader cr = new ClassReader(bytes)
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
            ClassVisitor cv = new StrRepClassVisitor(config, project, cw)

            cr.accept(cv, EXPAND_FRAMES)

            byte[] code = cw.toByteArray()
            return code
        } catch (Throwable e) {
            getLogger().log(e)
        }
        return bytes
    }
}
