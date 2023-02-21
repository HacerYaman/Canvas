package com.baitenthy.canvas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.baitenthy.canvas.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static String fileName;
    File filePath= new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "myPaintings");
    int defColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        defColor= ContextCompat.getColor(MainActivity.this, R.color.black);

        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date= simpleDateFormat.format(new Date());
        fileName= filePath+"/"+date+".png";

        if (!filePath.exists()){
            filePath.mkdirs();
        }


        binding.penSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.txtPenSize.setText(progress+"dp");
                binding.signatureView.setPenSize(progress);
                seekBar.setMax(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.colorWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorWheel();
            }
        });

        binding.eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int eraser= ContextCompat.getColor(MainActivity.this,R.color.white);
                binding.signatureView.setPenColor(eraser);
            }
        });

        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signatureView.clearCanvas();
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.signatureView.isBitmapEmpty()){
                    try {
                        saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        askPermission();
    }

    private void saveImage() throws IOException {

        File file= new File(fileName);
        Bitmap bitmap= binding.signatureView.getSignatureBitmap();

        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        byte[] bitmapData= byteArrayOutputStream.toByteArray();

        FileOutputStream fileOutputStream= new FileOutputStream(file);
        fileOutputStream.write(bitmapData);
        fileOutputStream.flush();
        fileOutputStream.close();

        Toast.makeText(this, "Painting Saved!", Toast.LENGTH_SHORT).show();
    }

    private void openColorWheel() {

        AmbilWarnaDialog ambilWarnaDialog= new AmbilWarnaDialog(MainActivity.this, defColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defColor=color;
                binding.signatureView.setPenColor(color);
            }
        });
        ambilWarnaDialog.show();
    }

    private void askPermission() {
        Dexter.withContext(MainActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(MainActivity.this, "permission granted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();        //kullanıcı izni verene kadar soracak
                    }
                }).check();
    }
}