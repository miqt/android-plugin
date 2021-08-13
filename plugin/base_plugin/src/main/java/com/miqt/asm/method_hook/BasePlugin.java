package com.miqt.asm.method_hook;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.ide.common.internal.WaitableExecutor;
import com.google.common.collect.Sets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import static com.android.build.api.transform.Status.ADDED;


public abstract class BasePlugin<E extends Extension> extends Transform implements Plugin<Project> {

    private Project project;
    private boolean isApp = true;

    private Logger logger;
    private E extension;
    private WaitableExecutor waitableExecutor;
    //某些情况下不需要执行插桩，直接复制文件即可
    boolean isNotRun = false;

    @Override
    public void apply(@NotNull Project project) {
        this.project = project;
        BaseExtension android = (BaseExtension) project.getExtensions().findByName("android");
        if (android == null) {
            return;
        }
        if (android instanceof AppExtension) {
            isApp = true;
        } else if (android instanceof LibraryExtension) {
            isApp = false;
        }
        E e = initExtension();
        project.getExtensions().create(e.getExtensionName(), e.getClass());
        extension = (E) project.getExtensions().getByType(e.getClass());
        android.registerTransform(this);
        if (waitableExecutor == null) {
            waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();
        }
    }

    public abstract E initExtension();

    public E getExtension() {
        return extension;
    }

    public Project getProject() {
        return project;
    }

