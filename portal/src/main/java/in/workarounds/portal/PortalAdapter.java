package in.workarounds.portal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes the intents received by {@link PortalService} and manages the Portals. Extend this to
 * instantiate your own CustomPortals. Think of it as a FragmentPagerAdapter
 *
 * @param <S> service that implements {@link IPermissionManager}
 */
public abstract class PortalAdapter<S extends Service & IPermissionManager> implements IntentResolver, MockActivity {
    /**
     * Tag fpr debugging
     */
    private static final String TAG = "PortalAdapter";

    /**
     * Helper class that decodes the intent and calls the correct method in the adapter based on the
     * data
     */
    private PortalCommands portalCommands;

    /**
     * List of portals that the adapter is managing
     */
    protected List<Portal> portals;

    /**
     * The service that actually receives intents and delegates to the adapter. It should implement
     * {@link IPermissionManager}, as the adapter would pass it a callback if it requires overlay
     * permission in Android M
     */
    protected S service;

    /**
     * The theme id to be set for all portals, pass -1 for no theme
     */
    private int themeId;

    /**
     * @param service service that receives the intents and implements {@link IPermissionManager}
     * @param themeId the theme to be set to all the portals, pass -1 for no theme
     */
    public PortalAdapter(S service, int themeId) {
        this.service = service;
        this.themeId = themeId;
        portalCommands = new PortalCommands(this);
        initPortals();
    }

    /**
     * Creates an empty list of {@link #portals} with count determined by {@link #getCount()}
     */
    protected void initPortals() {
        portals = new ArrayList<>(getCount());
        for (int i = 0; i < getCount(); i++) {
            portals.add(i, null);
        }
    }

    /**
     * Creates a portal and attaches it's view to window.
     * Creates the portal if it doesn't exist. If it does it just sends the data to
     * the portal and shows it.
     *
     * @param portalId id of the portal to be opened
     * @param data     to be sent to the portal
     * @see #createIfNull(int, Bundle)
     * @see #show(int)
     */
    public void open(int portalId, Bundle data) {
        if (!createIfNull(portalId, data)) {
            send(portalId, data);
        }
        show(portalId);
    }

    /**
     * Attaches Portal's view to window if not already attached.
     * In Android M if overlay permission is not given to the app (decided by
     * {@link OverlayPermissionHelper#hasOverlayPermission(Context)}) then
     * {@link IPermissionManager#promptForPermission(Intent)} is called.
     *
     * @param portalId the id of the portal to be shown
     */
    public void show(int portalId) {
        Portal portal = getPortal(portalId);
        if (portal == null || portal.getView() == null || portal.isViewAttached()) return;

        if (!OverlayPermissionHelper.hasOverlayPermission(getContext())) {
            service.promptForPermission(Portals.showIntent(portalId, getContext(), service.getClass()));
            return;
        }
        portal.attach();
    }

    /**
     * Detaches the Portal's view from window if it's attached.
     *
     * @param portalId id of the portal to be hidden
     */
    public void hide(int portalId) {
        Portal portal = getPortal(portalId);
        if (portal != null) portal.detach();
    }

    /**
     * Hides the portal and then sets it to null
     * Also terminates the service if all portals are closed. See {@link #checkForTermination()}
     *
     * @param portalId id of the portal to be closed
     */
    public void close(int portalId) {
        hide(portalId);
        Portal portal = getPortal(portalId);
        if (portal != null) {
            portal.onDestroy();
        }
        portals.set(portalId, null);
        checkForTermination();
    }

    /**
     * Sends the data to the portal if it's already created else does nothing
     *
     * @param portalId id of the portal to send the data to
     * @param data     data to be sent to the portal
     */
    public void send(int portalId, Bundle data) {
        Portal portal = getPortal(portalId);
        if (portal != null) portal.onData(data);
        else
            Log.w(TAG, "Unable to send data to portalId: " + portalId + ". The portal is not open");
        Log.i(TAG, "send: ");
    }

    /**
     * Closes all open portals and stops the {@link #service}
     */
    public void closeManager() {
        for (int i = 0; i < getCount(); i++) {
            close(i);
        }
        service.stopSelf();
        Log.i(TAG, "closeManager: ");
    }

    /**
     * Sends the data to all open Portals
     *
     * @param data data to be sent to the portals
     */
    public void sendToAll(Bundle data) {
        for (int i = 0; i < getCount(); i++) {
            send(i, data);
        }
        Log.i(TAG, "sendToAll: ");
    }

    /**
     * If all {@link #portals} are closed then terminates the {@link #service}
     */
    public void checkForTermination() {
        if (canTerminate()) service.stopSelf();
    }

