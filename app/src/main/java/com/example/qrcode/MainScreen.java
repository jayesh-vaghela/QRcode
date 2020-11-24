package com.example.qrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.qrcode.Adapters.PageAdapter;
import com.example.qrcode.Fragments.Generate;
import com.example.qrcode.Fragments.Scan;
import com.example.qrcode.Settings.SettingsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

public class MainScreen extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SharedPreferences sharedPreferences;
    private PageAdapter pa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("mkey",false)){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        pa = new PageAdapter(getSupportFragmentManager());
        pa.addfragment(new Generate());
        pa.addfragment(new Scan());
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(pa);
        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Generate");
        tabLayout.getTabAt(1).setText("Scanner");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pa.fragmentList.clear();
    }

    @Override
    protected void onResume() {
       SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("mkey",false)){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.barcodes:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this,Barcodes.class));
                    return true;
                }else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},101);
                        return true;
                    }
                }
            case R.id.scan:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Intent barcode=new Intent(Intent.ACTION_PICK);
                         barcode.setType("image/*");
                        startActivityForResult(barcode,100);
                        return  true;}
                else {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},101);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==100) && (resultCode==RESULT_OK)){
            Uri selected_barcode=data.getData();
            InputStream imageStream=null;
            try{
                imageStream=getApplication().getContentResolver().openInputStream(selected_barcode);
            }catch (FileNotFoundException e){
                Toast.makeText(getApplicationContext(), "Srry Barcode Not Found.", Toast.LENGTH_SHORT).show();
            }
            Bitmap bitmap= BitmapFactory.decodeStream(imageStream);
            int[] ints=new int[bitmap.getWidth()*bitmap.getHeight()];
            bitmap.getPixels(ints,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
            LuminanceSource source=new RGBLuminanceSource(bitmap.getWidth(),bitmap.getHeight(),ints);
            BinaryBitmap bitmap1=new BinaryBitmap(new HybridBinarizer(source));
            Reader reader=new MultiFormatReader();
            try{
                Hashtable<DecodeHintType,Object> decodehints=new Hashtable<DecodeHintType, Object>();
                decodehints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
                decodehints.put(DecodeHintType.PURE_BARCODE,Boolean.TRUE);
                Result result=reader.decode(bitmap1,decodehints);
                final String barcode_data = result.getText().toString();
                if (barcode_data != null) {
                    AlertDialog alertDialog=null;
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("Scan Result");
                    builder.setMessage(barcode_data);
                    builder.setPositiveButton("copy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                            ClipData data=ClipData.newPlainText("result",barcode_data);
                            clipboardManager.setPrimaryClip(data);
                        }
                    });
                    builder.setNeutralButton("visit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                if (barcode_data.toLowerCase().startsWith("http")) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(barcode_data));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Barcode is not a Link", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Barcode is not a Link", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog=builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }catch (NotFoundException e){
                Toast.makeText(getApplicationContext(), "Nothing Found.", Toast.LENGTH_LONG).show();
            }
            catch (ChecksumException e){
                Toast.makeText(getApplicationContext(), "Srry Something Wrong.", Toast.LENGTH_LONG).show();
            }
            catch (FormatException e){
                Toast.makeText(getApplicationContext(), "Wrong Barcode/QR Format.", Toast.LENGTH_LONG).show();
            }
            catch (NullPointerException e){
                Toast.makeText(getApplicationContext(), "Nothing Found.", Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"Something Went Wrong.", Toast.LENGTH_LONG).show();
            }
        }
    }

}