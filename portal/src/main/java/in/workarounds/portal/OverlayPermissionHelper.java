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
 * An implementation of the overlay permission helper.
 * <h3>What it does</h3>
 * <p>
 * When on Android M the user hasn't yet granted the
 * overlay permission and your app tries to show a portal this helper shows the user a notification
 * (use #preparePromptNotification(NotificationCompat.Builder) to set it's title and content)
 * If the user dismisses the notification the service and the portals are killed saying permission
 * denied (override the behavior by overriding {@link #onPermissionDenied()}), if he clicks on it he
 * is sent to the settings page where he's shown a toggle to grant overlay permission. At this point
 * the message returned by {@link #getPermissionRationale()} is shown as toast to the user. If the
 * user grants permission {@link #onPermissionApproved()} is called, he's shown another notification
 * clicking on which will execute the queued intent and shows the portal which triggered the
 * permission requirement.
 * </p>
 * <h3>How to override it</h3>
 * <p>
 * All methods are public or protected, so the class can be overridden to change the entire behavior.
 * The flexibility is given as there is not one best way to deal with lack of permission. If the
 * above flow seems fine to you then override all the abstract methods and additionally
 * {@link #getPromptNotifContent()} and {@link #getApprovedNotifContent()}.
 * But if you want to change the flow override {@link #promptForPermission(Intent)},
 * {@link #onPermissionApproved()} and {@link #onPermissionDenied()}.
 * </p>
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
        Toast.makeText(context, "Draw over other apps permission denied", Toast.LENGTH_LONG).show();
    }


    @TargetApi(Build.VERSION_CODES.M)
    protected Notification promptNotification() {
        return preparePromptNotification(new NotificationCompat.Builder(context))
                .setContentIntent(promptNotificationClick(NotificationId.PENDING_DRAW_OVER_APPS))
                .setDeleteIntent(promptNotificationDelete(NotificationId.PENDING_DELETE_DRAW_OVER_APPS))
                .build();
    }

    /**
     * Applies style and priority settings to all notifications
     * @param builder notification builder
     * @return notification builder
     */
    protected NotificationCompat.Builder prepareNotification(NotificationCompat.Builder builder) {
        return builder.setAutoCancel(true)
                .setSmallIcon(getNotificationIcon())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0])
                .setColor(ContextCompat.getColor(context, getAccentColor()));
    }

    /**
     * Applies style and sets title and content for prompt notification
     * @param builder notification builder
     * @return notification builder
     */
    protected NotificationCompat.Builder preparePromptNotification(NotificationCompat.Builder builder) {
        return prepareNotification(builder)
                .setContentTitle(getAppName())
                .setContentText(getPromptNotifContent());
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
                .setContentText(getApprovedNotifContent());
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

    /**
     * @return Notification text which is shown when the user is prompted for overlay permission
     */
    protected String getPromptNotifContent() {
       return "Draw over other apps permission required.";
    }

    /**
     * @return Notification text which is shown once the user approves overlay permission
     */
    protected String getApprovedNotifContent() {
       return "Permission approved. Click to continue";
    }

    /**
     * The toast that should be shown when the user is navigated to the overlay permission setting
     * @return string permission rationale
     */
    public String getPermissionRationale() {
        return "The application " + getAppName() + " needs 'Draw over other apps' permission to work.";
    }
}
