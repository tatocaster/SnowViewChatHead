package me.tatocaster.snowview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.tatocaster.snowview.services.OverlayService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 6666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Utils.isSystemAlertPermissionGranted(this))
            startOverlayService();
        else
            Utils.requestSystemAlertPermission(this, null, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE && Utils.isSystemAlertPermissionGranted(this))
            startOverlayService();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startOverlayService() {
        if (!Utils.isSnowOverlayingServiceIsRunning(this, OverlayService.class))
            startService(new Intent(MainActivity.this, OverlayService.class));
    }
}
