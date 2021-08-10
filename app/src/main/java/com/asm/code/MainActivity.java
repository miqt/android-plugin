package com.asm.code;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.miqt.pluginlib.annotation.HookMethod;
import com.miqt.pluginlib.annotation.IgnoreMethodHook;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
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
