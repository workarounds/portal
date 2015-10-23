package in.workarounds.portal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.workarounds.portal.WrapperLayout.Reason;

/**
 * Created by madki on 16/09/15.
 */
public class PortalManager extends Service implements WrapperLayout.OnCloseDialogsListener{
    private static final String TAG = "PortalManager";

    protected static final String INTENT_KEY_INTENT_TYPE = "intent_key_intent_type";
    protected static final int INTENT_TYPE_NO_TYPE = 0;
    protected static final int INTENT_TYPE_OPEN_PORTAL   = 1;
    protected static final int INTENT_TYPE_CLOSE_PORTAL  = 2;
    protected static final int INTENT_TYPE_HIDE_PORTAL   = 3;
    protected static final int INTENT_TYPE_SHOW_PORTAL   = 4;
    protected static final int INTENT_TYPE_PORTAL_DATA   = 5;

    protected static final int INTENT_TYPE_OPEN_PORTLET  = 11;
    protected static final int INTENT_TYPE_CLOSE_PORTLET = 12;
    protected static final int INTENT_TYPE_HIDE_PORTLET  = 13;
    protected static final int INTENT_TYPE_SHOW_PORTLET  = 14;
    protected static final int INTENT_TYPE_PORTLET_DATA  = 15;

    protected static final int INTENT_TYPE_CLOSE_MANAGER = 100;
    protected static final int INTENT_TYPE_DATA_TO_ALL   = 101;

    protected static final String INTENT_KEY_CLASS = "intent_key_class";
    protected static final String INTENT_KEY_DATA = "intent_key_data";

    protected static final String INTENT_KEY_PORTLET_ID     = "intent_key_portlet_id";

