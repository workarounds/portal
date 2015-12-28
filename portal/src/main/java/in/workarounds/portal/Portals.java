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

    public static Intent openIntent(int portalId, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.open(portalId));
    }

    public static void open(int portalId, Context context, Class<?> serviceClass) {
        context.startService(openIntent(portalId, context, serviceClass));
    }

    public static Intent showIntent(int portalId, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.show(portalId));
    }

    public static void show(int portalId, Context context, Class<?> serviceClass) {
        context.startService(showIntent(portalId, context, serviceClass));
    }

    public static Intent hideIntent(int portalId, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.hide(portalId));
    }

    public static void hide(int portalId, Context context, Class<?> serviceClass) {
        context.startService(hideIntent(portalId, context, serviceClass));
    }

    public static Intent closeIntent(int portalId, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.close(portalId));
    }

    public static void close(int portalId, Context context, Class<?> serviceClass) {
        context.startService(closeIntent(portalId, context, serviceClass));
    }

    public static Intent sendIntent(int portalId, Bundle data, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.send(portalId, data));
    }

    public static void send(int portalId, Bundle data, Context context, Class<?> serviceClass) {
        context.startService(sendIntent(portalId, data, context, serviceClass));
    }

    public static Intent closeManagerIntent(Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.closeManager());
    }

    public static void closeManager(Context context, Class<?> serviceClass) {
        context.startService(closeManagerIntent(context, serviceClass));
    }

    public static Intent sendToAllIntent(@NonNull Bundle data, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.sendToAll(data));
    }

    public static void sendToAll(@NonNull Bundle data, Context context, Class<?> serviceClass) {
        context.startService(sendToAllIntent(data, context, serviceClass));
    }

    public static Intent startActivityForResultIntent(Intent activityIntent, int requestCode, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass)
                .putExtras(MockActivityHelper.startActivityForResultBundle(activityIntent, requestCode));
    }
}
