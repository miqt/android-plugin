package com.asm.code;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.miqt.pluginlib.annotation.HookInfo;
import com.miqt.pluginlib.annotation.HookMethod;
import com.miqt.pluginlib.annotation.IgnoreMethodHook;

import java.util.Random;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(v -> Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_SHORT).show());
    }
    public static void ccc(View view){
        view.setOnClickListener(v -> {
            "aa".toString().getBytes();
        });
    }
    private  static void aaa(View view){
        view.setOnClickListener(v -> {
            "aa".toString().getBytes();
        });
    }
    private void bbb(View view){
        view.setOnClickListener(v -> {
            "aa".toString().getBytes();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
       runOnUiThread(() -> {
           Toast.makeText(this, "xxx", Toast.LENGTH_SHORT).show();
       });
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(){
            @HookInfo
            @Override
            public void run() {
                super.run();
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @HookInfo
    @Override
    protected void onStop() {
        super.onStop();
    }

    @HookMethod
    public void testHookMethod() {
        System.out.println("testHookMethod");
    }

    @IgnoreMethodHook
    public void testIgHookMethod() {
        System.out.println("testIgHookMethod");
    }
}
