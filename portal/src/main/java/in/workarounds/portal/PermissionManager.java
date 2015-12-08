package in.workarounds.portal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by madki on 29/11/15.
 */
public class PermissionManager implements BasePermissionManager {
    private Context context;
    private ActivityHelper activityHelper;

    public PermissionManager(Context context, ActivityHelper activityHelper) {
        this.context = context;
        this.activityHelper = activityHelper;
    }

    @Override
    public void promptForPermission() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setColor(ContextCompat.getColor(context, R.color.portal_permission_notification))
                        .setVibrate(new long[0]) //mandatory for high priority,setting no vibration
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.overlay_permission_notification));

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));

        // TODO
        Intent resultIntent = new Intent();

        PendingIntent resultPendingIntent =
                PendingIntent.getService(
                        context,
                        NotificationId.PENDING_DRAW_OVER_APPS,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        PendingIntent deleteIntent = PendingIntent.getService(
                context,
                NotificationId.PENDING_DELETE_DRAW_OVER_APPS,
                new Intent(), // TODO
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(resultPendingIntent);
//        builder.setDeleteIntent(deleteIntent);
//        notificationManager.notify(NotificationId.PERMISSION_NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onPermissionApproved() {
        // TODO show notification with existing intent as pending intent
    }

    @Override
    public void onPermissionDenied() {
        // TODO show toast
    }
}
