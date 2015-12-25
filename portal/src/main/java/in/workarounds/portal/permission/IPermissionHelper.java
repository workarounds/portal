package in.workarounds.portal.permission;

import android.content.Intent;

/**
 * Created by madki on 29/11/15.
 */
public interface IPermissionHelper {
    boolean requiresPermissionPrompt();
    void promptForPermission(Intent queuedIntent);
    boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
