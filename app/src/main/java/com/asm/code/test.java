package com.asm.code;

import com.miqt.pluginlib.annotation.HookMethod;


public  class test {
    @HookMethod
    static void aaa(int a) {

    }

    @HookMethod
    static void aaa() {
         class innerc2 {
            @HookMethod
             void aaa(int a) {

            }

            @HookMethod
            void aaa() {

            }
        }
    }

    class innerc {
        @HookMethod
        void aaa(int a) {

        }

        @HookMethod
        void aaa() {

        }
    }

    static class innerc2 {
        @HookMethod
        static void aaa(int a) {

        }

        @HookMethod
        void aaa() {

        }

        @HookMethod
        static void aaa2(Object xxx) {

        }
        class innerc {
            @HookMethod
            void aaa(int a) {

            }

            @HookMethod
            void aaa() {

            }
        }
    }
}
