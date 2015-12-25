package in.workarounds.portal.adapter;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by madki on 28/11/15.
 */
public interface BasePortalAdapter {
    void open(int portalId);
    void show(int portalId);
    void hide(int portalId);
    void close(int portalId);
    void send(int portalId, Bundle bundle);

    void closeManager();
    void sendToAll(Bundle bundle);

    void onActivityResult(int requestCode, int resultCode, Intent result);
}
