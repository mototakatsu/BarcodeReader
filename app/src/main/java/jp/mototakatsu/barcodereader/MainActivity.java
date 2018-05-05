package jp.mototakatsu.barcodereader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Detector.Processor<Barcode> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_HANDLE_CAMERA = 1;

    private CameraSource mCameraSource;
    private SurfaceView mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreview = (SurfaceView)findViewById(R.id.preview);

        // Check for the camera permission
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onDestroy() {
        if (mCameraSource != null) {
            mCameraSource.stop();
            mCameraSource.release();
        }
        super.onDestroy();
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA);
        }
        else {
            // show error message and finish this application
            showPermissionErrorMessage();
        }
    }

    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .build();
        barcodeDetector.setProcessor(this);

        mCameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        mPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                startCameraSource();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                mCameraSource.stop();
            }
        });
    }

    private void startCameraSource() {
        try {
            mCameraSource.start(mPreview.getHolder());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void showPermissionErrorMessage() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.error_camera_permission)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        MainActivity.this.finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
            startCameraSource();
        }
        else {
            // show error message and finish this application
            showPermissionErrorMessage();
        }
    }

    /// Detector.Processor<Barcode>

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        if (detections.getDetectedItems().size() > 0) {
            String displayValue = detections.getDetectedItems().valueAt(0).displayValue;
            Log.d(TAG, "displayValue: " + displayValue);
            startActivity(ResultActivity.createIntent(this, displayValue));
        }
    }

    @Override
    public void release() {}
}
