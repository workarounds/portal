package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * <p>
 * Implements the behavior of the floating UIs. This is the main component of the library. Extend
 * this to create your own floating UIs.
 * </p>
 *
 * @param <T> the {@link PortalAdapter} that would instantiate this Portal
 */
public class Portal<T extends PortalAdapter> extends ContextThemeWrapper {
    /**
     * Tag for logging
     */
    private static final String TAG = "Portal";

    /**
     * View that is set to the portal. Can be null. Use{@link #getView()} to get a reference to the view.
     * Use {@link #setView(View)} or {@link #setContentView(View)} or {@link #setContentView(int)}
     * to set it.
     */
    View view;

    /**
     * Value of the layout resource set using {@link #setContentView(int)}. This is used to retrieve the
     * resource again when orientation changes occur
     */
    @LayoutRes
    private int layoutId = -1;

    /**
     * Custom implementation of the {@link PortalAdapter} that instantiates this Portal.
     */
    @NonNull
    protected final T portalAdapter;

    /**
     * the id of the portal with which the adapter instantiated this portal
     */
    private final int portalId;

    /**
     * Cached value of the android window manager service
     */
    protected WindowManager windowManager;

    /**
     * LayoutParams that are used to attach the view to window. These are computed once when the
     * view is initially set using {@link #setContentView(View)} or {@link #setContentView(int)}
     */
    protected WindowManager.LayoutParams layoutParams;

    /**
     * @param portalAdapter custom implementation of {@link PortalAdapter} that instantiates this
     *                      Portal.
     * @param portalId      the id of the portal with which the adapter instantiated this portal
     */
    public Portal(@NonNull T portalAdapter, int portalId) {
        this.portalAdapter = portalAdapter;
        portalAdapter.throwIfInvalidId(portalId);
        this.portalId = portalId;
    }

    /**
     * Sets the view specified by the layoutId (calls the required callbacks). Saves the value of
     * layoutId, sets the view and computes the layoutParams for the view
     *
     * @param layoutId id of the layout to be set
     * @see #setContentView(View)
     * @see #setView(View)
     */
    protected void setContentView(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        setContentView(LayoutInflater.from(this).cloneInContext(this).inflate(layoutId, new FrameLayout(this), false));
    }

    /**
     * Sets the view and computes the layoutParams for the view
     *
     * @param view to be set as the view
     * @see #setView(View)
     * @see #setLayoutParams(View)
     */
    protected void setContentView(View view) {
        setView(view);
        setLayoutParams(view);
    }

