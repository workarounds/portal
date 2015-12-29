package in.workarounds.portal;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madki on 29/11/15.
 */
public abstract class PortalAdapter<S extends Service & IPermissionManager> implements IntentResolver, MockActivity {
    private static final String TAG = "PortalAdapter";
    private PortalCommands portalCommands;
    protected List<Portal> portals;
    protected S service;

    public PortalAdapter(S service) {
        this.service = service;
        portalCommands = new PortalCommands(this);
        initPortals();
    }

    protected void initPortals() {
        portals = new ArrayList<>(getCount());
        for (int i=0; i < getCount(); i++) {
            portals.add(i, null);
        }
    }

    public void open(int portalId, Bundle data) {
        if (!createIfNull(portalId, data)) {
            send(portalId, data);
        }
        show(portalId);
        Log.i(TAG, "open: ");
    }

    public void show(int portalId) {
        Portal portal = getPortal(portalId);
        if (portal == null || portal.getView() == null || portal.isViewAttached()) return;

        if (!hasOverlayPermission()) {
            service.promptForPermission(Portals.showIntent(portalId, getContext(), service.getClass()));
            return;
        }
        portal.attach();
        Log.i(TAG, "show: ");
    }

    public void hide(int portalId) {
        Portal portal = getPortal(portalId);
        if (portal != null) portal.detach();
        Log.i(TAG, "hide: ");
    }

    public void close(int portalId) {
        hide(portalId);
        Portal portal = getPortal(portalId);
        if (portal != null) {
            portal.onDestroy();
        }
        portals.set(portalId, null);
        checkForTemination();
        Log.i(TAG, "close: ");
    }

    public void send(int portalId, Bundle data) {
        Portal portal = getPortal(portalId);
        if (portal != null) portal.onData(data);
        else
            Log.w(TAG, "Unable to send data to portalId: " + portalId + ". The portal is not open");
        Log.i(TAG, "send: ");
    }

    public void closeManager() {
        for (int i=0; i < getCount(); i++) {
            close(i);
        }
        service.stopSelf();
        Log.i(TAG, "closeManager: ");
    }

    public void sendToAll(Bundle data) {
        for (int i = 0; i < getCount(); i++) {
            send(i, data);
        }
        Log.i(TAG, "sendToAll: ");
    }

    public void checkForTemination() {
        if(canTerminate()) service.stopSelf();
    }

    public boolean canTerminate() {
        boolean canTerminate = true;
        for(int i=0; i < getCount(); i++) {
            if(getPortal(i) != null) canTerminate = false;
        }
        return canTerminate;
    }

    @Override
    public void startActivity(Intent intent) {
        service.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (service instanceof MockActivity) {
            ((MockActivity) service).startActivityForResult(intent, requestCode);
        } else {
            throw new IllegalStateException("Unable to startActivityForResult, PortalService doesn't implement MockActivity");
        }
    }

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

    public abstract int getCount();

    @NonNull
    protected abstract Portal createPortal(int portalId);

    @Nullable
    public Portal getPortal(int portalId) {
        throwIfInvalidId(portalId);
        return portals.get(portalId);
    }

    protected void setPortal(int portalId, Portal portal) {
        throwIfInvalidId(portalId);
        portals.set(portalId, portal);
    }

    protected boolean createIfNull(int portalId, Bundle data) {
        Portal portal = getPortal(portalId);
        if (portal == null) {
            portal = createPortal(portalId);
            portal.onCreate(data);
            setPortal(portalId, portal);
            return true;
        }
        return false;
    }


    @Override
    public boolean handleCommand(@Nullable Intent intent) {
        Log.i(TAG, "handleCommand: ");
        return portalCommands.handleCommand(intent);
    }

    protected void throwIfInvalidId(int portalId) {
        if (portalId < 0 || portalId >= getCount()) {
            throw new IllegalArgumentException("Invalid portal Id: " + portalId);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected boolean hasOverlayPermission() {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                || Settings.canDrawOverlays(service);
    }

   public Context getContext() {
        return service;
    }

    public int indexOf(Portal portal) {
        return portals.indexOf(portal);
    }

}
