package com.example.mylocation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openMainActivity(View v){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);

    }
}
