package com.ljk.mytest;

import com.ljk.bss_mymqtt.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);  
        int heapSize = manager.getLargeMemoryClass(); 
        TextView textView=(TextView) findViewById(R.id.showtext);
        textView.setText(""+heapSize+"---"+manager.getMemoryClass());
    }

}
