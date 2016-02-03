package in.workarounds.samples.portal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import in.workarounds.portal.Portal;

/**
 * Created by madki on 17/09/15.
 */
public class TestPortal extends Portal<TestService.MyPortalAdapter> {
    private static final String TAG = "TestPortal";

    public TestPortal(TestService.MyPortalAdapter portalAdapter, int portalId) {
        super(portalAdapter, portalId);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        portalAdapter.open(TestService.TEST_MAIN_PORTAL, bundle);

        if (bundle != null) {
            Toast.makeText(this, bundle.getString("key") + " onCreate", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();

    }

    @Override
    protected boolean onData(@Nullable Bundle data) {
        portalAdapter.open(TestService.TEST_MAIN_PORTAL, data);
        if (data != null) {
            Toast.makeText(this, data.getString("key") + " onData", Toast.LENGTH_LONG).show();
        }
        return false;
    }

}
