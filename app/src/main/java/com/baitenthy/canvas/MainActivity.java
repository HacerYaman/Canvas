package com.baitenthy.canvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.baitenthy.canvas.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1001;
    private ActivityMainBinding binding;
    int defColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().hide();

        defColor = ContextCompat.getColor(MainActivity.this, R.color.black);

        binding.penSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.txtPenSize.setText(progress + "dp");
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
                int eraser = ContextCompat.getColor(MainActivity.this, R.color.white);
                binding.signatureView.setPenColor(eraser);
            }
        });

        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signatureView.clearCanvas();
            }
        });

        binding.pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int penColor = ContextCompat.getColor(MainActivity.this, R.color.black);
                binding.signatureView.setPenColor(penColor);
            }
        });
    }

    private void openColorWheel() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(MainActivity.this, defColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defColor = color;
                binding.signatureView.setPenColor(color);
            }
        });
        ambilWarnaDialog.show();
    }

    public void saveFile(View view) {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            saveBitmapasPNG();
        }

    }
    private void saveBitmapasPNG() {
        Bitmap bitmap = binding.signatureView.getSignatureBitmap();

        String file_name= UUID.randomUUID()+".png";
        File fileee = new File(getFilesDir() + file_name);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileee);
            fileOutputStream.write(byteArray);
            fileOutputStream.flush();
            fileOutputStream.close();

            MediaStore.Images.Media.insertImage(getContentResolver(),fileee.getAbsolutePath(),fileee.getName(),fileee.getName());

            Toast.makeText(this, "CANVAS SAVED", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveBitmapasPNG();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

  /*  private void askPermission() { WITH DEXTER
        Dexter.withContext(MainActivity.this)
                .withPermissions(
                                 Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                 Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                Toast.makeText(MainActivity.this, "Granted.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }*/

}