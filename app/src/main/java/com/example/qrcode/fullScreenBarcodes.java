package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class fullScreenBarcodes extends AppCompatActivity {
    private ImageView imageView;
    private PhotoViewAttacher photoViewAttacher;
    private File path;
    private String[] files;
    private int i;
    private Intent intent=null,choose=null;
    private String[] imagename;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("mkey",false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_barcodes);
        imageView=(ImageView)findViewById(R.id.image_view);
        Intent intent=getIntent();
        i=intent.getExtras().getInt("id");
        files=intent.getExtras().getStringArray("files");
        path=(File)intent.getSerializableExtra("path");
        imagename=files[i].split("\\.");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(imagename[0]);
        }
        if(path.exists()){
            String[] files=path.list();
            Bitmap bitmap= BitmapFactory.decodeFile(path.getPath()+"/"+files[i]);
            imageView.setImageBitmap(bitmap);
            photoViewAttacher=new PhotoViewAttacher(imageView);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.share_menu_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                Uri uri=Uri.parse(path.getPath()+"/"+files[i]);
                intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                choose=Intent.createChooser(intent,"SendImage via");
                startActivity(choose);
                return true;
            default:
                return super.onOptionsItemSelected(item);}
    }
}
