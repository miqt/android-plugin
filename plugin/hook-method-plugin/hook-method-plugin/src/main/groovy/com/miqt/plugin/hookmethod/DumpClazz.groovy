package com.miqt.plugin.hookmethod


import org.gradle.api.Project
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

import java.util.regex.Pattern

public class DumpClazz {

    static byte[] dump(String impl) throws Exception {
        impl = impl.replace(".", "/")
        ClassWriter cw = new ClassWriter(0)
        FieldVisitor fv
        MethodVisitor mv

        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "com/miqt/pluginlib/tools/MethodHookHandler", null, "java/lang/Object", null)


        fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "M_PRINT", "Lcom/miqt/pluginlib/tools/IMethodHookHandler;", null, null)
        fv.visitEnd()

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_VARARGS, "enter", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V", null, null)
        mv.visitCode()
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/miqt/pluginlib/tools/MethodHookHandler", "M_PRINT", "Lcom/miqt/pluginlib/tools/IMethodHookHandler;")
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitVarInsn(Opcodes.ALOAD, 2)
        mv.visitVarInsn(Opcodes.ALOAD, 3)
        mv.visitVarInsn(Opcodes.ALOAD, 4)
        mv.visitVarInsn(Opcodes.ALOAD, 5)
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/miqt/pluginlib/tools/IMethodHookHandler", "onMethodEnter", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V", true)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(7, 6)
        mv.visitEnd()

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_VARARGS, "exit", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V", null, null)
        mv.visitCode()
        mv.visitFieldInsn(Opcodes.GETSTATIC, "com/miqt/pluginlib/tools/MethodHookHandler", "M_PRINT", "Lcom/miqt/pluginlib/tools/IMethodHookHandler;")
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitVarInsn(Opcodes.ALOAD, 2)
        mv.visitVarInsn(Opcodes.ALOAD, 3)
        mv.visitVarInsn(Opcodes.ALOAD, 4)
        mv.visitVarInsn(Opcodes.ALOAD, 5)
        mv.visitVarInsn(Opcodes.ALOAD, 6)
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/miqt/pluginlib/tools/IMethodHookHandler", "onMethodReturn", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V", true)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(8, 7)
        mv.visitEnd()

        mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
        mv.visitCode()
        mv.visitTypeInsn(Opcodes.NEW, impl)
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, impl, "<init>", "()V", false)
        mv.visitFieldInsn(Opcodes.PUTSTATIC, "com/miqt/pluginlib/tools/MethodHookHandler", "M_PRINT", "Lcom/miqt/pluginlib/tools/IMethodHookHandler;")
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(2, 0)
        mv.visitEnd()

        cw.visitEnd()

        return cw.toByteArray()
    }
}
