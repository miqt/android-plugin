package com.miqt.plugin.hookmethod;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.lang.model.element.Modifier;

public class GenHandlerClass {

    private static final String classDoc = "This class generate by Hook Method Plugin." +
            "\nIts function is to receive the forwarding of the intercepted method. " +
            "\nYou can add processing logic to the generated method." +
            "Have fun!" +
            "\nEach time you add a hook point, manually merge the new method in the latest ‘.tmp’ file after rebuild"+
            "\nproject page:https://github.com/miqt/android-plugin" +
            "\n@author miqingtang@163.com";

    static void genHandlerClass(HookMethodExtension extension, @NotNull Project project) {
        String filePath = extension.handler.replace(".", "/") + ".java";
        String dir = project.getProjectDir() + "/src/main/java/";
        try {
            File file = new File(dir, filePath);
            boolean hasOldFile = false;
            if (file.exists()) {
                //生成过了
                hasOldFile = true;
                file = new File(file.getParent(), file.getName() + ".tmp");
            }
            String className = file.getName().replace(".java", "");
            className = className.replace(".tmp", "");
            String packageName = extension.handler.replace("." + className, "");

            TypeSpec.Builder handlerClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addJavadoc(classDoc);
            for (HookTarget hookTarget : extension.hookTargets) {
                genEnter(handlerClass, hookTarget);
                genReturn(handlerClass, hookTarget);
            }
            JavaFile handlerFile = JavaFile.builder(packageName, handlerClass.build())
                    .build();
            FileUtils.write(file, handlerFile.toString(), false);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private static void genReturn(TypeSpec.Builder handlerClass, HookTarget hookTarget) {
        if (!hookTarget.hookTiming.contains(HookTarget.Return)) {
            return;
        }
        MethodSpec.Builder method = genMethodDOc(hookTarget, hookTarget.getReturnMethodName());
        method.addParameter(Object.class, "returnObj");
        method.addParameter(Object.class, "thisObj");
        method.addParameter(String.class, "className");
        method.addParameter(String.class, "methodName");
        method.addParameter(String.class, "argsType");
        method.addParameter(String.class, "returnType");
        method.addParameter(Object[].class, "args");
        handlerClass.addMethod(method.build());
    }

    private static void genEnter(TypeSpec.Builder handlerClass, HookTarget hookTarget) {
        if (!hookTarget.hookTiming.contains(HookTarget.Enter)) {
            return;
        }
        MethodSpec.Builder method = genMethodDOc(hookTarget, hookTarget.getEnterMethodName());
        method.addParameter(Object.class, "thisObj");
        method.addParameter(String.class, "className");
        method.addParameter(String.class, "methodName");
        method.addParameter(String.class, "argsType");
        method.addParameter(String.class, "returnType");
        method.addParameter(Object[].class, "args");
        handlerClass.addMethod(method.build());
    }

    @NotNull
    private static MethodSpec.Builder genMethodDOc(HookTarget hookTarget, String methodName) {
        MethodSpec.Builder main = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement(CodeBlock.of("//TODO "+methodName+" empty implementation!"))
                .returns(void.class)
                .addJavadoc("This method generate with:")
                .addJavadoc("\n\t")
                .addJavadoc("$L", hookTarget.toString());
        if (hookTarget.getDescriptor() != null) {
            Type[] types = Type.getArgumentTypes(hookTarget.getDescriptor());
            main.addJavadoc("\nArgument:");
            for (int i = 0; i < types.length; i++) {
                main.addJavadoc("\n\t" + types[i].toString() + "\n");
            }
            main.addJavadoc("\nReturn Type:");
            main.addJavadoc("\n\t" + Type.getReturnType(hookTarget.getDescriptor()).toString());
        }
        main.addJavadoc("\nGenerate Time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));


        return main;
    }
}