    protected Portal mPortal;
    protected HashMap<Integer, Portlet> mPortlets;
    protected WindowManager mWindowManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPortlets = new HashMap<>();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            int intentType = intent.getIntExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_NO_TYPE);
            resolveIntent(intentType, intent);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closePortal();
        List<Integer> keys = new ArrayList<>();
        keys.addAll(mPortlets.keySet());
        for (int i : keys) {
            closePortlet(i);
        }
    }

    protected void resolveIntent(int intentType, @NonNull Intent intent) {
        switch (intentType) {
            case INTENT_TYPE_OPEN_PORTAL:
                openPortal(intent);
                break;
            case INTENT_TYPE_HIDE_PORTAL:
                hidePortal();
                break;
            case INTENT_TYPE_SHOW_PORTAL:
                showPortal();
                break;
            case INTENT_TYPE_CLOSE_PORTAL:
                closePortal();
                checkForTermination();
                break;
            case INTENT_TYPE_OPEN_PORTLET:
                openPortlet(intent);
                break;
            case INTENT_TYPE_HIDE_PORTLET:
                hidePortlet(intent.getIntExtra(INTENT_KEY_PORTLET_ID, -1));
                break;
            case INTENT_TYPE_SHOW_PORTLET:
                showPortlet(intent.getIntExtra(INTENT_KEY_PORTLET_ID, -1));
                break;
            case INTENT_TYPE_CLOSE_PORTLET:
                closePortlet(intent.getIntExtra(INTENT_KEY_PORTLET_ID, -1));
                checkForTermination();
                break;
            case INTENT_TYPE_CLOSE_MANAGER:
                closeManager();
                break;
            case INTENT_TYPE_PORTAL_DATA:
                sendDataToPortal(intent);
                break;
            case INTENT_TYPE_PORTLET_DATA:
                sendDataToPortlet(intent, intent.getIntExtra(INTENT_KEY_PORTLET_ID, -1));
                break;
            case INTENT_TYPE_DATA_TO_ALL:
                sendData(intent);
                break;
            case INTENT_TYPE_NO_TYPE:
                break;
        }
    }

    protected void openPortal(Intent intent) {
        openPortal(createPortal(intent));
    }

    protected void openPortal(Portal portal) {
        if(portal != null) {
            closePortal();
            mPortal = portal;
            showPortal();
        }
    }

    protected void openPortlet(Intent intent) {
        openPortlet(createPortlet(intent));
    }

    protected void openPortlet(Portlet portlet) {
        if(portlet != null && Portlet.isValidID(portlet.getId())) {
            closePortlet(portlet.getId());
            addPortlet(portlet);
            showPortlet(portlet.getId());
        }
    }

    @Nullable
    protected Portal createPortal(Intent intent) {
        Portal portal = constructPortal(intent.getStringExtra(INTENT_KEY_CLASS));
        if(portal != null) {
            portal.onCreate(intent.getBundleExtra(INTENT_KEY_DATA));
            portal.setPortalManager(this);
        } else {
            Log.e(TAG, "Problem creating portal");
        }
        return portal;
    }

    @Nullable
    protected Portlet createPortlet(Intent intent) {
        int portletId = intent.getIntExtra(INTENT_KEY_PORTLET_ID, -1);
        Portlet portlet = null;
        if(portletId != -1) {
            portlet = constructPortlet(intent.getStringExtra(INTENT_KEY_CLASS), portletId);
            if(portlet != null) {
                portlet.onCreate(intent.getBundleExtra(INTENT_KEY_DATA));
                portlet.setPortalManager(this);
            }
        }
        return portlet;
    }

    protected void closePortal() {
        hidePortal();
        if(mPortal != null) {
            mPortal.onDestroy();
            mPortal = null;
        }
    }

    protected void closePortlet(int portletId) {
        closePortlet(getPortlet(portletId));
    }

    protected void closePortlet(Portlet portlet) {
        hidePortlet(portlet);
        if(portlet != null) {
            portlet.onDestroy();
            removePortlet(portlet.getId());
        }
    }

    protected void showPortal() {
        if(mPortal != null && mPortal.getState() != AbstractPortal.STATE_ACTIVE) {
            attachToWindow(mPortal.getView(), mPortal.getLayoutParams());
            // TODO add animation
            mPortal.addOnCloseDialogsListener(this);
            mPortal.onResume();
        }
    }

    protected void showPortlet(int portletId) {
        showPortlet(getPortlet(portletId));
    }

    protected void showPortlet(Portlet portlet) {
        if(portlet != null && portlet.getState() != AbstractPortal.STATE_ACTIVE) {
            attachToWindow(portlet.getView(), portlet.getLayoutParams());
            portlet.onResume();
        }
    }

    protected void hidePortal() {
        if(mPortal != null && mPortal.getState() == AbstractPortal.STATE_ACTIVE) {
            // TODO add animation
            mPortal.onPause();
            mPortal.removeOnCloseDialogsListener(this);
            detachFromWindow(mPortal.getView());
        }
    }

    protected void hidePortlet(int portletId) {
        hidePortlet(getPortlet(portletId));

    }

    protected void hidePortlet(Portlet portlet) {
        if(portlet != null && portlet.getState() == AbstractPortal.STATE_ACTIVE) {
            portlet.onPause();
            detachFromWindow(portlet.getView());
        }
    }

    protected void sendData(Intent intent) {
        if(mPortal != null){
            sendDataToPortal(intent);
        }
        for (int id: mPortlets.keySet()) {
            sendDataToPortlet(intent, id);
        }
    }

    protected void sendDataToPortal(Intent intent) {
        if(mPortal != null) {
            mPortal.onData(intent.getBundleExtra(INTENT_KEY_DATA));
        } else {
            openPortal(intent);
        }
    }

    protected void sendDataToPortlet(Intent intent, int id) {
        Portlet portlet = getPortlet(id);
        if(portlet != null) {
            portlet.onData(intent.getBundleExtra(INTENT_KEY_DATA));
        } else {
            openPortlet(intent);
        }
    }

    protected void closeManager() {
        stopSelf();
    }

    @Nullable
    public Portal getPortal() {
        return mPortal;
    }

    @Nullable
    public Portlet getPortlet(int portletId) {
        return mPortlets.get(portletId);
    }

    protected void addPortlet(Portlet portlet) {
        if(Portlet.isValidID(portlet.getId())) {
            mPortlets.put(portlet.getId(), portlet);
        }
    }

    protected void removePortlet(int portletId) {
        if(mPortlets.containsKey(portletId)) {
            mPortlets.remove(portletId);
        }
    }

    protected void attachToWindow(View view, WindowManager.LayoutParams params) {
        mWindowManager.addView(view, params);
    }

    protected void detachFromWindow(View view) {
        mWindowManager.removeView(view);
    }

    @Nullable
    protected Class<?> getTypeFromName(String className) {
        Class<?> type = null;
        if(!TextUtils.isEmpty(className)) {
            try {
                type = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Class name is empty");
        }
        return type;
    }

    @Nullable
    protected Constructor<?> getPortalConstructor(Class<?> type) {
        Constructor<?> constructor = null;
        if(type != null) {
            try {
                constructor = type.getConstructor(Context.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Type is null, unable to get constructor");
        }
        return constructor;
    }

    @Nullable
    protected Constructor<?> getPortletConstructor(Class<?> type) {
        Constructor<?> constructor = null;
        if(type != null) {
            try {
                constructor = type.getConstructor(Context.class, int.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Type is null, unable to get constructor");
        }
        return constructor;
    }

    @Nullable
    protected Portal constructPortal(Constructor<?> constructor) {
        Portal portal = null;
        if(constructor != null) {
            try {
                portal = (Portal) constructor.newInstance(this);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Constructor is null, unable to create portal");
        }
        return portal;
    }

    @Nullable
    protected Portlet constructPortlet(Constructor<?> constructor, int portletId) {
        Portlet portlet = null;
        if(constructor != null) {
            try {
                portlet = (Portlet) constructor.newInstance(this, portletId);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Constructor is null, unable to create portal");
        }
        return portlet;
    }

    @Nullable
    protected Portal constructPortal(String className) {
        return constructPortal(getPortalConstructor(getTypeFromName(className)));
    }

    @Nullable
    protected Portlet constructPortlet(String className, int portletId) {
        return constructPortlet(getPortletConstructor(getTypeFromName(className)), portletId);
    }

    @Override
    public void onCloseDialogs(Reason reason) {
        switch (reason) {
            case KEY_BACK:
                if(mPortal != null) {
                    mPortal.onBackPressed();
                }
                break;
            case KEY_HOME:
            case KEY_RECENT_APPS:
            case KEY_UNKNOWN:
                hidePortal();
                break;
        }
    }

    public static void close(Context context) {
        close(context, PortalManager.class);
    }

    public static <S extends PortalManager> void close(Context context, Class<S> type) {
        Intent intent = new Intent(context, type);
        intent.putExtra(PortalManager.INTENT_KEY_INTENT_TYPE, PortalManager.INTENT_TYPE_CLOSE_MANAGER);
        context.startService(intent);
    }

    public static void send(Context context, Bundle data) {
        send(context, data, PortalManager.class);
    }

    public static <S extends PortalManager> void send(Context context, Bundle data, Class<S> type) {
        Intent intent = new Intent(context, type);
        intent.putExtra(PortalManager.INTENT_KEY_INTENT_TYPE, PortalManager.INTENT_TYPE_DATA_TO_ALL);
        intent.putExtra(PortalManager.INTENT_KEY_DATA, data);
        context.startService(intent);
    }

    private void checkForTermination() {
        if(mPortal == null && mPortlets.isEmpty()) {
            stopSelf();
        }
    }

    @IntDef({INTENT_TYPE_NO_TYPE, INTENT_TYPE_OPEN_PORTAL, INTENT_TYPE_SHOW_PORTAL,
            INTENT_TYPE_HIDE_PORTAL, INTENT_TYPE_CLOSE_PORTAL, INTENT_TYPE_OPEN_PORTLET,
            INTENT_TYPE_SHOW_PORTLET, INTENT_TYPE_HIDE_PORTLET, INTENT_TYPE_CLOSE_PORTLET,
            INTENT_TYPE_CLOSE_MANAGER, INTENT_TYPE_PORTLET_DATA, INTENT_TYPE_PORTAL_DATA, INTENT_TYPE_DATA_TO_ALL
    })
    public @interface PM_INTENT_ID {
    }
}
