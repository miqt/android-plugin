package com.miqt.pluginlib.tools;

import android.os.SystemClock;
import android.util.Log;

import com.miqt.pluginlib.annotation.IgnoreMethodHook;

import java.util.Arrays;
import java.util.Stack;

@IgnoreMethodHook
public class MethodHookPrint implements IMethodHookHandler {
    private String tag = "MethodHookHandler";
    private final ThreadLocal<Stack<Long>> local = new ThreadLocal<>();

    public MethodHookPrint(String tag) {
        this.tag = tag;
    }

    public MethodHookPrint() {
    }

    @Override
    public void onMethodEnter(Object thisObj, String className, String methodName, String argsType, String returnType, Object... args) {
        Stack<Long> queue = local.get();
        if (queue == null) {
            queue = new Stack<>();
            local.set(queue);
        }
        String value = getSpace(queue.size()) +
                "┌" + (thisObj == null ? className : className + "@" + Integer.toHexString(System.identityHashCode(thisObj))) +
                "." + methodName +
                "():[" + Thread.currentThread().getName() + "]";
        Log.i(tag == null ? "MethodHookHandler" : tag, value);
        queue.push(SystemClock.elapsedRealtime());
    }

    @Override
    public void onMethodReturn(Object returnObj, Object thisObj, String className, String methodName, String argsType, String returnType, Object... args) {
        String[] strings = className.split("/");
        StackTraceElement element = Thread.currentThread().getStackTrace()[4];
        String linkFile = "(" + strings[strings.length-1] + ".java:" + element.getLineNumber() + ")";
        Stack<Long> queue = local.get();
        assert queue != null;
        Long time = queue.pop();
        if (time != null) {
            long duc = SystemClock.elapsedRealtime() - time;
            String value = getSpace(queue.size()) +
                    "└" + (thisObj == null ? className : className + "@" + Integer.toHexString(System.identityHashCode(thisObj))) +
                    "." + methodName +
                    "():[" + duc + "]"+linkFile;


            if (duc >= 1000) {
                Log.e(tag == null ? "MethodHookHandler" : tag, value);
            } else if (duc >= 600) {
                Log.w(tag == null ? "MethodHookHandler" : tag, value);
            } else if (duc >= 300) {
                Log.d(tag == null ? "MethodHookHandler" : tag, value);
            } else {
                Log.i(tag == null ? "MethodHookHandler" : tag, value);
            }
        }
    }


    public String getSpace(int size) {
        char[] value = new char[size];
        Arrays.fill(value, '|');
        return new String(value);
    }

}
