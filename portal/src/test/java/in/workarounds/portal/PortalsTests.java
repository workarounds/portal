package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by madki on 28/12/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PortalsTests {
    @Mock
    Context context = RuntimeEnvironment.application.getApplicationContext();
    static Bundle DATA;

    private static final int TEST_PORTAL_ID = 1;
    private static final Class<?> TEST_SERVICE_CLASS = PortalService.class;
    private static final String TEST_KEY = "key";
    private static final String TEST_STRING = "string";

    @BeforeClass
    public static void setUp() {
        DATA = new Bundle();
        DATA.putString(TEST_KEY, TEST_STRING);
    }


    @Test
    public void getIntent() {
        Intent intent = Portals.getIntent(context, TEST_SERVICE_CLASS);
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(intent));
    }

    private Intent getOpenIntent() {
        return Portals.openIntent(TEST_PORTAL_ID, null, context, TEST_SERVICE_CLASS);
    }

    @Test
    public void openIntent_setServiceClass() {
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(getOpenIntent()));
    }

    @Test
    public void openIntent_setIntentType() {
        assertEquals(IntentType.OPEN, getOpenIntent().getIntExtra(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    @Test
    public void openIntent_setPortalId() {
        assertEquals(TEST_PORTAL_ID, getOpenIntent().getIntExtra(PortalCommandsBundler.Keys.PORTAL_ID, -1));
    }

    @Test
    public void openIntent_setNullData() {
        assertFalse(getOpenIntent().hasExtra(PortalCommandsBundler.Keys.DATA));
    }

    @Test
    public void openIntent_setNonNullData() {
        Intent intent = Portals.openIntent(TEST_PORTAL_ID, DATA, context, TEST_SERVICE_CLASS);
        Bundle result = intent.getBundleExtra(PortalCommandsBundler.Keys.DATA);
        assertEquals(TEST_STRING, result.getString(TEST_KEY));
    }

}
