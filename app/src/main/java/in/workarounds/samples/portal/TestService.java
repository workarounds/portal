package in.workarounds.samples.portal;

import android.support.annotation.NonNull;

import com.squareup.leakcanary.RefWatcher;

import in.workarounds.portal.OverlayPermissionHelper;
import in.workarounds.portal.Portal;
import in.workarounds.portal.PortalAdapter;
import in.workarounds.portal.PortalService;

/**
 * Created by madki on 28/12/15.
 */
public class TestService extends PortalService {
    @Override
    protected PortalAdapter createPortalAdapter() {
        return new MyPortalAdapter(this);
    }

    @Override
    protected OverlayPermissionHelper createPermissionHelper() {
        return new OverlayPermissionHelper(this) {
            @Override
            protected String getAppName() {
                return context.getString(R.string.app_name);
            }

            @Override
            protected int getAccentColor() {
                return android.R.color.primary_text_dark;
            }

            @Override
            protected int getNotificationIcon() {
                return R.drawable.app_icon;
            }
        };
    }

    public static class MyPortalAdapter extends PortalAdapter<TestService> {

        public MyPortalAdapter(TestService service) {
            super(service);
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public void close(int portalId) {
            RefWatcher refWatcher = ExampleApplication.getRefWatcher(getContext());
            refWatcher.watch(getPortal(portalId));
            super.close(portalId);
        }

        @NonNull
        @Override
        protected Portal createPortal(int portalId) {
            switch (portalId) {
                case 0:
                    return new TestPortal(service, this);
                case 1:
                    return new TestMainPortal(service, this);
                default:
                    throw new IndexOutOfBoundsException("given portalId exceeds count");
            }
        }
    }
}