    /**
     * A check to see if all portals are closed
     *
     * @return true if all {@link #portals} are closed
     */
    public boolean canTerminate() {
        boolean canTerminate = true;
        for (int i = 0; i < getCount(); i++) {
            if (getPortal(i) != null) canTerminate = false;
        }
        return canTerminate;
    }

    /**
     * Informs config changes to all portals. Called by the service in it's
     * {@link Service#onConfigurationChanged(Configuration)}. Delegates the config changes to all the
     * portals which actually handle the config changes
     *
     * @param newConfig the new configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        for (int i = 0; i < getCount(); i++) {
            Portal portal = getPortal(i);
            if (portal != null) {
                portal.onConfigurationChanged(newConfig);
            }
        }
    }

    /**
     * Helper method to start an activity, calls {@link Service#startActivity(Intent)}.
     *
     * @param intent the activity intent
     */
    @Override
    public void startActivity(Intent intent) {
        service.startActivity(intent);
    }

    /**
     * Helper method to start activity for result. Works only if the {@link #service} implements
     * {@link MockActivity} else throws {@link IllegalStateException}
     *
     * @param intent      intent of the activity to be started
     * @param requestCode requestCode of the request
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (service instanceof MockActivity) {
            ((MockActivity) service).startActivityForResult(intent, requestCode);
        } else {
            throw new IllegalStateException("Unable to startActivityForResult, PortalService doesn't implement MockActivity");
        }
    }

    /**
     * Informs all portals of the activity result that is received.
     *
     * @param requestCode requestCode of the request
     * @param resultCode  resultCode one of {@link MockActivity#RESULT_OK} or
     *                    {@link MockActivity#RESULT_CANCELLED} or {@link MockActivity#RESULT_DESTROYED}
     * @param result result sent by the activity
     * @return true if one of the portals handles the result
     */
    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent result) {
        Portal portal;
        boolean handled = false;
        for (int i = 0; i < getCount(); i++) {
            portal = getPortal(i);
            if (portal != null) {
                handled = handled || portal.onActivityResult(requestCode, resultCode, result);
            }
        }
        return handled;
    }

    /**
     * Should return the number of {@link #portals}
     * @return number of portals
     */
    public abstract int getCount();

    /**
     * Instantiate the the portal for given id.
     * @param portalId id of the portal
     * @return Portal object corresponding to the id
     */
    @NonNull
    protected abstract Portal createPortal(int portalId);

    /**
     * Helper method to get the portal at given id
     * @param portalId id of the portal
     * @return Portal at the given id if the id is valid else throws {@link IllegalArgumentException}
     */
    @Nullable
    public Portal getPortal(int portalId) {
        throwIfInvalidId(portalId);
        return portals.get(portalId);
    }

    /**
     * Helper method to set portal at the given id. Throws {@link IllegalArgumentException} if the
     * id is not valid
     * @param portalId id of the portal
     * @param portal portal to be set
     */
    protected void setPortal(int portalId, Portal portal) {
        throwIfInvalidId(portalId);
        portals.set(portalId, portal);
    }

    /**
     * Creates the portal if not already created. Returns true if the portal is created
     * @param portalId the id at which the portal needs to be set after creation
     * @param data data to be sent to portal
     * @return true if portal is created
     */
    protected boolean createIfNull(int portalId, Bundle data) {
        Portal portal = getPortal(portalId);
        if (portal == null) {
            portal = createPortal(portalId);
            portal.initThemedContext(getContext(), themeId);
            portal.onCreate(data);
            setPortal(portalId, portal);
            return true;
        }
        return false;
    }

    /**
     * Handles the intent received by the service and calls the corresponding adapter method.
     * @param intent intent to be handled
     * @return true if it's an intentType handled by the adapter
     */
    @Override
    public boolean handleCommand(@Nullable Intent intent) {
        return portalCommands.handleCommand(intent);
    }

    /**
     * Checks if the portalId is between 0 and {@link #getCount()}, throws
     * {@link IllegalArgumentException} if not valid id
     * @param portalId id to be validated
     */
    protected void throwIfInvalidId(int portalId) {
        if (portalId < 0 || portalId >= getCount()) {
            throw new IllegalArgumentException("Invalid portal Id: " + portalId);
        }
    }

    /**
     * A convenience method to get the context
     * @return {@link #service} (which is a Context)
     */
    public Context getContext() {
        return service;
    }

    /**
     * Returns the index of give portal object in {@link #portals}
     * @param portal the portal whose index is needed
     * @return portal index in {@link #portals}
     */
    public int indexOf(Portal portal) {
        return portals.indexOf(portal);
    }

}
