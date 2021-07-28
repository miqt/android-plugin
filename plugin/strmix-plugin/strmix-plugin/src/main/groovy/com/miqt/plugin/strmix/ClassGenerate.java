package com.miqt.plugin.strmix;

import org.json.simple.JSONObject;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.UnsupportedEncodingException;

public class ClassGenerate {
    public static byte[] generateMix(Config config) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "com/miqt/strmixlib/StrMixConstans_v1", null, "java/lang/Object", null);

        {
            fv = cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "key", "Ljava/lang/String;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            mv.visitLdcInsn(config.getKey());
            mv.visitFieldInsn(Opcodes.PUTSTATIC, "com/miqt/strmixlib/StrMixConstans_v1", "key", "Ljava/lang/String;");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(1, 0);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    public static byte[] generateRep(Config config) {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "com/miqt/strmixlib/StrRepConstans_v1", null, "java/lang/Object", null);

        {
            fv = cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "RepTableRaw", "Ljava/lang/String;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "key", "Ljava/lang/String;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            try {
                mv.visitLdcInsn(new String(Base64.encode(JSONObject.toJSONString(config.getRepTable()).getBytes(), Base64.NO_WRAP),"utf-8"));
            } catch (UnsupportedEncodingException e) {
                mv.visitLdcInsn(new String(Base64.encode(JSONObject.toJSONString(config.getRepTable()).getBytes(), Base64.NO_WRAP)));
            }
            mv.visitFieldInsn(Opcodes.PUTSTATIC, "com/miqt/strmixlib/StrRepConstans_v1", "RepTableRaw", "Ljava/lang/String;");
            mv.visitLdcInsn(config.getKey());
            mv.visitFieldInsn(Opcodes.PUTSTATIC, "com/miqt/strmixlib/StrRepConstans_v1", "key", "Ljava/lang/String;");
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(1, 0);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