    /**
     * @return {@link #layoutParams}
     */
    @Nullable
    protected WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    /**
     * Computes the layoutParams from the given view and saves them in layoutParams field
     * Converts the FrameLayout.LayoutParams of the view into WindowManager.LayoutParams
     *
     * @param view from which the params are to be inferred
     * @see ParamUtils#transferMarginAndGravity(WindowManager.LayoutParams, FrameLayout.LayoutParams)
     */
    protected void setLayoutParams(View view) {
        if (getView() == null) {
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

    /**
     * These params are the basic settings that makes it possible to attach the view as System
     * alert (over other apps). The additional flags set here add the Portal as a system alert (over telephone calls) and
     * split the touch between the portal and the background. The FLAG_NOT_FOCUSABLE flag is required
     * if the app in the background is to receive the back button (this is the major change in params
     * for {@link MainPortal})
     *
     * @return layoutParams that define the behavior of the portal
     */
    @NonNull
    protected WindowManager.LayoutParams portalLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = params.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        params.format = PixelFormat.TRANSLUCENT;
        return params;
    }

    /**
     * Sets the base context and theme. Called immediately after portal instantiation
     *
     * @param base    base context
     * @param themeId id of the theme
     */
    public void initThemedContext(Context base, int themeId) {
        attachBaseContext(base);
        if (themeId != -1) {
            setTheme(themeId);
        }
    }

    /**
     * Lifecycle method that is called after instantiation. {@link #initThemedContext(Context, int)}
     * is called before onCreate. So if each portal needs to have a different theme you can call
     * {@link #setTheme(int)} in onCreate
     *
     * @param data that is passed using {@link Portals#open(int, Bundle, Context, Class)}
     */
    protected void onCreate(@Nullable Bundle data) {
    }

    /**
     * Lifecycle method is called when data is sent using {@link Portals#send(int, Bundle, Context, Class)}
     * or {@link Portals#sendToAll(Bundle, Context, Class)} if the Portal is open
     * or {@link Portals#open(int, Bundle, Context, Class)} if the portal is already open
     *
     * @param data that is sent using the above methods
     * @return true if the data is handled
     */
    protected boolean onData(@Nullable Bundle data) {
        return true;
    }

    /**
     * Lifecycle method is called when the view is set for the first time. Use this to finding views
     * and adding listeners
     */
    protected void onViewCreated() {

    }

    /**
     * Attaches the Portal's view to the window if it's not null and isn't already attached. Calls
     * the {@link #onViewAttached()} after the view is attached
     *
     * @return true if the view is not null and is not already attached
     */
    public boolean attach() {
        if (getView() != null && !(isViewAttached())) {
            getWindowManager().addView(getView(), getLayoutParams());
            onViewAttached();
            return true;
        }
        return false;
    }

    /**
     * Lifecycle method called after the view is attached. This is similar to onResume functionality
     * of an android Activity or Fragment.
     */
    protected void onViewAttached() {

    }

    /**
     * Lifecycle method called when configuration changes occur. If a {@link #layoutId} is provided
     * then the current view is detached and re-inflated from the layoutId. This is useful if the
     * landscape and portrait layouts are different.
     *
     * @param newConfig new configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        if (layoutId != -1) {
            boolean detached = detach();
            setContentView(null);
            setContentView(layoutId);
            if (detached) attach();
        }
    }

    /**
     * Lifecycle method called before detaching the view from window. This is similar to onPause of
     * the activity or fragment lifecycle
     */
    protected void onDetachView() {

    }

    /**
     * Detaches the Portal's view from window if it's not null and attached to window. Calls
     * {@link #onDetachView()} before detaching the view
     *
     * @return true if view is detached
     */
    public boolean detach() {
        if (isViewAttached()) {
            onDetachView();
            getWindowManager().removeView(getView());
            return true;
        }
        return false;
    }

    /**
     * Lifecycle method called before destroying the view. Remove listeners or any references to the
     * view
     */
    protected void onDestroyView() {

    }

    /**
     * Lifecycle method called when before destroying the Portal. Release resources and remove all
     * references to the portal
     */
    protected void onDestroy() {
    }

    /**
     * Destroys the Portal if a portalAdapter has been set. Else just detaches the View from window
     */
    public void finish() {
        portalAdapter.close(portalId);
        // if for some reason portalAdapter doesn't have a reference to this portal
        // this ensures that the portal's view is detached from window
        detach();
    }

    /**
     * @return the Portal's view
     */
    @Nullable
    public View getView() {
        return view;
    }

    /**
     * Destroys the view already set to the portal(sets it to null and calls {@link #onDestroyView()})
     * and assigns the new value of view. Does not check if the view's are equal. Does not detach
     * the existing view if it's attached to the window. Make sure to {@link #detach()} the view
     * else the reference to the view will be lost and there'd be no way to remove it from the
     * window (stranded view)
     *
     * @param view to be set as the Portal's view
     */
    protected void setView(View view) {
        if (view == null && this.view != null) {
            onDestroyView();
        }

        this.view = view;

        if (view != null) {
            onViewCreated();
        }
    }

    /**
     * @return true if view not null and is attached to window
     */
    public boolean isViewAttached() {
        return getView() != null && getView().getWindowToken() != null;
    }

    /**
     * Finds the view with given id in Portal's view
     *
     * @param id of the view to be found
     * @return view if found, else null
     */
    @Nullable
    public View findViewById(@IdRes int id) {
        if (getView() == null) return null;
        return getView().findViewById(id);
    }

    /**
     * Similar to onActivityResult of activity. The result of an activity started using
     * {@link PortalAdapter#startActivityForResult(Intent, int)}
     *
     * @param requestCode request code of the request
     * @param resultCode  value to indicate if the result is ok, cancelled or destroyed
     *                    {@link MockActivity#RESULT_OK}, {@link MockActivity#RESULT_CANCELLED} or
     *                    {@link MockActivity#RESULT_DESTROYED}
     * @param result      the result sent back by the activity
     * @return true if the result is handled
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent result) {
        return false;
    }

    /**
     * @return window manager
     */
    protected WindowManager getWindowManager() {
        if (windowManager == null) windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        return windowManager;
    }
}
