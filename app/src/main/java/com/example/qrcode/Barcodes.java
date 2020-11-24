package com.example.qrcode;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.qrcode.Adapters.ImageAdapter;

import java.io.File;

public class Barcodes extends AppCompatActivity {
    private File path;
    private String[] files;
    private GridView gridView;
    private SharedPreferences sharedPreferences;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("mkey", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodes);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Generated Barcodes");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            path = new File(Environment.getExternalStorageDirectory(), "Qrcodes");
            if (path.exists()) {
                files = path.list();
                gridView = findViewById(R.id.grid_view);
                gridView.setAdapter(new ImageAdapter(this, files, path));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), fullScreenBarcodes.class);
                        intent.putExtra("id", position);
                        intent.putExtra("files", files);
                        intent.putExtra("path", path);
                        startActivity(intent);
                    }
                });
            }
        }else{
            requestInternalPermission();
        }
    }
    private void requestInternalPermission(){

        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},100);

    }
}