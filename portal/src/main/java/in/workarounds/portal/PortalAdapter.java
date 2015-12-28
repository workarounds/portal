package in.workarounds.portal;

import android.annotation.TargetApi;
import android.app.Service;
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
public abstract class PortalAdapter<S extends Service & IPermissionManager> implements IntentResolver, WrapperLayout.OnCloseDialogsListener {
    private static final String TAG = "PortalAdapter";
    private PortalCommands portalCommands;
    protected List<Portal> portals;
    protected int mainPortal;
    protected S service;

    public PortalAdapter(S service) {
        this.service = service;
        portalCommands = new PortalCommands(this);
        portals = new ArrayList<>(getCount());
    }


    public void open(int portalId, Bundle data) {
        Log.i(TAG, "open: ");
    }

    public void show(int portalId) {
        Log.i(TAG, "show: ");
    }

    public void hide(int portalId) {
        Log.i(TAG, "hide: ");
    }

    public void close(int portalId) {
        Log.i(TAG, "close: ");
    }

    public void send(int portalId, Bundle bundle) {
        Log.i(TAG, "send: ");
    }

    public void closeManager() {
        Log.i(TAG, "closeManager: ");
    }

    public void sendToAll(Bundle bundle) {
        Log.i(TAG, "sendToAll: ");
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent result) {
        // TODO call onActivityResult of all existing portals
        return false;
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

    @NonNull
    protected Portal getOrCreatePortal(int portalId, Bundle data) {
        Portal portal = getPortal(portalId);
        if (portal == null) {
            portal = createPortal(portalId);
            portal.onCreate(data);
            setPortal(portalId, portal);
        }
        return portal;
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

    @Override
    public void onCloseDialogs(@WrapperLayout.IReason int reason) {

    }

    public int indexOf(Portal portal) {
        return portals.indexOf(portal);
    }
}
