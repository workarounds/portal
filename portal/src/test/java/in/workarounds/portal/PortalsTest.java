package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * Created by madki on 28/12/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PortalsTest {
    @Mock
    Context context;
    @Mock
    Bundle data;

    @Test
    public void getIntent() {
        Intent intent = Portals.getIntent(context, PortalService.class);
        Intent expected = new Intent(context, PortalService.class);
        assertTrue(expected.filterEquals(intent));
    }

    @Test
    public void openIntent() {
        Intent intent = Portals.openIntent(1, data, context, PortalService.class);
        Intent expected = new Intent(context, PortalService.class)
                .putExtra(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.OPEN)
                .putExtra(PortalCommandsBundler.Keys.PORTAL_ID, 1)
                .putExtra(PortalCommandsBundler.Keys.DATA, data);
        assertTrue(expected.filterEquals(intent));
    }

}
