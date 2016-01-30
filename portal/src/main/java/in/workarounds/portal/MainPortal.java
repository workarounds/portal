package in.workarounds.portal;

import android.graphics.PixelFormat;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

/**
 * A type of Portal that has focus and can listen to KeyEvents.
 * @param <T> Custom implementation of {@link PortalAdapter} that instantiates this Portal
 */
public class MainPortal<T extends PortalAdapter> extends Portal<T> implements WrapperLayout.OnCloseDialogsListener {

    /**
     * {@inheritDoc}
     * @param portalAdapter
     * @param portalId
     */
    public MainPortal(T portalAdapter, int portalId) {
        super(portalAdapter, portalId);
    }

    /**
     * {@inheritDoc}
     * It also adds a {@link WrapperLayout} around the view if the view is not an instance of
     * {@link WrapperLayout}. This is to be able to listen to button clicks of Home button and
     * recent apps button
     * @param view to be set as the view
     */
    @Override
    protected void setContentView(View view) {
        if (view != null && !(view instanceof WrapperLayout)) {
            WrapperLayout parent = new WrapperLayout(this);
            parent.addView(view);
            setView(parent);
            setLayoutParams(view);
        } else {
            super.setContentView(view);
        }
    }

    /**
     * Sets the layout params for the MainPortal so that it's focusable (listen to KeyEvents)
     * @return layoutParams for MainPortal
     */
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

    /**
     * {@inheritDoc}
     * Sets the Portal as a listener to {@link WrapperLayout} to listen to back, home and recent apps
     * keys
     */
    @Override
    @CallSuper
    protected void onViewAttached() {
        super.onViewAttached();
        setAsListener();
    }

    /**
     * {@inheritDoc}
     * Removes the Portal as listener to the {@link WrapperLayout}
     */
    @Override
    protected void onDetachView() {
        super.onDetachView();
        removeAsListener();
    }

    /**
     * Method called when back button is clicked. Finishes the Portal by default
     * @return true if back button is handled
     */
    protected boolean onBackPressed() {
        finish();
        return true;
    }

    /**
     * Method called when home button is pressed. Detaches the Portal's view by default
     * @return true if handled
     */
    protected boolean onHomePressed() {
        detach();
        return true;
    }

    /**
     * Method called when recent apps button is pressed. Detaches the Portal's view by default
     * @return true if handled
     */
    protected boolean onRecentAppsPressed() {
        detach();
        return true;
    }

    /**
     * Method called by {@link WrapperLayout} when back, home or recent apps buttons are pressed
     * @param reason which triggered the callback
     * @see WrapperLayout
     * @see in.workarounds.portal.WrapperLayout.OnCloseDialogsListener
     */
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

    /**
     * Sets the Portal as lister to the view if it's not null.
     */
    public void setAsListener() {
        if(getView() == null) {
            throw new IllegalStateException("View is null, cannot set listener");
        } else {
            ((WrapperLayout) getView()).addOnCloseDialogsListener(this);
        }
    }

    /**
     * Removes the Portal as listener to the view.
     */
    public void removeAsListener() {
        if(getView() != null) {
            ((WrapperLayout) getView()).removeOnCloseDialogsListener(this);
        }
    }
}
