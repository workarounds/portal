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
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by madki on 29/11/15.
 */
public abstract class OverlayPermissionHelper {
    public static final int OVERLAY_PERMISSION_REQUEST = 1001;

    protected Context context;
    protected MockActivity mockActivity;
    protected Intent queuedIntent = null;
    protected NotificationManager notificationManager;

    public OverlayPermissionHelper(MockActivity mockActivity) {
        this.context = mockActivity.getContext().getApplicationContext();
        this.mockActivity = mockActivity;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void promptForPermission(Intent queuedIntent) {
        this.queuedIntent = queuedIntent;
        notificationManager.notify(NotificationId.PERMISSION_NOTIFICATION_ID, promptNotification());
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST) {
            if (hasOverlayPermission(context)) {
                onPermissionApproved();
            } else {
                onPermissionDenied();
            }
            return true;
        }
        return false;
    }

    protected void onPermissionApproved() {
        notificationManager.notify(NotificationId.APPROVED_NOTFICATION_ID, approvedNotification());
    }

    protected void onPermissionDenied() {
        Portals.closeManager(context, getServiceClass());
        Toast.makeText(context, "Draw over apps permission denied", Toast.LENGTH_LONG).show();
    }


    @TargetApi(Build.VERSION_CODES.M)
    protected Notification promptNotification() {
        return preparePromptNotification(new NotificationCompat.Builder(context))
                .setContentIntent(promptNotificationClick(NotificationId.PENDING_DRAW_OVER_APPS))
                .setDeleteIntent(promptNotificationDelete(NotificationId.PENDING_DELETE_DRAW_OVER_APPS))
                .build();
    }

    protected NotificationCompat.Builder prepareNotification(NotificationCompat.Builder builder) {
        return builder.setAutoCancel(true)
                .setSmallIcon(getNotificationIcon())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0])
                .setColor(ContextCompat.getColor(context, getAccentColor()));
    }

    protected NotificationCompat.Builder preparePromptNotification(NotificationCompat.Builder builder) {
        return prepareNotification(builder)
                .setContentTitle(getAppName())
                .setContentText(context.getString(R.string.overlay_permission_notification));
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected Intent overlayPermissionActivity() {
        return new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName())
        );
    }

    protected Intent promptNotificationClick() {
        return Portals.startActivityForResultIntent(
                overlayPermissionActivity(),
                OVERLAY_PERMISSION_REQUEST,
                context,
                getServiceClass()
        );
    }

    protected PendingIntent promptNotificationClick(int notifId) {
        return PendingIntent.getService(
                context,
                notifId,
                promptNotificationClick(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    protected Intent promptNotificationDelete() {
        return Portals.closeManagerIntent(context, getServiceClass());
    }

    protected PendingIntent promptNotificationDelete(int notifId) {
        return PendingIntent.getService(
                context,
                notifId,
                promptNotificationDelete(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    protected Notification approvedNotification() {
        return prepareApprovedNotification(new NotificationCompat.Builder(context))
                .setContentIntent(approvedNotificationClick(NotificationId.PENDING_CLICK_APPROVED))
                .setDeleteIntent(approvedNotificationDelete(NotificationId.PENDING_DELETE_APPROVED))
                .build();
    }

    protected NotificationCompat.Builder prepareApprovedNotification(NotificationCompat.Builder builder) {
        return prepareNotification(builder)
                .setContentTitle(getAppName())
                .setContentText("Permission approved. Click to continue");
    }

    protected PendingIntent approvedNotificationClick(int notifId) {
        return PendingIntent.getService(
                context,
                notifId,
                queuedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    protected PendingIntent approvedNotificationDelete(int notifId) {
        return PendingIntent.getService(
                context,
                notifId,
                Portals.closeManagerIntent(context, getServiceClass()),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    protected Class<?> getServiceClass() {
        return mockActivity.getClass();
    }

    protected abstract String getAppName();
    @ColorRes
    protected abstract int getAccentColor();
    @DrawableRes
    protected abstract int getNotificationIcon();

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasOverlayPermission(Context context) {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                || Settings.canDrawOverlays(context);
    }
}
