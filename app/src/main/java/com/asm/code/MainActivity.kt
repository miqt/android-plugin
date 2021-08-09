package com.asm.code

import android.os.Bundle
import android.util.Log
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import com.miqt.pluginlib.annotation.HookInfo
import com.miqt.pluginlib.annotation.HookMethod
import com.miqt.pluginlib.annotation.IgnoreMethodHook

class MainActivity : AppCompatActivity() {
    @HookInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            Test.m1();
            hello1()
            hello2()
            try {
                hello3()
                hello4()
            } catch (e: Exception) {
            }
            hello4()
        } catch (e: Exception) {
        } finally {
        }
    }

    fun hello1() {

    }

    fun hello2() {
        hello4()
    }

    fun hello3() {
        case1()
        case2()
        case3()

    }

    fun hello4() {
        case1()
        case2()
        case3()
    }

    @HookMethod
    @IgnoreMethodHook
    @MainThread
    fun case1() {

    }

    fun case2() {
        case3();
    }

    fun case3() {

    }



}