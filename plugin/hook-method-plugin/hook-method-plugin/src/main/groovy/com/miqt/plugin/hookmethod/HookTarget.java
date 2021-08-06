package com.miqt.plugin.hookmethod;

import java.util.List;
import java.util.regex.Pattern;

public class HookTarget {
    private int access = -1;//方法的访问权限
    private String interfaces;//继承类
    private String superName;//所在父类
    private String className;//方法所在的类名
    private String methodName;//方法名称
    private String descriptor;//方法参数和返回值字段描述符
    private String annotation;//方法上的注解
    private String signature;//方法参数或返回值为泛型
    private String[] exceptions;//方法抛出那些异常
    private int hookTiming = Enter | Return;//是在方法进入时hook还是退出时
    public final static int Enter = 1 << 1;//方法进入
    public final static int Return = 1 << 2;//方法退出

    public HookTarget() {
    }

    public int getAccess() {
        return access;
    }

    public HookTarget setAccess(int access) {
        this.access = access;
        return this;
    }

    public String getInterfaces() {
        return interfaces;
    }

    public HookTarget setInterfaces(String interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    public String getSuperName() {
        return superName;
    }

    public HookTarget setSuperName(String superName) {
        this.superName = superName;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public HookTarget setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public HookTarget setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public HookTarget setDescriptor(String descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public String getAnnotation() {
        return annotation;
    }

    public HookTarget setAnnotation(String annotation) {
        this.annotation = annotation;
        return this;
    }

    public String getSignature() {
        return signature;
    }

    public HookTarget setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public HookTarget setExceptions(String[] exceptions) {
        this.exceptions = exceptions;
        return this;
    }

    public int getHookTiming() {
        return hookTiming;
    }

    public HookTarget setHookTiming(int hookTiming) {
        this.hookTiming = hookTiming;
        return this;
    }

    public static int getEnter() {
        return Enter;
    }

    public static int getReturn() {
        return Return;
    }

    boolean isMatch(int access,//方法的访问权限
                    String[] interfaces,//继承类
                    String superName,//所在父类
                    String className,//方法所在的类名
                    String methodName,//方法名称
                    String descriptor,//方法参数和返回值字段描述符
                    List<String> methodAnnotation,//方法上的注解
                    List<String> classAnnotation,//方法上的注解
                    String signature,//方法参数或返回值为泛型
                    String[] exceptions,//方法抛出那些异常
                    int hookTiming
    ) {
        if (this.access != -1 && this.access != access) {
            return false;
        }
        if (!isEmpty(this.interfaces)) {
            for (String interfaceItem : interfaces
            ) {
                if (!interfaceItem.equals(this.interfaces)) return false;
            }
        }
        if (!isEmpty(this.superName) && !this.superName.equals(superName)) {
            return false;
        }
        if (!isEmpty(this.className) && !Pattern.matches(className, className)) {
            return false;
        }
        if (!isEmpty(this.methodName) && !Pattern.matches(methodName, methodName)) {
            return false;
        }
        if (!isEmpty(this.descriptor) && !this.descriptor.equals(descriptor)) {
            return false;
        }
        if (!isEmpty(this.annotation)) {
            if (!methodAnnotation.contains(this.annotation)
                    && !classAnnotation.contains(this.annotation))
                return false;
        }
        if (!isEmpty(this.signature) && !this.signature.equals(signature)) {
            return false;
        }
        if (this.exceptions != null) {
            if (exceptions == null) {
                return false;
            }
            if (this.exceptions.length != exceptions.length) {
                return false;
            }
            for (int i = 0; i < exceptions.length; i++) {
                if (!exceptions[i].equals(this.exceptions[i])) {
                    return false;
                }
            }
        }

        if ((hookTiming & this.hookTiming) == 0) {
            return false;
        }
        return true;
    }

    private boolean isEmpty(String value) {
        return value == null || value.equals("");
    }

}
