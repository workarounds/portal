package in.workarounds.portal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
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

    protected static final int INTENT_TYPE_OPEN_PORTLET  = 11;
    protected static final int INTENT_TYPE_CLOSE_PORTLET = 12;
    protected static final int INTENT_TYPE_HIDE_PORTLET  = 13;
    protected static final int INTENT_TYPE_SHOW_PORTLET  = 14;

    protected static final int INTENT_TYPE_CLOSE_MANAGER = 100;

    protected static final String INTENT_KEY_PORTAL_CLASS = "intent_key_portal_class";
    protected static final String INTENT_KEY_PORTAL_DATA  = "intent_key_portal_data";

    protected static final String INTENT_KEY_PORTLET_CLASS  = "intent_key_portlet_class";
    protected static final String INTENT_KEY_PORTLET_DATA   = "intent_key_portlet_data";
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
                break;
            case INTENT_TYPE_CLOSE_MANAGER:
                closeManager();
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
        Portal portal = constructPortal(intent.getStringExtra(INTENT_KEY_PORTAL_CLASS));
        if(portal != null) {
            portal.onCreate(intent.getBundleExtra(INTENT_KEY_PORTAL_DATA));
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
            portlet = constructPortlet(intent.getStringExtra(INTENT_KEY_PORTLET_CLASS), portletId);
            if(portlet != null) {
                portlet.onCreate(intent.getBundleExtra(INTENT_KEY_PORTLET_DATA));
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

    public static <T extends Portal> void openPortal(Context context, Class<T> portalType, Bundle portalData) {
        openPortal(context, portalType, portalData, PortalManager.class);
    }

    public static <T extends Portal, S extends PortalManager> void openPortal(Context context, Class<T> portalType, Bundle portalData, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_OPEN_PORTAL);
        intent.putExtra(INTENT_KEY_PORTAL_CLASS, portalType.getName());
        intent.putExtra(INTENT_KEY_PORTAL_DATA, portalData);
        context.startService(intent);
    }

    public static <S extends PortalManager> void showPortal(Context context, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_SHOW_PORTAL);
        context.startService(intent);
    }

    public static <S extends PortalManager> void hidePortal(Context context, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_HIDE_PORTAL);
        context.startService(intent);
    }

    public static <S extends PortalManager> void closePortal(Context context, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_CLOSE_PORTAL);
        context.startService(intent);
    }

    public static void showPortal(Context context) {
        showPortal(context, PortalManager.class);
    }

    public static void hidePortal(Context context) {
        hidePortal(context, PortalManager.class);
    }

    public static void closePortal(Context context) {
        closePortal(context, PortalManager.class);
    }

    public static <T extends Portlet> void openPortlet(Context context, Class<T> portletType, int portletId, Bundle portletData) {
        openPortlet(context, portletType, portletId, portletData, PortalManager.class);
    }

    public static <T extends Portlet, S extends PortalManager> void openPortlet(Context context, Class<T> portletType, int portletId, Bundle portletData, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_OPEN_PORTLET);
        intent.putExtra(INTENT_KEY_PORTLET_CLASS, portletType.getName());
        intent.putExtra(INTENT_KEY_PORTLET_DATA, portletData);
        intent.putExtra(INTENT_KEY_PORTLET_ID, portletId);
        context.startService(intent);
    }

    public static <S extends PortalManager> void showPortlet(Context context, int portletId, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_SHOW_PORTLET);
        intent.putExtra(INTENT_KEY_PORTLET_ID, portletId);
        context.startService(intent);
    }

    public static <S extends PortalManager> void hidePortlet(Context context, int portletId, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_HIDE_PORTLET);
        intent.putExtra(INTENT_KEY_PORTLET_ID, portletId);
        context.startService(intent);
    }

    public static <S extends PortalManager> void closePortlet(Context context, int portletId, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_CLOSE_PORTLET);
        intent.putExtra(INTENT_KEY_PORTLET_ID, portletId);
        context.startService(intent);
    }

    public static void showPortlet(Context context, int portletId) {
        showPortlet(context, portletId, PortalManager.class);
    }

    public static void hidePortlet(Context context, int portletId) {
        hidePortlet(context, portletId, PortalManager.class);
    }

    public static void closePortlet(Context context, int portletId) {
        closePortlet(context, portletId, PortalManager.class);
    }

    public static <S extends PortalManager> void closeManager(Context context, Class<S> managerType) {
        Intent intent = new Intent(context, managerType);
        intent.putExtra(INTENT_KEY_INTENT_TYPE, INTENT_TYPE_CLOSE_MANAGER);
        context.startService(intent);
    }

    public static void closeManager(Context context) {
        closeManager(context, PortalManager.class);
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
}
