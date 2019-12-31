package cn.jtduan.crack;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        System.out.println("====" + context.getClass().getName());

        TextView tv = findViewById(R.id.sample_text);
        new NativeAPI().updateKey("345");
//        tv.setText("res=" + new NativeAPI().callJava("123"));
        tv.setText("res=" + new NativeAPI().stringFromJNI());
    }
}
