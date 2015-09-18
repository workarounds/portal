package in.workarounds.portal;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        if(mView.getLayoutParams() instanceof WindowManager.LayoutParams) {
            return (WindowManager.LayoutParams) mView.getLayoutParams();
        } else {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            params.flags = params.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
            params.format = PixelFormat.TRANSLUCENT;

            FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams) mView.getLayoutParams();

            ParamUtils.transferMarginAndGravity(params, viewParams);
            return params;
        }
    }

    @Override
    public void finish() {
        Portlet.with(this).id(getId()).manager(mPortalManager.getClass()).close();
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

    protected void closeAll() {
        mPortalManager.stopSelf();
    }

    public static PortletBuilder with(Context context) {
        return new PortletBuilder(context);
    }
}
