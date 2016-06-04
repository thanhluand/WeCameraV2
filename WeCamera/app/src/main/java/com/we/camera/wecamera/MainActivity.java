package com.we.camera.wecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    Button btnCamera;
    Button btnAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        RelativeLayout layoutApp = (RelativeLayout)findViewById(R.id.layoutApp);
        Bitmap bmpOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.backgroundcamera2);
        BitmapDrawable bmpBackground = new BitmapDrawable(getResources(), bmpOriginal);
        layoutApp.setBackground(bmpBackground);

        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btnAlbum = (Button)findViewById(R.id.btnAlbum);
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EffectActivity.class);
                try
                {
                    startActivityForResult(intent, 1);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
