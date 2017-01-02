package me.tatocaster.snowview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by tatocaster on 1/2/17.
 */

public class Utils {

    /**
     * @param context
     * @param fragment
     * @param requestCode
     */
    public static void requestSystemAlertPermission(Activity context, Fragment fragment, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        final String packageName = context == null ? fragment.getActivity().getPackageName() : context.getPackageName();
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
        if (fragment != null)
            fragment.startActivityForResult(intent, requestCode);
        else
            context.startActivityForResult(intent, requestCode);
    }

    /**
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isSystemAlertPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    /**
     * @param context
     * @param serviceClass
     * @return
     */
    public static boolean isSnowOverlayingServiceIsRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param windowManager
     * @return
     */
    public static int getScreenWidth(WindowManager windowManager) {
        return getScreenMetrics(windowManager).widthPixels;
    }

    /**
     * @param windowManager
     * @return
     */
    public static int getScreenHeight(WindowManager windowManager) {
        return getScreenMetrics(windowManager).heightPixels;
    }

    /**
     * @param windowManager
     * @return
     */
    private static DisplayMetrics getScreenMetrics(WindowManager windowManager) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }
}
