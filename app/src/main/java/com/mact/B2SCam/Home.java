package com.mact.B2SCam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    Button htu,contact,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        htu = (Button)findViewById(R.id.btnHowToUse);
        back = (Button)findViewById(R.id.btnBack);
        contact = (Button)findViewById(R.id.btnContact);
        htu.setOnClickListener(view -> {
            Intent i = new Intent(Home.this, SliderActivity.class);
            startActivity(i);
            finish();
        });
        back.setOnClickListener(view -> {
            Intent i = new Intent(Home.this,CameraActivity.class);
            startActivity(i);
            finish();
        });
        contact.setOnClickListener(view -> {
            Uri uri = Uri.parse("http://demonboyiscurrentlylive.on.drv.tw/www.B2SCam.com/"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }
}