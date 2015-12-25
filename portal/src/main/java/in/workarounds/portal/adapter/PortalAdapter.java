package in.workarounds.portal.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import in.workarounds.portal.Portal;

/**
 * Created by madki on 29/11/15.
 */
public abstract class PortalAdapter implements BasePortalAdapter {
    protected Portal[] portals;
    protected Context context;

    public PortalAdapter(Context context) {
        this.context = context;
        portals = new Portal[getCount()];
    }

    @Override
    public void open(int portalId) {

    }

    @Override
    public void show(int portalId) {

    }

    @Override
    public void hide(int portalId) {

    }

    @Override
    public void close(int portalId) {

    }

    @Override
    public void send(int portalId, Bundle bundle) {

    }

    @Override
    public void closeManager() {

    }

    @Override
    public void sendToAll(Bundle bundle) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {

    }

    public abstract int getCount();
    public abstract Portal createPortal(int portalId);

    public Portal getPortal(int portalId) {
        if(portals[portalId] == null) {
            portals[portalId] = createPortal(portalId);
        }
        return portals[portalId];
    }

}
