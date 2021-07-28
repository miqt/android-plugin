package com.miqt.plugin.strmix

import com.miqt.strmixlib.StrRepWrapper
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

public class StrMixTransform extends Transform {
    Project project
    boolean islib;

    StrMixTransform(Project project, boolean islib) {
        this.project = project
        this.islib = islib
    }

    @Override
    String getName() {
        return "AllStrMix"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        if (islib) {
            Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
            );
        } else {
            TransformManager.SCOPE_FULL_PROJECT
        }
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println '┌------------------------┐'
        println '|      StrMix start      |'
        println '└------------------------┘'

        Config config = project.StrMix
        println(config.toString())

        if (!StrRepWrapper.checkReferenceTable(config.getRepTable(), config.getKey())) {
            error("rep table Contains duplicate kv pairs.")
        }

        boolean generated = false



        inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def jarFile = jarInput.file
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                println("[jarInputs.each] " + jarName)
                if (jarName.contains("StrMixlib")) {
                    println("[ClassGenerate] StrMixlib inject...")
                    jarFile = injectJar(jarFile, context.getTemporaryDir(), config)
                    println("[ClassGenerate] StrMixlib inject end")
                    generated = true
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                FileUtils.copyFile(jarFile, dest)
            }
            input.directoryInputs.each { DirectoryInput directoryInput ->
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        def name = file.name
                        println("[classfile.each] " + name)
                        if (name.endsWith(".class") && !name.startsWith("R\$") &&
                                !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {

                            byte[] code = file.bytes;
                            if (config.isEnableRep()) {
                                code = repStrMix(config, code)
                            }
                            if (config.isEnableMix()) {
                                code = allStrMix(config, code)
                            }

                            FileOutputStream fos = new FileOutputStream(
                                    file.parentFile.absolutePath + File.separator + name)
                            fos.write(code)
                            fos.close()
                        }
                    }
                }

                if (!generated) {
                    File gDir = new File(directoryInput.file.path, "com/miqt/strmixlib/")
                    if (!gDir.exists()) {
                        gDir.mkdirs()
                    }
                    File repClassFile = new File(directoryInput.file.path, "com/miqt/strmixlib/StrRepConstans_v1.class")
                    repClassFile.bytes = ClassGenerate.generateRep(config)
                    File mixClassFile = new File(directoryInput.file.path, "com/miqt/strmixlib/StrMixConstans_v1.class")
                    mixClassFile.bytes = ClassGenerate.generateMix(config)
                    generated = true
                }

                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                println("[classfile.each] " + name + "->" + dest)
                println("[classfile.each] " + name + "->" + directoryInput.file.path)

                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }


        println '┌------------------------┐'
        println "|      StrMix   √        |"
        println '└------------------------┘'
    }

    private File injectJar(File jarFile, File tempDir, Config config) {
        def file = new JarFile(jarFile)
        //设置输出到的jar
        def hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        def outputJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)

            if (entryName.equals("com/miqt/strmixlib/StrMixConstans_v1.class")) {
                modifiedClassBytes = ClassGenerate.generateMix(config)
                println("[ClassGenerate] StrMixConstans_v1")
            } else if (entryName.equals("com/miqt/strmixlib/StrRepConstans_v1.class")) {
                modifiedClassBytes = ClassGenerate.generateRep(config)
                println("[ClassGenerate] StrRepConstans_v1")
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes)
            } else {
                jarOutputStream.write(modifiedClassBytes)
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
        return outputJar
    }

    private void error(String msg) {
        println(msg)
        println '┌------------------------┐'
        println "|      StrMix   ×        |"
        println '└------------------------┘'
    }

    private byte[] allStrMix(Config config, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        //由 lysys2020ana base64而来
        ClassVisitor cv = new StrMixClassVisitor(config, project, cw)

        cr.accept(cv, EXPAND_FRAMES)

        byte[] code = cw.toByteArray()
        code
    }

    private byte[] repStrMix(Config config, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        //由 lysys2020ana base64而来
        ClassVisitor cv = new StrRepClassVisitor(config, project, cw)

        cr.accept(cv, EXPAND_FRAMES)

        byte[] code = cw.toByteArray()
        code
    }
}