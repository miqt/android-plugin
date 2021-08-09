package com.miqt.plugin.hookmethod

import org.gradle.api.Project
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

class MethodHookVisitor extends ClassVisitor {

    private String[] interfaces;//继承类
    private String superName;//所在父类
    private String className;//方法所在的类名

    private List<String> classAnnotation = new ArrayList<>();//方法注释
    private HookMethodExtension config
    private Project project
    private HookMethodPlugin plugin
    private boolean isIgnoreMethodHook = false

    public MethodHookVisitor(ClassVisitor classVisitor, HookMethodPlugin plugin) {
        super(Opcodes.ASM5, classVisitor)
        this.config = plugin.getExtension()
        this.project = plugin.getProject()
        this.plugin = plugin;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
        this.interfaces = interfaces
        this.superName = superName
    }

    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor == "Lcom/miqt/pluginlib/annotation/IgnoreMethodHook;") {
            isIgnoreMethodHook = true
        }
        classAnnotation.add(descriptor)
        return super.visitAnnotation(descriptor, visible)
    }

    HookTarget isMatch(int access = -1,//方法的访问权限
                       String[] interfaces,//继承类
                       String superName,//所在父类
                       String className,//方法所在的类名
                       String methodName,//方法名称
                       String descriptor,//方法参数和返回值字段描述符
                       List<String> methodAnnotation,//方法上的注解
                       List<String> classAnnotation,//方法上的注解
                       String signature,//方法参数或返回值为泛型
                       String[] exceptions,//方法抛出那些异常
                       String hookTiming,
                       boolean isIgnoreMethodHook
    ) {
        if (isIgnoreMethodHook) {
            return null
        }
        List<HookTarget> targets = plugin.getExtension().hookTargets;
        for (i in 0..<targets.size()) {
            if (targets.get(i).isMatch(
                    access, interfaces, superName, className,
                    methodName, descriptor, methodAnnotation, classAnnotation,
                    signature, exceptions, hookTiming)) {
                return targets.get(i)
            }
        }
        return null
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (isIgnoreMethodHook)
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        def isInit = name == "<init>"
        def isStaticInit = name == "<clinit>"
        def isUnImplMethod = (access & Opcodes.ACC_ABSTRACT) != 0
        if (isUnImplMethod) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }

        Type[] argsTypes = Type.getArgumentTypes(descriptor)
        Type returnType = Type.getReturnType(descriptor)
        def isStatic = (access & Opcodes.ACC_STATIC) != 0


        def mv = cv.visitMethod(access, name, descriptor, signature, exceptions)
        mv = new AdviceAdapter(Opcodes.ASM5, mv, access, name, descriptor) {

            private List<String> methodAnnotation = new ArrayList<>();//方法注释
            @Override
            AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                if (descriptor == "Lcom/miqt/pluginlib/annotation/IgnoreMethodHook;") {
                    isIgnoreMethodHook = true
                }
                methodAnnotation.add(desc)
                return super.visitAnnotation(desc, visible)
            }

            @Override
            protected synchronized void onMethodEnter() {
                HookTarget target = isMatch(access, interfaces, superName, className,
                        name, descriptor, methodAnnotation, classAnnotation,
                        signature, exceptions, HookTarget.Enter, isIgnoreMethodHook)
                if (target == null) {
                    return
                }
                getArgs()
                mv.visitMethodInsn(INVOKESTATIC,  config.impl.replace(".","/"),
                        target.getEnterMethodName(),
                        "(" +
                                "Ljava/lang/Object;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/String;" +
                                "[Ljava/lang/Object;" +
                                ")V",
                        false)
                plugin.getLogger().log("\t[MethodEnter]" + className + "." + name +
                        "\n\t\t-->" + config.impl + "." + target.getEnterMethodName())

                super.onMethodEnter()
            }

            @Override
            protected synchronized void onMethodExit(int opcode) {
                HookTarget target = isMatch(access, interfaces, superName, className,
                        name, descriptor, methodAnnotation, classAnnotation,
                        signature, exceptions, HookTarget.Return, isIgnoreMethodHook)
                if (target == null) {
                    return
                }
                //有返回值的装载返回值参数，无返回值的装载null
                if (opcode >= IRETURN && opcode < RETURN) {
                    if (returnType.sort == Type.LONG || returnType.sort == Type.DOUBLE) {
                        mv.visitInsn(DUP2)
                    } else {
                        mv.visitInsn(DUP)
                    }
                    ClassUtils.autoBox(mv, returnType)
                    getArgs()
                    mv.visitMethodInsn(INVOKESTATIC,  config.impl.replace(".","/"),
                            target.getReturnMethodName(),
                            "(" +
                                    "Ljava/lang/Object;" + //return
                                    "Ljava/lang/Object;" + //this
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "[Ljava/lang/Object;" +//prams
                                    ")V",
                            false)
                    plugin.getLogger().log("\t[MethodReturn]" + className + "." + name +
                            "\n\t\t-->" + config.impl + "." + target.getReturnMethodName())
                } else if (opcode == RETURN) {
                    mv.visitInsn(ACONST_NULL)
                    getArgs()
                    mv.visitMethodInsn(INVOKESTATIC, config.impl.replace(".","/"),
                            target.getReturnMethodName(),
                            "(" +
                                    "Ljava/lang/Object;" + //return
                                    "Ljava/lang/Object;" + //this
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "Ljava/lang/String;" +
                                    "[Ljava/lang/Object;" +//prams
                                    ")V",
                            false)
                    plugin.getLogger().log("\t[MethodReturn]" + className + "." + name +
                            "\n\t\t-->" + config.impl + "." + target.getReturnMethodName())
                }


            }

            /**
             * 装载this，方法名等共 6 个参数
             */
            private void getArgs() {
                if (isStatic || isInit || isStaticInit) {
                    mv.visitInsn(ACONST_NULL)//null
                } else {
                    mv.visitVarInsn(ALOAD, 0)//this
                }
                mv.visitLdcInsn(className)//className
                mv.visitLdcInsn(name)//methodbName
                mv.visitLdcInsn(getArgsType())//argsTypes
                mv.visitLdcInsn(returnType.className)//returntype

                getICONST(argsTypes == null ? 0 : argsTypes.length)
                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object")
                if (argsTypes != null) {
                    def valLen = 0
                    for (int i = 0; i < argsTypes.length; i++) {
                        mv.visitInsn(DUP)
                        getICONST(i)
                        getOpCodeLoad(argsTypes[i], isStatic ? (valLen) : (valLen + 1))
                        mv.visitInsn(AASTORE)
                        valLen += getlenByType(argsTypes[i])
                    }
                }
            }

            void getICONST(int i) {
                if (i == 0) {
                    mv.visitInsn(ICONST_0)
                } else if (i == 1) {
                    mv.visitInsn(ICONST_1)
                } else if (i == 2) {
                    mv.visitInsn(ICONST_2)
                } else if (i == 3) {
                    mv.visitInsn(ICONST_3)
                } else if (i == 4) {
                    mv.visitInsn(ICONST_4)
                } else if (i == 5) {
                    mv.visitInsn(ICONST_5)
                } else {
                    mv.visitIntInsn(BIPUSH, i)
                }
            }

            void getOpCodeLoad(Type type, int argIndex) {
                if (type.equals(Type.INT_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false)
                    return
                }
                if (type.equals(Type.BOOLEAN_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false)
                    return
                }
                if (type.equals(Type.CHAR_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false)
                    return
                }
                if (type.equals(Type.SHORT_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false)
                    return
                }
                if (type.equals(Type.BYTE_TYPE)) {
                    mv.visitVarInsn(ILOAD, argIndex)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false)
                    return
                }

                if (type.equals(Type.LONG_TYPE)) {
                    mv.visitVarInsn(LLOAD, argIndex)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false)
                    return
                }
                if (type.equals(Type.FLOAT_TYPE)) {
                    mv.visitVarInsn(FLOAD, argIndex)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false)
                    return
                }
                if (type.equals(Type.DOUBLE_TYPE)) {
                    mv.visitVarInsn(DLOAD, argIndex)
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false)
                    return
                }
                mv.visitVarInsn(ALOAD, argIndex)
            }

            String getArgsType() {
                if (argsTypes == null)
                    return "null"

                int iMax = argsTypes.length - 1
                if (iMax == -1)
                    return "[]"

                StringBuilder b = new StringBuilder()
                b.append('[')
                for (int i = 0; ; i++) {
                    b.append(String.valueOf(argsTypes[i].className))
                    if (i == iMax)
                        return b.append(']').toString()
                    b.append(", ")
                }
            }

            int getlenByType(Type type) {
                if (type.equals(Type.DOUBLE_TYPE)
                        || type.equals(Type.LONG_TYPE)) {
                    return 2
                }
                return 1
            }
        }
    }
}
