package in.workarounds.portal;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.util.HashSet;
import java.util.Set;

import static in.workarounds.portal.WrapperLayout.Reason.KEY_BACK;
import static in.workarounds.portal.WrapperLayout.Reason.KEY_HOME;
import static in.workarounds.portal.WrapperLayout.Reason.KEY_RECENT_APPS;
import static in.workarounds.portal.WrapperLayout.Reason.KEY_UNKNOWN;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by madki on 16/09/15.
 */
public class WrapperLayout extends FrameLayout {
    private Set<OnCloseDialogsListener> listeners = new HashSet<>();

    public WrapperLayout(Context context) {
        super(context);
    }

    public WrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapperLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onCloseSystemDialogs(String reason) {
        switch (reason) {
            case "homekey":
                sendCallbackToListeners(KEY_HOME);
                break;
            case "recentapps":
                sendCallbackToListeners(KEY_RECENT_APPS);
                break;
            default:
                sendCallbackToListeners(KEY_UNKNOWN);
                break;
        }
    }

    public interface OnCloseDialogsListener {
        void onCloseDialogs(@REASON int reason);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) sendCallbackToListeners(KEY_BACK);
        return super.dispatchKeyEvent(event);
    }

    public void addOnCloseDialogsListener(@NonNull OnCloseDialogsListener listener) {
        if (!listeners.add(listener)) {
            Log.w("WrapperLayout", "Listener already registered");
        }
    }

    public void removeOnCloseDialogsListener(@NonNull OnCloseDialogsListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        } else {
            Log.w("WrapperLayout", "Listener was not registered");
        }
    }

    private void sendCallbackToListeners(@REASON int reason) {
        if (listeners.size() > 0) {
            for (OnCloseDialogsListener listener : listeners) {
                listener.onCloseDialogs(reason);
            }
        }
    }

    public interface Reason {
        int KEY_BACK = 1;
        int KEY_HOME = 2;
        int KEY_RECENT_APPS = 3;
        int KEY_UNKNOWN = 4;
    }

    @Retention(SOURCE)
    @IntDef({KEY_BACK, KEY_HOME, KEY_RECENT_APPS, KEY_UNKNOWN})
    public @interface REASON {}
}
