package com.asm.code;

import java.util.Arrays;

public class Test {
    public static void m1() {


        int a = 100 + 1;
        int b = a + a;
        String s = "abc";
        System.out.println(b);
        System.out.println(s);
    }

    public static String m2() {
        char[] value = new char[100];
        Arrays.fill(value, '┆');
        return new String(value);
    }

    public String m3() {
        char[] value = new char[100];
        Arrays.fill(value, '┆');
        return new String(value);
    }
}
