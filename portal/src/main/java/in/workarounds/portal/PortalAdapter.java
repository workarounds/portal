package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by madki on 29/11/15.
 */
public abstract class PortalAdapter {
    protected Portal[] portals;
    protected Context context;

    public PortalAdapter(Context context) {
        this.context = context;
        portals = new Portal[getCount()];
    }

    public void open(int portalId) {

    }

    public void show(int portalId) {

    }

    public void hide(int portalId) {

    }

    public void close(int portalId) {

    }

    public void send(int portalId, Bundle bundle) {

    }

    public void closeManager() {

    }

    public void sendToAll(Bundle bundle) {

    }

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