    public boolean isApp() {
        return isApp;
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = new Logger(project.getBuildDir().getAbsolutePath() + "/plugin/", getName() + ".log");
        }
        return logger;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        if (!isApp) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT);
        } else {
            return TransformManager.SCOPE_FULL_PROJECT;
        }
    }

    @Override
    public boolean isIncremental() {
        return true;
    }


    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        if (extension.buildLog) {
            getLogger().init();
        }
        try {
            System.out.println("┌---------------------------------------------");
            System.out.println("|The plugin [" + getName() + "] --> Start!");
            System.out.println("|项目主页:https://github.com/miqt/android-plugin");
            System.out.println("|联系作者:miqingtang@163.com");
            System.out.println("└---------------------------------------------");
            beginTransform(transformInvocation);
            doTransform(transformInvocation);
            afterTransform(transformInvocation);
        } catch (Throwable e) {
            e.printStackTrace();
            getLogger().log(e);
            System.out.println("┌---------------------------------------------");
            System.out.println("|The plugin [" + getName() + "] --> Error!");
            if (getExtension().buildLog) {
                System.out.println("|log:" + getLogger().getLogFilePath());
            }
            System.out.println("└---------------------------------------------");
        }

        waitableExecutor.waitForAllTasks();

        System.out.println("┌---------------------------------------------");
        System.out.println("|The plugin [" + getName() + "] --> Done!");
        if (getExtension().buildLog) {
            System.out.println("|log:" + getLogger().getLogFilePath());
        }
        System.out.println("└---------------------------------------------");
        getLogger().release();
    }

    protected void afterTransform(TransformInvocation transformInvocation) {
    }


    protected void beginTransform(TransformInvocation transformInvocation) {
    }


    private void doTransform(TransformInvocation transformInvocation) {
        isNotRun = false;
        if (!getExtension().enable) {
            getLogger().log(getName() + " not enable!");
            isNotRun = true;
        }
        String vn = transformInvocation.getContext().getVariantName();
        if (!isNotRun && !getExtension().runVariant.toLowerCase().equals(RunVariant.ALWAYS.name().toLowerCase())) {
            //目标环境与当前环境不相等，或，当前环境是从不运行。
            isNotRun = !getExtension().runVariant.toLowerCase().equals(transformInvocation.getContext().getVariantName().toLowerCase())
                    || getExtension().runVariant.toLowerCase().equals(RunVariant.NEVER.name().toLowerCase());
            if (isNotRun) {
                logger.log("Current build type is " + vn + ". Not match runVariant = " + getExtension().runVariant);
            }
        }
        try {
            super.transform(transformInvocation);
            boolean isIncremental = transformInvocation.isIncremental();
            logger.log("----------------------------------------------------------------");
            logger.log("ProjectName: " + project.getName());
            logger.log("ProjectPath: " + transformInvocation.getContext().getPath());
            logger.log("BuildType  : " + vn);
            logger.log("Incremental: " + isIncremental);
            logger.log("extension  : " + extension.toString());
            logger.log("Time       : " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            logger.log("----------------------------------------------------------------");
            logger.log("多线程编译已经打开，目前并发处理数：" + waitableExecutor.getParallelism());
            //如果非增量，则清空旧的输出内容
            if (!isIncremental) {
                transformInvocation.getOutputProvider().deleteAll();
            }
            Collection<TransformInput> inputs = transformInvocation.getInputs();
            inputs.forEach(transformInput -> {
                transformInput.getDirectoryInputs().forEach(directoryInput -> {
                    eachDir(transformInvocation, isIncremental, directoryInput);
                });
                transformInput.getJarInputs().forEach(jarInput -> {
                    eachJar(transformInvocation, isIncremental, jarInput);
                });
            });
        } catch (Throwable e) {
            e.printStackTrace();
            logger.log(e);
        }
    }

    private void eachJar(TransformInvocation transformInvocation, boolean isIncremental, JarInput jarInput) {
        try {
            String jarName = jarInput.getName();
            File file = jarInput.getFile();
//            File temDir = transformInvocation.getContext().getTemporaryDir();
            File dest = transformInvocation.getOutputProvider().getContentLocation(
                    jarInput.getFile().getAbsolutePath(),
                    jarInput.getContentTypes(),
                    jarInput.getScopes(),
                    Format.JAR);
            Status status;
            if (isIncremental) {
                status = jarInput.getStatus();
            } else {
                status = Status.ADDED;
            }
            logger.log("[JarInput]" + file.getAbsolutePath() + " status:" + status);
            //根据是否变化决定是否更新
            switch (status) {
                case NOTCHANGED:
                    break;
                case REMOVED:
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest);
                    }
                    break;
                case ADDED:
                case CHANGED:
                    try {
                        FileUtils.touch(dest);
                    } catch (Throwable e) {
                        File pr = dest.getParentFile();
                        if (!pr.exists()) {
                            pr.mkdirs();
                        }
                    }
                    weaveSingleJarToFile(file, dest);
                    break;
            }
        } catch (IOException e) {
            logger.log(e);
        }
    }

    private void weaveSingleJarToFile(File file, File dest) throws IOException {
        waitableExecutor.execute((Callable<Object>) () -> {
            if (dest.exists()) {
                FileUtils.forceDelete(dest);
            }
            //不遍历jar，则直接退出
            if (!getExtension().injectJar||isNotRun) {
                FileUtils.copyFile(file, dest);
                return null;
            }
            JarFile jarFile = new JarFile(file);
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(dest));
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                String name = entry.getName();

                if (isRemoveJarEntry(jarFile,entry)){
                    continue;
                }

                JarEntry outJarEntry = new JarEntry(name);

                jarOutputStream.putNextEntry(outJarEntry);
                byte[] modifiedClassBytes = null;
                byte[] sourceClassBytes = IOUtils.toByteArray(jarFile.getInputStream(entry));
                if (name.endsWith(".class")) {
                    try {
                        modifiedClassBytes = transformJar(sourceClassBytes, file, entry);
                    } catch (Throwable e) {
                        getLogger().log(e);
                        e.printStackTrace();
                        modifiedClassBytes = sourceClassBytes;
                    }
                }

                if (modifiedClassBytes == null) {
                    modifiedClassBytes = sourceClassBytes;
                }
                jarOutputStream.write(modifiedClassBytes);
                jarOutputStream.flush();
                jarOutputStream.closeEntry();

            }
            jarOutputStream.close();
            jarFile.close();
            return null;
        });

    }

    public boolean isRemoveJarEntry(JarFile jarFile, JarEntry entry) {
        return false;
    }

    private void eachDir(TransformInvocation transformInvocation, boolean isIncremental, DirectoryInput directoryInput) {
        try {
            File dest = transformInvocation.getOutputProvider().getContentLocation(
                    directoryInput.getName(),
                    directoryInput.getContentTypes(),
                    directoryInput.getScopes(),
                    Format.DIRECTORY);
            FileUtils.forceMkdir(dest);
            BiConsumer<File, Status> biConsumer = (file, status) -> {
                try {
                    String srcDirPath = directoryInput.getFile().getAbsolutePath();
                    String destDirPath = dest.getAbsolutePath();
                    String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath);
                    File destFile = new File(destFilePath);
                    logger.log("[DirectoryInput]" + file.getAbsolutePath() + " status:" + status.name());
                    switch (status) {
                        case NOTCHANGED:
                            break;
                        case REMOVED:
                            if (destFile.exists()) {
                                FileUtils.forceDelete(destFile);
                            }
                            break;
                        case ADDED:
                        case CHANGED:
                            try {
                                FileUtils.touch(destFile);
                            } catch (Throwable e) {
                                File pr = destFile.getParentFile();
                                if (!pr.exists()) {
                                    pr.mkdirs();
                                }
                            }
                            weaveSingleClassToFile(file, destFile);
                            break;

                    }
                } catch (IOException e) {
                    logger.log(e);
                }
            };
            waitableExecutor.execute(() -> {
                appendClass(dest);
                return null;
            });

            //当前是否是增量编译
            if (isIncremental) {
                directoryInput.getChangedFiles().forEach(biConsumer);
            } else {
                com.android.utils.FileUtils.getAllFiles(directoryInput.getFile()).forEach(file -> {
                    biConsumer.accept(file, ADDED);
                });
            }
        } catch (IOException e) {
            logger.log(e);
        }
    }

    public void appendClass(File dest) {

    }

    public abstract byte[] transform(byte[] classBytes, File classFile);

    public abstract byte[] transformJar(byte[] classBytes, File jarFile, JarEntry entry);

    private static final String FILE_SEP = File.separator;

    public final void weaveSingleClassToFile(File inputFile, File outputFile) throws IOException {
        waitableExecutor.execute(() -> {
            if (!isNotRun && inputFile.getName().endsWith(".class")) {
                FileUtils.touch(outputFile);
                byte[] classByte = FileUtils.readFileToByteArray(inputFile);
                classByte = transform(classByte, inputFile);
                FileUtils.writeByteArrayToFile(outputFile, classByte);
            } else {
                if (inputFile.isFile()) {
                    FileUtils.touch(outputFile);
                    FileUtils.copyFile(inputFile, outputFile);
                }
            }
            return null;
        });

    }
}
