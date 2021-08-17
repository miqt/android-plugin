package com.miqt.plugin.hookmethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class HookTarget {
    public String name = "Preset";
    public int access = -1;//方法的访问权限
    public String interfaces;//继承类
    public String superName;//所在父类
    public String className;//方法所在的类名
    public String methodName;//方法名称
    public String descriptor;//方法参数和返回值字段描述符
    public String annotation;//方法上的注解
    public String signature;//方法参数或返回值为泛型
    public String[] exceptions;//方法抛出那些异常
    public String hookTiming = "Enter|Return";//"Enter"|"Return";//是在方法进入时hook还是退出时
    public static final String Enter = "Enter";
    public static final String Return = "Return";

    public HookTarget(String name) {
        this.name = name;
    }

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

    public String getHookTiming() {
        return hookTiming;
    }

    public HookTarget setHookTiming(String hookTiming) {
        this.hookTiming = hookTiming;
        return this;
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
                    String hookTiming
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
        if (!isEmpty(this.className) && !Pattern.matches(this.className, className)) {
            return false;
        }
        if (!isEmpty(this.methodName) && !Pattern.matches(this.methodName, methodName)) {
            return false;
        }
        if (!isEmpty(this.descriptor) && !this.descriptor.equals(descriptor)) {
            return false;
        }
        if (!isEmpty(this.annotation)) {
            if (classAnnotation == null) {
                classAnnotation = new ArrayList<>();
            }
            if (methodAnnotation == null) {
                methodAnnotation = new ArrayList<>();
            }
            if (!classAnnotation.contains(annotation) && !methodAnnotation.contains(annotation)) {
                return false;
            }
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

        if (!this.hookTiming.contains(hookTiming)) {
            return false;
        }
        return true;
    }

    private boolean isEmpty(String value) {
        return value == null || value.equals("");
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(name + "{\n\t\t");
        sb.append("access=").append(access).append("\n\t\t");
        if (interfaces != null)
            sb.append("interfaces=\"").append(interfaces).append("\"\n\t\t");
        if (superName != null)
            sb.append("superName=\"").append(superName).append("\"\n\t\t");
        if (className != null)
            sb.append("className=\"").append(className).append("\"\n\t\t");
        if (methodName != null)
            sb.append("methodName=\"").append(methodName).append("\"\n\t\t");
        if (descriptor != null)
            sb.append("descriptor=\"").append(descriptor).append("\"\n\t\t");
        if (annotation != null)
            sb.append("annotation=\"").append(annotation).append("\"\"\n\t\t");
        if (signature != null)
            sb.append("signature=\"").append(signature).append("\"\n\t\t");
        if (exceptions != null)
            sb.append("exceptions=").append(Arrays.asList(exceptions).toString()).append("\n\t\t");
        sb.append("hookTiming=\"").append(hookTiming).append("\"\n\t");
        sb.append('}');
        return sb.toString();
    }


    public String getEnterMethodName() {
        return name+"Enter";
    }

    public String getReturnMethodName() {
        return name+"Return";
    }
}
