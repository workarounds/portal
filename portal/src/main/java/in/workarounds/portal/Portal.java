package in.workarounds.portal;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by madki on 16/09/15.
 */
public class Portal extends AbstractPortal {
    private PortalManager mPortalManager;
    protected boolean mRootAdded;

    public Portal(Context base) {
        super(base);
    }

    @Override
    protected void setContentView(View view) {
        if (view instanceof WrapperLayout) {
            super.setContentView(view);
            mRootAdded = false;
        } else {
            WrapperLayout parent = new WrapperLayout(this);
            parent.addView(view);
            mRootAdded = true;
            super.setContentView(parent);
        }
    }

    @NonNull
    @Override
    protected WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.flags = params.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format = PixelFormat.TRANSLUCENT;

        if (!mRootAdded) {
            FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams) mView.getLayoutParams();
            ParamUtils.transferMarginAndGravity(params, viewParams);
        } else {
            params.gravity = Gravity.TOP;
        }

        return params;
    }

    @Override
    public void finish() {
        PortalManager.closePortal(this, mPortalManager.getClass());
    }

    public boolean onBackPressed() {
        finish();
        return true;
    }

    public void setPortalManager(PortalManager portalManager) {
        mPortalManager = portalManager;
    }

    public void addOnCloseDialogsListener(WrapperLayout.OnCloseDialogsListener listener) {
        ((WrapperLayout) getView()).addOnCloseDialogsListener(listener);
    }

    public void removeOnCloseDialogsListener(WrapperLayout.OnCloseDialogsListener listener) {
        ((WrapperLayout) getView()).removeOnCloseDialogsListener(listener);
    }

    @Nullable
    protected Portlet getPortlet(int portletId) {
        return mPortalManager.getPortlet(portletId);
    }

}
