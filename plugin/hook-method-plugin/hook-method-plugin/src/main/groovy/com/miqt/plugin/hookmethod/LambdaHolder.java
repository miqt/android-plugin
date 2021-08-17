package com.miqt.plugin.hookmethod;

public class LambdaHolder {
    String methodName;
    String lambdaOwner,lambdaDescriptor,lambdaInterface;

    public LambdaHolder(String methodName, String lambdaOwner, String lambdaDescriptor, String lambdaInterface) {
        this.methodName = methodName;
        this.lambdaOwner = lambdaOwner;
        this.lambdaDescriptor = lambdaDescriptor;
        this.lambdaInterface = lambdaInterface;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getLambdaOwner() {
        return lambdaOwner;
    }

    public String getLambdaDescriptor() {
        return lambdaDescriptor;
    }

    public String getLambdaInterface() {
        return lambdaInterface;
    }
}
