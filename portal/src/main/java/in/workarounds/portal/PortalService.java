package in.workarounds.portal;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by madki on 29/11/15.
 */
public abstract class PortalService extends Service implements ActivityResultListener {
    private static final String TAG = "PortalService";
    protected ActivityHelper<PortalService> activityHelper;
    protected IntentResolver intentResolver;
    protected PortalAdapter portalAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        activityHelper = new ActivityHelper<>(this);
        portalAdapter = createPortalAdapter();
        intentResolver = new IntentResolver(portalAdapter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(activityHelper.onStartCommand(intent) || intentResolver.onStartCommand(intent)) {
            return START_STICKY;
        } else {
            Log.w(TAG, "Unknown intentType in onStartCommand");
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
        activityHelper.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "Unhandled activity result");
    }
}
