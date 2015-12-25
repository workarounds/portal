package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by madki on 10/12/15.
 */
public class Portals {

    protected Portals() {
        throw new AssertionError("No instantiation");
    }

    private static Intent getIntent(Context context, Class<?> serviceClass) {
        return new Intent(context, serviceClass);
    }

    public static Intent openIntent(Context context, Class<?> serviceClass, int portalId) {
        return getIntent(context, serviceClass).putExtras(PortalCommandHelper.open(portalId));
    }

    public static void open(Context context, Class<?> serviceClass, int portalId) {
        context.startService(openIntent(context, serviceClass, portalId));
    }

    public static Intent showIntent(Context context, Class<?> serviceClass, int portalId) {
        return getIntent(context, serviceClass).putExtras(PortalCommandHelper.show(portalId));
    }

    public static void show(Context context, Class<?> serviceClass, int portalId) {
        context.startService(showIntent(context, serviceClass, portalId));
    }

    public static Intent hideIntent(Context context, Class<?> serviceClass, int portalId) {
        return getIntent(context, serviceClass).putExtras(PortalCommandHelper.hide(portalId));
    }

    public static void hide(Context context, Class<?> serviceClass, int portalId) {
        context.startService(hideIntent(context, serviceClass, portalId));
    }

    public static Intent closeIntent(Context context, Class<?> serviceClass, int portalId) {
        return getIntent(context, serviceClass).putExtras(PortalCommandHelper.close(portalId));
    }

    public static void close(Context context, Class<?> serviceClass, int portalId) {
        context.startService(closeIntent(context, serviceClass, portalId));
    }

    public static Intent sendIntent(Context context, Class<?> serviceClass, int portalId, Bundle data) {
        return getIntent(context, serviceClass).putExtras(PortalCommandHelper.send(portalId, data));
    }

    public static void send(Context context, Class<?> serviceClass, int portalId, Bundle data) {
        context.startService(sendIntent(context, serviceClass, portalId, data));
    }

    public static Intent closeManagerIntent(Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommandHelper.closeManager());
    }

    public static void closeManager(Context context, Class<?> serviceClass) {
        context.startService(closeManagerIntent(context, serviceClass));
    }

    public static Intent sendToAllIntent(Context context, Class<?> serviceClass, @NonNull Bundle data) {
        return getIntent(context, serviceClass).putExtras(PortalCommandHelper.sendToAll(data));
    }

    public static void sendToAll(Context context, Class<?> serviceClass, @NonNull Bundle data) {
        context.startService(sendToAllIntent(context, serviceClass, data));
    }

    public static Intent startActivityForResultIntent(Context context, Class<?> serviceClass, Intent activityIntent, int requestCode) {
        return getIntent(context, serviceClass)
                .putExtras(CommandHelper.startActivityForResult(activityIntent, requestCode));
    }
}
