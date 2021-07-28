package com.miqt.plugin.strmix

import com.miqt.asm.method_hook.BasePlugin
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry

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
            getLogger().log("repStrMix classBytes size:"+classBytes.length)
        }
        if (getExtension().isEnableMix()) {
            classBytes = allStrMix(getExtension(), classBytes)
        }
        return classBytes
    }

    @Override
    byte[] transformJar(byte[] classBytes, File jarFile, JarEntry entry) {
        if (jarFile.name.contains("strmix-plugin-lib")) {
            getLogger().log("find jar strmix-plugin-lib")
            if (entry.name == "com/miqt/strmixlib/StrMixConstans_v1.class") {
                getLogger().log("gen StrMixConstans_v1")
                return ClassGenerate.generateMix(getExtension())
            } else if (entry.name == "com/miqt/strmixlib/StrRepConstans_v1.class") {
                getLogger().log("gen StrRepConstans_v1")
                return ClassGenerate.generateRep(getExtension())
            }
        }
        return classBytes
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
