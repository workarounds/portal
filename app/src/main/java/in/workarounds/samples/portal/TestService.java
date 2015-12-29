package in.workarounds.samples.portal;

import android.content.Intent;
import android.support.annotation.NonNull;

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
        return null;
    }

    @Override
    public void promptForPermission(Intent queuedIntent) {

    }

    public static class MyPortalAdapter extends PortalAdapter<TestService> {

        public MyPortalAdapter(TestService service) {
            super(service);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @NonNull
        @Override
        protected Portal createPortal(int portalId) {
            switch (portalId) {
                case 0:
                    return new TestPortal(service, this);
                case 1:
                    return new TestPortal(service, this);
                default:
                    throw new IndexOutOfBoundsException("given portalId exceeds count");
            }
        }
    }
}
