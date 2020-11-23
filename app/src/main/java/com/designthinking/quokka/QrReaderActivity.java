package com.designthinking.quokka;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.ViewfinderView;

public class QrReaderActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private static final int DEVICE_NUMBER_REQUEST = 2001;

    DecoratedBarcodeView barcodeView;
    CaptureManager capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);

        barcodeView = findViewById(R.id.zxing_barcode_scanner);
        barcodeView.setTorchListener(this);
        //barcodeView.getBarcodeView().setFramingRectSize(new Size(100, 100));
        //barcodeScannerView.setTorchListener(this);

        ViewfinderView viewfinderView = findViewById(R.id.zxing_viewfinder_view);
        viewfinderView.setLaserVisibility(false);

        capture = new CaptureManager(this, barcodeView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        Button numberBtn = findViewById(R.id.number_input_btn);
        numberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(QrReaderActivity.this, CodeActivity.class), DEVICE_NUMBER_REQUEST);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    /*public void switchFlashlight(View view) {
        if (switchFlashlightButtonCheck) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }*/

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public void onTorchOn() {
        //switchFlashlightButton.setImageResource(R.drawable.ic_flash_on_white_36dp);
        //switchFlashlightButtonCheck = false;
    }

    @Override
    public void onTorchOff() {
        //switchFlashlightButton.setImageResource(R.drawable.ic_flash_off_white_36dp);
        //switchFlashlightButtonCheck = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DEVICE_NUMBER_REQUEST && resultCode == RESULT_OK) {
            // pass by
            Intent intent = new Intent();
            intent.putExtra("code", data.getStringExtra("code"));

            setResult(RESULT_OK, intent);
            finish();
        }
    }
}