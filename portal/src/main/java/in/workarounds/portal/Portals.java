package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Util class to create intents and start {@link PortalService}.
 */
public class Portals {

    protected Portals() {
        throw new AssertionError("No instantiation");
    }

    static Intent getIntent(Context context, Class<?> serviceClass) {
        return new Intent(context, serviceClass);
    }

    /**
     * @param portalId id of the portal which is to be opened
     * @param data data to be sent to the portal
     * @param context context
     * @param serviceClass the class of the service
     * @return intent to open the given portal id
     */
    public static Intent openIntent(int portalId, Bundle data, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.open(portalId, data));
    }

    /**
     * Starts the given service with an intent to open the portal with given id and pass the given
     * data
     * @param portalId id of the portal to be opened
     * @param data data to be passed to the portal
     * @param context context
     * @param serviceClass the class of the service
     */
    public static void open(int portalId, Bundle data, Context context, Class<?> serviceClass) {
        context.startService(openIntent(portalId, data, context, serviceClass));
    }

    /**
     * @param portalId id of the portal to be showed
     * @param context context
     * @param serviceClass the class of the service
     * @return intent to show the portal with given id
     */
    public static Intent showIntent(int portalId, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.show(portalId));
    }

    /**
     * Starts the given service with an intent to show the portal with given id
     * @param portalId id of the portal to be showed
     * @param context context
     * @param serviceClass the class of the service
     */
    public static void show(int portalId, Context context, Class<?> serviceClass) {
        context.startService(showIntent(portalId, context, serviceClass));
    }

    /**
     * @param portalId id of the portal to be hidden
     * @param context context
     * @param serviceClass the class of the service
     * @return intent to hide the portal with given id
     */
    public static Intent hideIntent(int portalId, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.hide(portalId));
    }

    /**
     * Starts the given service with an intent to hide the portal with given id
     * @param portalId id of the portal to be hidden
     * @param context context
     * @param serviceClass the class of the service
     */
    public static void hide(int portalId, Context context, Class<?> serviceClass) {
        context.startService(hideIntent(portalId, context, serviceClass));
    }

    /**
     * @param portalId id of the portal to be closed
     * @param context context
     * @param serviceClass the class of the service
     * @return intent to close the portal with given id
     */
    public static Intent closeIntent(int portalId, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.close(portalId));
    }

    /**
     * Starts the given service with an intent to close the portal with given id
     * @param portalId id of the portal to be closed
     * @param context context
     * @param serviceClass the class of the service
     */
    public static void close(int portalId, Context context, Class<?> serviceClass) {
        context.startService(closeIntent(portalId, context, serviceClass));
    }

    /**
     * @param portalId id of the portal to send the data to
     * @param data data to be sent
     * @param context context
     * @param serviceClass the class of the service
     * @return intent to send data to the portal
     */
    public static Intent sendIntent(int portalId, Bundle data, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.send(portalId, data));
    }

    /**
     * Starts the service to send data to the portal with given id
     * @param portalId id of the portal to send the data to
     * @param data data to be sent
     * @param context context
     * @param serviceClass the class of the service
     */
    public static void send(int portalId, Bundle data, Context context, Class<?> serviceClass) {
        context.startService(sendIntent(portalId, data, context, serviceClass));
    }

    /**
     * @param context context
     * @param serviceClass the class of the service
     * @return Intent to close all portals and stop the service
     */
    public static Intent closeManagerIntent(Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.closeManager());
    }

    /**
     * Starts the service with an intent to close all portals and stop itself
     * @param context context
     * @param serviceClass the class of the service
     */
    public static void closeManager(Context context, Class<?> serviceClass) {
        context.startService(closeManagerIntent(context, serviceClass));
    }

    /**
     * @param data data to be sent
     * @param context context
     * @param serviceClass the class of the service
     * @return intent to send data to all the portals
     */
    public static Intent sendToAllIntent(@NonNull Bundle data, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass).putExtras(PortalCommands.sendToAll(data));
    }

    /**
     * Starts the service to send data to all the portals
     * @param data data to be sent
     * @param context context
     * @param serviceClass the class of the service
     */
    public static void sendToAll(@NonNull Bundle data, Context context, Class<?> serviceClass) {
        context.startService(sendToAllIntent(data, context, serviceClass));
    }

    /**
     * @param activityIntent the intent of the activity to be started
     * @param requestCode request code of the request
     * @param context context
     * @param serviceClass the class of the service
     * @return the intent to start the service to start an activity for result
     */
    public static Intent startActivityForResultIntent(Intent activityIntent, int requestCode, Context context, Class<?> serviceClass) {
        return getIntent(context, serviceClass)
                .putExtras(MockActivityHelper.startActivityForResultBundle(activityIntent, requestCode));
    }
}
