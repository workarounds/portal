package in.workarounds.portal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by madki on 29/11/15.
 */
public abstract class PortalService<T extends PortalAdapter, P extends OverlayPermissionHelper> extends Service implements MockActivity, IPermissionManager {
    private static final String TAG = "PortalService";
    protected MockActivityHelper mockActivityHelper;
    protected T portalAdapter;
    protected P permissionHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mockActivityHelper = new MockActivityHelper(this);
        permissionHelper = createPermissionHelper();
        portalAdapter = createPortalAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mockActivityHelper.handleCommand(intent) || portalAdapter.handleCommand(intent)) {
            return START_STICKY;
        } else {
            Log.w(TAG, "Unknown intentType in handleCommand");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void promptForPermission(Intent queuedIntent) {
        if(permissionHelper != null) permissionHelper.promptForPermission(queuedIntent);
    }

    protected abstract T createPortalAdapter();

    protected abstract P createPermissionHelper();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        portalAdapter.onConfigurationChanged(newConfig);
    }

    @Override
    public void startActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(permissionHelper != null &&
                requestCode == OverlayPermissionHelper.OVERLAY_PERMISSION_REQUEST) {
            Toast.makeText(this, permissionHelper.getPermissionRationale(), Toast.LENGTH_LONG).show();
        }
        mockActivityHelper.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (permissionHelper != null && permissionHelper.onActivityResult(requestCode, resultCode, data)) {
            return true;
        }
        if (portalAdapter.onActivityResult(requestCode, resultCode, data)) return true;

        Log.w(TAG, "Unhandled activity result");
        return false;
    }

    @Override
    public Context getContext() {
        return this;
    }

}
