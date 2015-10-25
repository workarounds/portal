package in.workarounds.portal;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
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
public abstract class AbstractPortal extends ContextWrapper {
    public static final int STATE_HIDDEN = 1;
    public static final int STATE_ACTIVE = 2;

    protected @PORTAL_STATE int mState = STATE_HIDDEN;
    protected View mView;

    public AbstractPortal(Context base) {
        super(base);
    }

    protected void setContentView(@LayoutRes int layoutId) {
        setContentView(LayoutInflater.from(this).cloneInContext(this).inflate(layoutId, new FrameLayout(this), false));
    }

    protected void setContentView(View view) {
        this.mView = view;
    }

    /**
     * Look for a child view with the given id.  If this view has the given
     * id, return this view.
     *
     * @param id The id to search for.
     * @return The view that has the given id in the hierarchy or null
     */
    @Nullable
    public View findViewById(@IdRes int id) {
        return mView.findViewById(id);
    }

    public int getState() {
        return mState;
    }

    /**
     * @return the layout params to be used while attaching the Portal to window
     */
    @NonNull
    protected abstract WindowManager.LayoutParams getLayoutParams();

    /**
     * @return the inflated view of the portal
     */
    protected View getView() {
        return mView;
    }

    protected void onCreate(Bundle bundle) {

    }

    protected void onData(Bundle data) {

    }

    protected void onResume() {
        mState = STATE_ACTIVE;
    }

    protected void onPause() {
        mState = STATE_HIDDEN;
    }

    protected void onDestroy() {

    }

    public abstract void finish();
    public abstract void setPortalManager(PortalManager portalManager);

    @IntDef({STATE_HIDDEN, STATE_ACTIVE})
    public @interface PORTAL_STATE {
    }

}
