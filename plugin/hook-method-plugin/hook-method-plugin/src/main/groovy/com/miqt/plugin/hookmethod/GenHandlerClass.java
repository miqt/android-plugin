package com.miqt.plugin.hookmethod;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;

import javax.lang.model.element.Modifier;

public class GenHandlerClass {

    static void genHandlerClass(HookMethodExtension extension, @NotNull Project project) {
        String filePath = extension.impl.replace(".", "/") + ".java";
        String dir = project.getProjectDir() + "/src/main/java/";
        try {
            File file = new File(dir, filePath);
            if (file.exists()) {
                //生成过了
                // return;
            }
            String className = file.getName().replace(".java", "");
            String packageName = extension.impl.replace("." + className, "");

            TypeSpec.Builder handlerClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addJavadoc("This class generate by Hook Method Plugin." +
                            "\nIts function is to receive the forwarding of the intercepted method. " +
                            "\nYou can add processing logic to the generated method." +
                            "Have fun!" +
                            "\nproject page:https://github.com/miqt/android-plugin" +
                            "\n@author miqingtang@163.com");
            for (HookTarget hookTarget : extension.hookTargets) {
                genEnter(handlerClass, hookTarget);
                genReturn(handlerClass, hookTarget);
            }
            JavaFile javaFile = JavaFile.builder(packageName, handlerClass.build())
                    .build();
            FileUtils.write(file, javaFile.toString(), false);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private static void genReturn(TypeSpec.Builder handlerClass, HookTarget hookTarget) {
        MethodSpec.Builder method = genMethodDOc(hookTarget, hookTarget.getReturnMethodName());
        method.addParameter(Object.class, "thisObj");
        method.addParameter(String.class, "className");
        method.addParameter(String.class, "methodName");
        method.addParameter(String.class, "argsType");
        method.addParameter(String.class, "returnType");
        method.addParameter(Object[].class, "args");
        handlerClass.addMethod(method.build());
    }

    private static void genEnter(TypeSpec.Builder handlerClass, HookTarget hookTarget) {
        MethodSpec.Builder method = genMethodDOc(hookTarget, hookTarget.getEnterMethodName());
        method.addParameter(Object.class, "returnObj");
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
                .returns(void.class)
                .addJavadoc("This method generate with:")
                .addJavadoc("\n\t")
                .addJavadoc("$L", hookTarget.toString());
        if (hookTarget.getDescriptor() != null) {
            Type[] types = Type.getArgumentTypes(hookTarget.getDescriptor());
            main.addJavadoc("\nargument:");
            for (int i = 0; i < types.length; i++) {
                main.addJavadoc("\n\t" + types[i].toString() + "\n");
            }
            main.addJavadoc("\nreturn type:");
            main.addJavadoc("\n\t" + Type.getReturnType(hookTarget.getDescriptor()).toString());
        }
        return main;
    }
}
