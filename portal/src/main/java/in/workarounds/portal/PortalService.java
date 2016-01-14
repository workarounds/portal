package in.workarounds.portal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Simple implementation of a service that manages the portals. Uses composition and delegates most
 * of the actual work to the helpers. Simply extend the class and provide your own helpers to
 * override behavior. In some cases it's not possible to extend this service if another service has
 * to be extended (Ex: while writing an AccessibilityService or InputMethodService), in such cases
 * simply instantiate the helpers yourself and implement the interfaces and delegate the methods to
 * helpers
 * @param <A> the custom implementation of {@link PortalAdapter}
 * @param <P> the custom implementation of {@link OverlayPermissionHelper}
 */
public abstract class PortalService<A extends PortalAdapter, P extends OverlayPermissionHelper> extends Service implements MockActivity, IPermissionManager {
    private static final String TAG = "PortalService";
    /**
     * Helper that actually handles {@link #startActivityForResult(Intent, int)} and parses the
     * intent and calls {@link #onActivityResult(int, int, Intent)} when it's a result from activity
     */
    protected MockActivityHelper mockActivityHelper;
    /**
     * The portalAdapter that actually manages the portal life cycles
     */
    protected A portalAdapter;
    /**
     * Permission helper that actually handles overlay permission
     */
    protected P permissionHelper;

    /**
     * Initializes the helpers
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mockActivityHelper = new MockActivityHelper(this);
        permissionHelper = createPermissionHelper();
        portalAdapter = createPortalAdapter();
    }

    /**
     * Passes the intents to each of the helpers and they handle the actual intents.
     * @param intent
     * @param flags
     * @param startId
     * @return {@link #START_STICKY} if intent is handled by helpers
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mockActivityHelper.handleCommand(intent) || portalAdapter.handleCommand(intent)) {
            return START_STICKY;
        } else {
            Log.w(TAG, "Unknown intentType in handleCommand");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * In android M when the permission for overlays is not granted to the app this method is called
     * by the adapter before attaching a {@link Portal}'s non null view to window. If a permission
     * helper is provided, then the service delegates the task to the helper
     * @param queuedIntent the intent that has to be re executed once permission is granted
     */
    @Override
    public void promptForPermission(Intent queuedIntent) {
        if(permissionHelper != null) permissionHelper.promptForPermission(queuedIntent);
    }

    /**
     * @return a new instance of the {@link #portalAdapter}
     */
    @NonNull
    protected abstract A createPortalAdapter();

    /**
     * @return a new instance of {@link #permissionHelper}, can be null
     */
    @Nullable
    protected abstract P createPermissionHelper();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Delegates handling of config changes to {@link #portalAdapter}
     * @param newConfig new configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        portalAdapter.onConfigurationChanged(newConfig);
    }

    /**
     * A background service cannot start a new activity directly, a new flag specifying that a new
     * task is being started has to be provided.
     * @param intent intent to start the activity
     */
    @Override
    public void startActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    /**
     * Delegates to {@link MockActivityHelper#startActivityForResult(Intent, int)}. If a permission
     * rationale is provided then that is shown as a toast if the activity for result is being
     * started for getting overlay permission.
     * @param intent      intent of the activity
     * @param requestCode request code of the request
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(permissionHelper != null &&
                requestCode == OverlayPermissionHelper.OVERLAY_PERMISSION_REQUEST) {
            Toast.makeText(this, permissionHelper.getPermissionRationale(), Toast.LENGTH_LONG).show();
        }
        mockActivityHelper.startActivityForResult(intent, requestCode);
    }

    /**
     * Delegates to {@link OverlayPermissionHelper#onActivityResult(int, int, Intent)} and
     * {@link PortalAdapter#onActivityResult(int, int, Intent)} in that order
     * @param requestCode request code of the request
     * @param resultCode  result code one of {@link #RESULT_OK}, {@link #RESULT_CANCELLED},
     *                    {@link #RESULT_DESTROYED}
     * @param data        data returned by the activity
     * @return true if result was handled
     */
    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (permissionHelper != null && permissionHelper.onActivityResult(requestCode, resultCode, data)) {
            return true;
        }
        if (portalAdapter.onActivityResult(requestCode, resultCode, data)) return true;

        Log.w(TAG, "Unhandled activity result");
        return false;
    }

    /**
     * Helper method to return context
     * @return context
     */
    @Override
    public Context getContext() {
        return this;
    }

}
