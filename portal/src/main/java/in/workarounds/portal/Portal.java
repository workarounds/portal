package in.workarounds.portal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import in.workarounds.portal.util.ParamUtils;

/**
 * Created by madki on 16/09/15.
 */
public class Portal extends AbstractPortal {
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
        if (mView.getLayoutParams() instanceof WindowManager.LayoutParams) {
            return (WindowManager.LayoutParams) mView.getLayoutParams();
        } else {
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
    }

    @Override
    public void finish() {
    }

    public boolean onBackPressed() {
        finish();
        return true;
    }

    @Override
    @CallSuper
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
//        mSimplePortalService.startActivityForResult(intent, requestCode);
    }

    public void addOnCloseDialogsListener(WrapperLayout.OnCloseDialogsListener listener) {
        ((WrapperLayout) getView()).addOnCloseDialogsListener(listener);
    }

    public void removeOnCloseDialogsListener(WrapperLayout.OnCloseDialogsListener listener) {
        ((WrapperLayout) getView()).removeOnCloseDialogsListener(listener);
    }


    public static Manager manager(@NonNull Context context, @NonNull Class<? extends Service> serviceClass) {
        return new Manager(context, serviceClass);
    }

    public static class Manager {
        private Context context;
        private Class<?> serviceClass;

        private Manager(@NonNull Context context, @NonNull Class<? extends Service> serviceClass) {
            this.context = context.getApplicationContext();
            this.serviceClass = serviceClass;
        }

        private Intent getIntent() {
            return new Intent(context, serviceClass);
        }

        public void openPortal(int portalId) {
            context.startService(getIntent().putExtras(IntentResolver.openPortal(portalId)));
        }

        public void showPortal(int portalId) {
            context.startService(getIntent().putExtras(IntentResolver.showPortal(portalId)));
        }

        public void hidePortal(int portalId) {
            context.startService(getIntent().putExtras(IntentResolver.hidePortal(portalId)));
        }

        public void closePortal(int portalId) {
            context.startService(getIntent().putExtras(IntentResolver.closePortal(portalId)));
        }

        public void sendPortal(int portalId, Bundle data) {
            context.startService(getIntent().putExtras(IntentResolver.sendPortal(portalId, data)));
        }

        public void closeManager() {
            context.startService(getIntent().putExtras(IntentResolver.closeManager()));
        }

        public void sendToAll(@NonNull Bundle data) {
            context.startService(getIntent().putExtras(IntentResolver.sendToAll(data)));
        }

        public Class<?> getServiceClass() {
            return serviceClass;
        }

        public void setServiceClass(Class<?> serviceClass) {
            this.serviceClass = serviceClass;
        }
    }
}
