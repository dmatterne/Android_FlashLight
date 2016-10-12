package be.david.myflash;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import static android.R.id.toggle;
import static be.david.myflash.R.id.toggleButton;

public class MainActivity extends AppCompatActivity {

    private boolean hasFlash;

    private CameraManager manager;
    private String flashCameraId;
    private ToggleButton toggleButtons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButtons = (ToggleButton) findViewById(toggleButton);

//        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);


        manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String[] cameraIdList = manager.getCameraIdList();

            for (String x : cameraIdList) {

                hasFlash = manager.getCameraCharacteristics(x).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

                if (hasFlash) {

                    flashCameraId = x;
                    break;

                }
            }

        } catch (CameraAccessException e) {
//            e.printStackTrace();
            showAlert();
        }

        toggleButtons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               toggleFlash(isChecked);

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (toggleButtons.isChecked()) {
            toggleFlash(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (toggleButtons.isChecked()) {
            toggleFlash(false);
        }
    }

    private void toggleFlash(boolean onOff) {

        Log.d("MyFlash", "Setting Torch mode to " + onOff);
        if (manager == null || flashCameraId == null) return;

        try {
            manager.setTorchMode(flashCameraId,onOff);
        } catch (CameraAccessException e) {
            Log.e("MyFlash","Unable to change the Torchmode", e);
        }

        toggleButtons.setChecked(onOff);



    }


    private void showAlert() {
        AlertDialog alert = new AlertDialog.Builder(this).create();

        alert.setTitle("Error");
        alert.setMessage("Your device does not have a camera");
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }
}
