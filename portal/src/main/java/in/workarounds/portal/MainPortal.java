package in.workarounds.portal;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by madki on 28/12/15.
 */
public class MainPortal<T extends PortalAdapter> extends Portal<T> implements WrapperLayout.OnCloseDialogsListener {

    public MainPortal(Context base, T portalAdapter) {
        super(base, portalAdapter);
    }


    @Override
    protected void setContentView(@NonNull View view) {
        if (!(view instanceof WrapperLayout)) {
            WrapperLayout parent = new WrapperLayout(this);
            parent.addView(view);
            this.view = parent;
        } else {
            this.view = view;
        }
        setLayoutParams(view);
    }

    @NonNull
    @Override
    protected WindowManager.LayoutParams portalLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.flags = params.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return params;
    }

    @Override
    @CallSuper
    protected void onViewAttached() {
        super.onViewAttached();
        addOnCloseDialogsListener(this);
    }

    @Override
    protected void onDetachView() {
        super.onDetachView();
        removeOnCloseDialogsListener(this);
    }

    @Override
    @NonNull
    public View getView() {
        if (super.getView() == null) {
            throw new IllegalStateException("MainPortal has no view. Please call setContentView or use Portal if no view is needed.");
        }
        return super.getView();
    }

    protected boolean onBackPressed() {
        finish();
        return true;
    }

    protected boolean onHomePressed() {
        detach();
        return true;
    }

    protected boolean onRecentAppsPressed() {
        detach();
        return true;
    }

    @Override
    public void onCloseDialogs(@WrapperLayout.IReason int reason) {
        switch (reason) {
            case WrapperLayout.Reason.KEY_BACK:
                onBackPressed();
                break;
            case WrapperLayout.Reason.KEY_HOME:
                onHomePressed();
                break;
            case WrapperLayout.Reason.KEY_RECENT_APPS:
                onRecentAppsPressed();
                break;
            case WrapperLayout.Reason.KEY_UNKNOWN:
            default:
                onHomePressed();
                break;
        }
    }

    public void addOnCloseDialogsListener(WrapperLayout.OnCloseDialogsListener listener) {
        ((WrapperLayout) getView()).addOnCloseDialogsListener(listener);
    }

    public void removeOnCloseDialogsListener(WrapperLayout.OnCloseDialogsListener listener) {
        ((WrapperLayout) getView()).removeOnCloseDialogsListener(listener);
    }
}
