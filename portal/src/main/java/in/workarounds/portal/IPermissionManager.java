package in.workarounds.portal;

import android.content.Intent;

/**
 * Created by madki on 29/11/15.
 */
public interface IPermissionManager {
    void promptForPermission(Intent queuedIntent);
}
