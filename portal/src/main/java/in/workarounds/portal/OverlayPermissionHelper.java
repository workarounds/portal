package in.workarounds.portal;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by madki on 29/11/15.
 */
public class OverlayPermissionHelper {
    public static final int OVERLAY_PERMISSION_REQUEST = 1001;

    private Context context;
    private MockActivity mockActivity;
    private Intent queuedIntent = null;
    private NotificationManager notificationManager;

    public OverlayPermissionHelper(MockActivity mockActivity) {
        this.context = mockActivity.getContext();
        this.mockActivity = mockActivity;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void promptForPermission(Intent queuedIntent) {
        this.queuedIntent = queuedIntent;
        notificationManager.notify(NotificationId.PERMISSION_NOTIFICATION_ID, promptNotification());
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == OVERLAY_PERMISSION_REQUEST) {
            if(resultCode == MockActivity.RESULT_OK) {
                onPermissionApproved();
            } else {
                onPermissionDenied();
            }
            return true;
        }
        return false;
    }

    protected void onPermissionApproved() {
        // TODO show notification with existing intent as pending intent
    }

    protected void onPermissionDenied() {
        // TODO show toast
    }


    @TargetApi(Build.VERSION_CODES.M)
    protected Notification promptNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setColor(ContextCompat.getColor(context, R.color.portal_permission_notification))
                        .setVibrate(new long[0]) //mandatory for high priority,setting no vibration
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.overlay_permission_notification));

        Intent activityIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));

        Intent clickIntent = Portals.startActivityForResultIntent(activityIntent, OVERLAY_PERMISSION_REQUEST, context, getServiceClass());

        PendingIntent resultPendingIntent =
                PendingIntent.getService(
                        context,
                        NotificationId.PENDING_DRAW_OVER_APPS,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        PendingIntent deleteIntent = PendingIntent.getService(
                context,
                NotificationId.PENDING_DELETE_DRAW_OVER_APPS,
                Portals.closeManagerIntent(context, getServiceClass()),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(resultPendingIntent);
        builder.setDeleteIntent(deleteIntent);
        return null;
    }

    protected Notification approvedNotification() {
        return null;
    }

    protected Class<?> getServiceClass() {
        return mockActivity.getClass();
    }

}
