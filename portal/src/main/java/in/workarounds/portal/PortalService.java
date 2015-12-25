package in.workarounds.portal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import in.workarounds.portal.activity.MockActivity;
import in.workarounds.portal.activity.MockActivityHelper;
import in.workarounds.portal.adapter.PortalAdapter;
import in.workarounds.portal.permission.PermissionHelper;

/**
 * Created by madki on 29/11/15.
 */
public abstract class PortalService extends Service implements MockActivity {
    private static final String TAG = "PortalService";
    protected MockActivityHelper mockActivityHelper;
    protected PortalCommandHelper portalCommandHelper;
    protected PortalAdapter portalAdapter;
    protected PermissionHelper permissionHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mockActivityHelper = new MockActivityHelper(this);
        permissionHelper = new PermissionHelper(this);
        portalAdapter = createPortalAdapter();
        portalCommandHelper = new PortalCommandHelper(portalAdapter, permissionHelper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mockActivityHelper.handleCommand(intent) || portalCommandHelper.handleCommand(intent)) {
            return START_STICKY;
        } else {
            Log.w(TAG, "Unknown intentType in handleCommand");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected abstract PortalAdapter createPortalAdapter();

    @Override
    public void startActivity(Intent intent) {
        if(intent != null) intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mockActivityHelper.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!permissionHelper.onActivityResult(requestCode, resultCode, data)) {
            Log.w(TAG, "Unhandled activity result");
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}
