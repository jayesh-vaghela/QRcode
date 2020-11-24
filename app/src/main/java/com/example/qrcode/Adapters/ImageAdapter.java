package com.example.qrcode.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class ImageAdapter extends BaseAdapter {
    private Context mcontext;
    private String[] files;
    private File path;

    public ImageAdapter(Context mcontext, String[] files, File path) {
        this.mcontext = mcontext;
        this.files = files;
        this.path = path;
    } @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int position) {
        return files[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView==null){
            imageView=new ImageView(mcontext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(500,500));}
        else {
            imageView=(ImageView)convertView;
        }
        Bitmap bitmap= BitmapFactory.decodeFile(path.getPath()+"/"+files[position]);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

}
