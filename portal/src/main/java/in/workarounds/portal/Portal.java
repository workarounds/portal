package in.workarounds.portal;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by madki on 16/09/15.
 */
public class Portal<T extends PortalAdapter> extends ContextWrapper {
    private static final String TAG = "Portal";
    View view;
    private int layoutId = -1;

    protected final T portalAdapter;
    protected final WindowManager windowManager;
    protected WindowManager.LayoutParams layoutParams;

    public Portal(Context base, T portalAdapter) {
        super(base);
        this.portalAdapter = portalAdapter;
        windowManager = (WindowManager) base.getSystemService(WINDOW_SERVICE);
    }

    protected void setContentView(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        setContentView(LayoutInflater.from(this).cloneInContext(this).inflate(layoutId, new FrameLayout(this), false));
    }

    protected void setContentView(View view) {
        setView(view);
        setLayoutParams(view);
    }

    @Nullable
    protected WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }


    protected void setLayoutParams(View view) {
        if(getView() == null) {
            layoutParams = null;
            return;
        }
        if (view.getLayoutParams() instanceof WindowManager.LayoutParams) {
            layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        } else {
            layoutParams = portalLayoutParams();
            FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            ParamUtils.transferMarginAndGravity(layoutParams, viewParams);
        }
    }

    @NonNull
    protected WindowManager.LayoutParams portalLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = params.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        params.format = PixelFormat.TRANSLUCENT;
        return params;
    }

    protected void onCreate(@Nullable Bundle data) {
    }

    protected boolean onData(@Nullable Bundle data) {
        return true;
    }

    protected void onViewCreated() {

    }

    public boolean attach() {
        if(getView() != null && !(isViewAttached())) {
            windowManager.addView(getView(), getLayoutParams());
            onViewAttached();
            return true;
        }
        return false;
    }

    protected void onViewAttached() {

    }

    public void onConfigurationChanged(Configuration newConfig) {
        if(layoutId != -1) {
            boolean detached = detach();
            setContentView(null);
            setContentView(layoutId);
            if(detached) attach();
        }
    }

    protected void onDetachView() {

    }

    public boolean detach() {
        if(isViewAttached()) {
            onDetachView();
            windowManager.removeView(getView());
            return true;
        }
        return false;
    }

    protected void onDestroyView() {

    }

    protected void onDestroy() {
    }

    public void finish() {
        if(portalAdapter != null) {
            portalAdapter.close(portalAdapter.indexOf(this));
        } else {
            detach();
        }
    }

    @Nullable
    public View getView() {
        return view;
    }

    protected void setView(View view) {
        if (view == null && this.view != null) {
            onDestroyView();
        }

        this.view = view;

        if(view != null) {
            onViewCreated();
        }
    }

    public boolean isViewAttached() {
        return getView() != null && getView().getWindowToken() != null;
    }

    @Nullable
    public View findViewById(@IdRes int id) {
        if(getView() == null) return null;
        return getView().findViewById(id);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent result) {
       return false;
    }

}
