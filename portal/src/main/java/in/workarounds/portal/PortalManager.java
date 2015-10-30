package in.workarounds.portal;

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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;
import in.workarounds.portal.WrapperLayout.Reason;

/**
 * Created by madki on 16/09/15.
 */
@Freighter
public class PortalManager extends ForegroundService implements WrapperLayout.OnCloseDialogsListener {
    private static final String TAG = "PortalManager";

    @Cargo @INTENT_TYPE
    int intentType = IntentType.NO_TYPE;
    @Cargo
    String className;
    @Cargo @NonNull
    Bundle data = new Bundle();
    @Cargo
    int portletId = -1;

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
        if (intent != null) {
            FreighterPortalManager.Retriever retriever = FreighterPortalManager.retrieve(intent);
            if(retriever.hasIntentType()) {
                resolveIntent(retriever.intentType(intentType), retriever);
            } else {
                super.onStartCommand(intent, flags, startId);
            }
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
        PortalState.getInstance(this).clear();
        PortletState.getInstance(this).clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mPortal != null) {
            mPortal.onActivityResult(requestCode, resultCode, data);
        }
        for(Portlet portlet: mPortlets.values()) {
            portlet.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void resolveIntent(int intentType, FreighterPortalManager.Retriever retriever) {
        switch (intentType) {
            case IntentType.OPEN_PORTAL:
                openPortal(retriever.className(), retriever.data());
                break;
            case IntentType.HIDE_PORTAL:
                hidePortal();
                break;
            case IntentType.SHOW_PORTAL:
                showPortal();
                break;
            case IntentType.CLOSE_PORTAL:
                closePortal();
                checkForTermination();
                break;
            case IntentType.OPEN_PORTLET:
                openPortlet(retriever.portletId(portletId), retriever.className(), retriever.data());
                break;
            case IntentType.HIDE_PORTLET:
                hidePortlet(retriever.portletId(portletId));
                break;
            case IntentType.SHOW_PORTLET:
                showPortlet(retriever.portletId(portletId));
                break;
            case IntentType.CLOSE_PORTLET:
                closePortlet(retriever.portletId(portletId));
                checkForTermination();
                break;
            case IntentType.CLOSE_MANAGER:
                closeManager();
                break;
            case IntentType.SEND_PORTAL:
                sendDataToPortal(retriever.className(), retriever.data());
                break;
            case IntentType.SEND_PORTLET:
                Log.d(TAG, "send portlet type");
                sendDataToPortlet(retriever.portletId(portletId), retriever.className(), retriever.data());
                break;
            case IntentType.SEND_TO_ALL:
                sendData(retriever.data());
                break;
            case IntentType.NO_TYPE:
                break;
        }
    }

    protected void openPortal(String className, Bundle data) {
        openPortal(createPortal(className, data));
    }

    protected void openPortal(Portal portal) {
        if (portal != null) {
            closePortal();
            mPortal = portal;
            showPortal();
        }
    }

    protected void openPortlet(int portletId, String className, Bundle data) {
        openPortlet(createPortlet(portletId, className, data));
    }

    protected void openPortlet(Portlet portlet) {
        if (portlet != null && Portlet.isValidID(portlet.getId())) {
            closePortlet(portlet.getId());
            addPortlet(portlet);
            showPortlet(portlet.getId());
        }
    }

    @Nullable
    protected Portal createPortal(String className, Bundle data) {
        Portal portal = constructPortal(className);
        if (portal != null) {
            portal.onCreate(data);
            portal.setPortalManager(this);
        } else {
            Log.e(TAG, "Problem creating portal");
        }
        return portal;
    }

    @Nullable
    protected Portlet createPortlet(int portletId, String className, Bundle data) {
        Portlet portlet = null;
        if (portletId != -1) {
            portlet = constructPortlet(className, portletId);
            if (portlet != null) {
                portlet.onCreate(data);
                portlet.setPortalManager(this);
            }
        }
        return portlet;
    }

    protected void closePortal() {
        hidePortal();
        if (mPortal != null) {
            mPortal.onDestroy();
            mPortal = null;
        }
    }

    protected void closePortlet(int portletId) {
        closePortlet(getPortlet(portletId));
    }

    protected void closePortlet(Portlet portlet) {
        hidePortlet(portlet);
        if (portlet != null) {
            portlet.onDestroy();
            removePortlet(portlet.getId());
        }
    }

    protected void showPortal() {
        if (mPortal != null && mPortal.getState() != AbstractPortal.STATE_ACTIVE) {
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
        if (portlet != null && portlet.getState() != AbstractPortal.STATE_ACTIVE) {
            attachToWindow(portlet.getView(), portlet.getLayoutParams());
            portlet.onResume();
        }
    }

    protected void hidePortal() {
        if (mPortal != null && mPortal.getState() == AbstractPortal.STATE_ACTIVE) {
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
        if (portlet != null && portlet.getState() == AbstractPortal.STATE_ACTIVE) {
            portlet.onPause();
            detachFromWindow(portlet.getView());
        }
    }

    protected void sendData(Bundle data) {
        if (mPortal != null) {
            sendDataToPortal(null, data);
        }
        for (int id : mPortlets.keySet()) {
            sendDataToPortlet(id, null, data);
        }
    }

    protected void sendDataToPortal(String className, Bundle data) {
        if (mPortal != null) {
            mPortal.onData(data);
        } else {
            openPortal(className, data);
        }
    }

    protected void sendDataToPortlet(int portletId, String className, Bundle data) {
        Portlet portlet = getPortlet(portletId);
        if (portlet != null) {
            portlet.onData(data);
        } else {
            openPortlet(portletId, className, data);
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
        if (Portlet.isValidID(portlet.getId())) {
            mPortlets.put(portlet.getId(), portlet);
        }
    }

    protected void removePortlet(int portletId) {
        if (mPortlets.containsKey(portletId)) {
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
        if (!TextUtils.isEmpty(className)) {
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
        if (type != null) {
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
        if (type != null) {
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
        if (constructor != null) {
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
        if (constructor != null) {
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
                if (mPortal != null) {
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
        intent.putExtras(FreighterPortalManager.supply()
                        .intentType(IntentType.CLOSE_MANAGER)
                        .bundle()
        );
        context.startService(intent);
    }

    public static void send(Context context, Bundle data) {
        send(context, data, PortalManager.class);
    }

    public static <S extends PortalManager> void send(Context context, Bundle data, Class<S> type) {
        Intent intent = new Intent(context, type);
        intent.putExtras(FreighterPortalManager.supply()
                        .intentType(IntentType.SEND_TO_ALL)
                        .data(data)
                        .bundle()
        );
        context.startService(intent);
    }

    public static
    @State.STATE
    int getPortalState(Context context, Class<? extends Portal> type) {
        return PortalState.getInstance(context).getState(type);
    }

    public static
    @State.STATE
    int getPortletState(Context context, int id) {
        return PortletState.getInstance(context).getState(id);
    }

    private void checkForTermination() {
        if (mPortal == null && mPortlets.isEmpty()) {
            stopSelf();
        }
    }


    @IntDef({IntentType.NO_TYPE, IntentType.OPEN_PORTAL, IntentType.SHOW_PORTAL,
            IntentType.HIDE_PORTAL, IntentType.CLOSE_PORTAL, IntentType.OPEN_PORTLET,
            IntentType.SHOW_PORTLET, IntentType.HIDE_PORTLET, IntentType.CLOSE_PORTLET,
            IntentType.CLOSE_MANAGER, IntentType.SEND_PORTLET, IntentType.SEND_PORTAL,
            IntentType.SEND_TO_ALL, IntentType.ACTIVITY_RESULT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface INTENT_TYPE {
    }



    public interface IntentType {
        int NO_TYPE = 0;
        int OPEN_PORTAL = 1;
        int CLOSE_PORTAL = 2;
        int HIDE_PORTAL = 3;
        int SHOW_PORTAL = 4;
        int SEND_PORTAL = 5;

        int OPEN_PORTLET = 11;
        int CLOSE_PORTLET = 12;
        int HIDE_PORTLET = 13;
        int SHOW_PORTLET = 14;
        int SEND_PORTLET = 15;

        int CLOSE_MANAGER = 100;
        int SEND_TO_ALL = 101;
        int ACTIVITY_RESULT = 102;

    }
}
