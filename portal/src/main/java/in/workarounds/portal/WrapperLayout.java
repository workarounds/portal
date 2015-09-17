package in.workarounds.portal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madki on 16/09/15.
 */
public class WrapperLayout extends FrameLayout {
    private List<OnCloseDialogsListener> listeners = new ArrayList<>();

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
                sendCallbackToListeners(Reason.KEY_HOME);
                break;
            case "recentapps":
                sendCallbackToListeners(Reason.KEY_RECENT_APPS);
                break;
            default:
                sendCallbackToListeners(Reason.KEY_UNKNOWN);
                break;
        }
    }

    public interface OnCloseDialogsListener {
        void onCloseDialogs(Reason reason);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) sendCallbackToListeners(Reason.KEY_BACK);
        return super.dispatchKeyEvent(event);
    }

    public void addOnCloseDialogsListener(@NonNull OnCloseDialogsListener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        } else {
            Log.w("WrapperLayout", "Listener already registered");
        }
    }

    public void removeOnCloseDialogsListener(@NonNull OnCloseDialogsListener listener) {
        if(listeners.contains(listener)) {
            listeners.remove(listener);
        } else {
            Log.w("WrapperLayout", "Listener was not registered");
        }
    }

    private void sendCallbackToListeners(Reason reason) {
        for(OnCloseDialogsListener listener: listeners) {
            listener.onCloseDialogs(reason);
        }
    }

    public enum Reason {
        KEY_BACK,
        KEY_HOME,
        KEY_RECENT_APPS,
        KEY_UNKNOWN
    }
}
