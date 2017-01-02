package me.tatocaster.snowview.services;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import me.tatocaster.snowview.MainActivity;
import me.tatocaster.snowview.R;
import me.tatocaster.snowview.SnowView.SnowView;

/**
 * Created by tatocaster on 1/1/17.
 */

public class OverlayService extends Service {
    private static final String TAG = "OverlayService";
    private static final int FOREGROUND_ID = 9998;
    private SnowView mSnowView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "ServiceStarted", Toast.LENGTH_SHORT).show();

        mSnowView = new SnowView(this);
        mSnowView.addToWindowManager();

        // this needs to be here, because without the startForeground(), our view will not retain always
        startForeground(FOREGROUND_ID, createNotification());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "ServiceEnded", Toast.LENGTH_SHORT).show();
        mSnowView.destroy();
        super.onDestroy();
    }


    private Notification createNotification() {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return new Notification.Builder(this)
                .setContentTitle("Persistent Snow View")
                .setContentText("Content Text")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
    }
}
