package in.workarounds.portal;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by madki on 16/09/15.
 */
public class Portlet extends AbstractPortal {
    private PortalManager mPortalManager;

    private final int ID;

    public Portlet(Context base, int id) {
        super(base);
        this.ID = id;
    }

    public int getId() {
        return ID;
    }

    @NonNull
    @Override
    protected WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = params.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;

        FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams) mView.getLayoutParams();

        params.gravity = viewParams.gravity;
        int gravity = viewParams.gravity;

        if((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT) {
            params.x = viewParams.rightMargin;
        } else {
            params.x = viewParams.leftMargin;
        }

        if((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
            params.y = viewParams.bottomMargin;
        } else {
            params.y = viewParams.topMargin;
        }

        return params;
    }

    @Override
    public void finish() {
        PortalManager.closePortlet(this, getId(), mPortalManager.getClass());
    }

    public void setPortalManager(PortalManager portalManager) {
        mPortalManager = portalManager;
    }

    public static boolean isValidID(int id) {
        return id > 0;
    }

    @Nullable
    protected Portal getPortal() {
        return mPortalManager.getPortal();
    }
}
