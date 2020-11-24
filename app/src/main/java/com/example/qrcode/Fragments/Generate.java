package com.example.qrcode.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrcode.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Generate extends Fragment {
    private Button generate,save;
    private EditText mytext;
    private ImageView qr_code;
    private FileOutputStream outputStream = null;
    private boolean generate_barcode=false;


    public Generate() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mytext=(EditText)view.findViewById(R.id.text);
        qr_code=view.findViewById(R.id.qrcode);
        generate=view.findViewById(R.id.generate);
        save=view.findViewById(R.id.save);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=mytext.getText().toString();
                if(text!=null && !text.isEmpty()){
                    try {
                        MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
                        BitMatrix bitMatrix=multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
                        BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
                        Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
                        qr_code.setImageBitmap(bitmap);
                        generate_barcode=true;
                    }
                    catch(WriterException e){
                        e.printStackTrace();
                    }

                }
                else{
                    //Toast.makeText(getContext(),"Nothing has been Entered",Toast.LENGTH_LONG).show();
                    mytext.setError("Nothing has been Entered.");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generate_barcode) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            BitmapDrawable drawable = (BitmapDrawable) qr_code.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            //int check = bitmap.getWidth();
                            File file = Environment.getExternalStorageDirectory();
                            File dir = new File(file.getAbsolutePath() + "/Qrcodes/");
                            if (!dir.exists()) { dir.mkdirs(); }
                            File file1 = new File(dir, mytext.getText().toString() + ".jpg");
                            if(file1.exists()){file1.delete();}
                            try {
                                outputStream = new FileOutputStream(file1);
                            } catch (FileNotFoundException e) {
                                Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            Toast.makeText(getContext(), "Your Barcode has been Saved.", Toast.LENGTH_LONG).show();
                            try {
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (Exception w) {
                            Toast.makeText(getContext(), "Something Went Wrong." + w.toString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        request_permission();
                    }
                }else{
                    Toast.makeText(getActivity(),"Please Generate the Barcode.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void request_permission(){
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generate, container, false);
    }
}