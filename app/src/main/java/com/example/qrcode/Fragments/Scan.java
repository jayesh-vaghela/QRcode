package com.example.qrcode.Fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qrcode.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Scan extends Fragment implements ZXingScannerView.ResultHandler {
   private ZXingScannerView zXingScannerView;
    private LinearLayout linearLayout;

    public Scan() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_scan,container,false);
        linearLayout=(LinearLayout)view.findViewById(R.id.ll_qrcamera);
        zXingScannerView=new ZXingScannerView(getActivity().getApplicationContext());
        zXingScannerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.addView(zXingScannerView);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){}
        else{
            requestCameraPermission();
        }
    }
    private void requestCameraPermission(){

            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.CAMERA},100);

    }


    @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

    }

    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();

    }
    @Override
    public void handleResult(final Result rawResult) {
        if(rawResult!=null){
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setTitle("Scan Result");
            builder.setMessage(rawResult.getText());
            builder.setPositiveButton("copy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipboardManager clipboardManager=(ClipboardManager)getActivity().getSystemService(CLIPBOARD_SERVICE);
                    ClipData data=ClipData.newPlainText("result",rawResult.getText());
                    clipboardManager.setPrimaryClip(data);
                }
            });
            builder.setNeutralButton("visit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (rawResult.getText().toLowerCase().startsWith("http")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rawResult.getText()));
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Barcode is not a Link", Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "Barcode is not a Link", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
        zXingScannerView.resumeCameraPreview(this);
    }
}