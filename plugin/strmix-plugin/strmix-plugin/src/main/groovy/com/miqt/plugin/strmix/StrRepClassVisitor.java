package com.miqt.plugin.strmix;


import com.miqt.strmixlib.IStrRep;
import com.miqt.strmixlib.SampleStrRepImpl;
import com.miqt.strmixlib.StrRepWrapper;

import org.gradle.api.Project;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;


public class StrRepClassVisitor extends ClassVisitor {

    private static final String IGNORE_ANNOTATION = "Lcom/miqt/strmixlib/annotation/IgnoreStrRep;";
    private String mFogClassName;

    private boolean isClInitExists;

    private List<ClassStringField> mStaticFinalFields = new ArrayList<>();
    private List<ClassStringField> mStaticFields = new ArrayList<>();
    private List<ClassStringField> mFinalFields = new ArrayList<>();
    private List<ClassStringField> mFields = new ArrayList<>();

    private IStrRep mStrRepImpl;
    private String mClassName;
    private final String mKey;
    private final Config config;

    private boolean mIgnoreClass;


    public StrRepClassVisitor(Config config, Project project, ClassWriter cw) {
        super(Opcodes.ASM5, cw);
        this.config = config;
        this.mStrRepImpl = new SampleStrRepImpl();
        this.mKey = config.getKey();
        this.mFogClassName = StrRepWrapper.class.getName().replace('.', '/');
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.mClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        mIgnoreClass = IGNORE_ANNOTATION.equals(desc);
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (ClassStringField.STRING_DESC.equals(desc) && name != null && !mIgnoreClass) {

            if ((access & Opcodes.ACC_STATIC) != 0 && (access & Opcodes.ACC_FINAL) != 0) {
                mStaticFinalFields.add(new ClassStringField(name, (String) value));
                value = null;
            }

            if ((access & Opcodes.ACC_STATIC) != 0 && (access & Opcodes.ACC_FINAL) == 0) {
                mStaticFields.add(new ClassStringField(name, (String) value));
                value = null;
            }

            if ((access & Opcodes.ACC_STATIC) == 0 && (access & Opcodes.ACC_FINAL) != 0) {
                mFinalFields.add(new ClassStringField(name, (String) value));
                value = null;
            }

            if ((access & Opcodes.ACC_STATIC) != 0 && (access & Opcodes.ACC_FINAL) != 0) {
                mFields.add(new ClassStringField(name, (String) value));
                value = null;
            }
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && !mIgnoreClass) {
            if ("<clinit>".equals(name)) {
                isClInitExists = true;

                mv = new MethodVisitor(Opcodes.ASM5, mv) {

                    private String lastStashCst;

                    @Override
                    public void visitCode() {
                        super.visitCode();

                        for (ClassStringField field : mStaticFinalFields) {
                            if (!canEncrypted(field.value)) {
                                continue;
                            }
                            String originValue = field.value;
                            String encryptValue = mStrRepImpl.rep(originValue, config.getRepTable(), mKey);
                            super.visitLdcInsn(encryptValue);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, mFogClassName, "back", "(Ljava/lang/String;)Ljava/lang/String;", false);
                            super.visitFieldInsn(Opcodes.PUTSTATIC, mClassName, field.name, ClassStringField.STRING_DESC);
                        }
                    }

                    @Override
                    public void visitLdcInsn(Object cst) {

                        if (cst != null && cst instanceof String && canEncrypted((String) cst)) {
                            lastStashCst = (String) cst;
                            String originValue = lastStashCst;
                            String encryptValue = mStrRepImpl.rep(originValue, config.getRepTable(), mKey);
                            super.visitLdcInsn(encryptValue);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, mFogClassName, "back", "(Ljava/lang/String;)Ljava/lang/String;", false);
                        } else {
                            lastStashCst = null;
                            super.visitLdcInsn(cst);
                        }
                    }

                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                        if (mClassName.equals(owner) && lastStashCst != null) {
                            boolean isContain = false;
                            for (ClassStringField field : mStaticFields) {
                                if (field.name.equals(name)) {
                                    isContain = true;
                                    break;
                                }
                            }
                            if (!isContain) {
                                for (ClassStringField field : mStaticFinalFields) {
                                    if (field.name.equals(name) && field.value == null) {
                                        field.value = lastStashCst;
                                        break;
                                    }
                                }
                            }
                        }
                        lastStashCst = null;
                        super.visitFieldInsn(opcode, owner, name, desc);
                    }
                };

            } else if ("<init>".equals(name)) {

                mv = new MethodVisitor(Opcodes.ASM5, mv) {
                    @Override
                    public void visitLdcInsn(Object cst) {

                        if (cst != null && cst instanceof String && canEncrypted((String) cst)) {
                            String originValue = (String) cst;
                            String encryptValue = mStrRepImpl.rep(originValue, config.getRepTable(), mKey);
                            super.visitLdcInsn(encryptValue);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, mFogClassName, "back", "(Ljava/lang/String;)Ljava/lang/String;", false);
                        } else {
                            super.visitLdcInsn(cst);
                        }
                    }
                };
            } else {
                mv = new MethodVisitor(Opcodes.ASM5, mv) {

                    @Override
                    public void visitLdcInsn(Object cst) {
                        if (cst != null && cst instanceof String && canEncrypted((String) cst)) {

                            for (ClassStringField field : mStaticFinalFields) {
                                if (cst.equals(field.value)) {
                                    super.visitFieldInsn(Opcodes.GETSTATIC, mClassName, field.name, ClassStringField.STRING_DESC);
                                    return;
                                }
                            }

                            for (ClassStringField field : mFinalFields) {

                                if (cst.equals(field.value)) {
                                    super.visitVarInsn(Opcodes.ALOAD, 0);
                                    super.visitFieldInsn(Opcodes.GETFIELD, mClassName, field.name, "Ljava/lang/String;");
                                    return;
                                }
                            }

                            String originValue = (String) cst;
                            String encryptValue = mStrRepImpl.rep(originValue, config.getRepTable(), mKey);
                            super.visitLdcInsn(encryptValue);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, mFogClassName, "back", "(Ljava/lang/String;)Ljava/lang/String;", false);
                            return;
                        }
                        super.visitLdcInsn(cst);
                    }

                };
            }
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (!mIgnoreClass && !isClInitExists && !mStaticFinalFields.isEmpty()) {
            MethodVisitor mv = super.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();

            for (ClassStringField field : mStaticFinalFields) {
                if (!canEncrypted(field.value)) {
                    continue;
                }
                String originValue = field.value;
                String encryptValue = mStrRepImpl.rep(originValue, config.getRepTable(), mKey);
                mv.visitLdcInsn(encryptValue);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, mFogClassName, "back", "(Ljava/lang/String;)Ljava/lang/String;", false);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, mClassName, field.name, ClassStringField.STRING_DESC);
            }
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(1, 0);
            mv.visitEnd();
        }
        super.visitEnd();
    }

    private boolean canEncrypted(String value) {
        if(StrRepWrapper.overflow(value,config.getRepTable(), mKey)){
            return false;
        }
        if(TextUtils.isEmptyAfterTrim(value)){
            return false;
        }
        System.out.println(value +" true");
        return true;
    }

    private String getJavaClassName() {
        return mClassName != null ? mClassName.replace('/', '.') : null;
    }

}
