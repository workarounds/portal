package in.workarounds.portal;

import android.app.Activity;
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
    private static final int TEST_REQUEST_CODE = 2;

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

    // Test cases for openIntent() method
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

    // Test cases for sendIntent() method
    private Intent getSendIntent() {
        return Portals.sendIntent(TEST_PORTAL_ID, null, context, TEST_SERVICE_CLASS);
    }

    @Test
    public void sendIntent_setServiceClass() {
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(getSendIntent()));
    }

    @Test
    public void sendIntent_setIntentType() {
        assertEquals(IntentType.SEND, getSendIntent().getIntExtra(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    @Test
    public void sendIntent_setPortalId() {
        assertEquals(TEST_PORTAL_ID, getSendIntent().getIntExtra(PortalCommandsBundler.Keys.PORTAL_ID, -1));
    }

    @Test
    public void sendIntent_setNullData() {
        assertFalse(getSendIntent().hasExtra(PortalCommandsBundler.Keys.DATA));
    }

    @Test
    public void sendIntent_setNonNullData() {
        Intent intent = Portals.sendIntent(TEST_PORTAL_ID, DATA, context, TEST_SERVICE_CLASS);
        Bundle result = intent.getBundleExtra(PortalCommandsBundler.Keys.DATA);
        assertEquals(TEST_STRING, result.getString(TEST_KEY));
    }

    // Test cases for showIntent() method
    private Intent getShowIntent() {
        return Portals.showIntent(TEST_PORTAL_ID, context, TEST_SERVICE_CLASS);
    }

    @Test
    public void showIntent_setServiceClass() {
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(getShowIntent()));
    }

    @Test
    public void showIntent_setIntentType() {
        assertEquals(IntentType.SHOW, getShowIntent().getIntExtra(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    @Test
    public void showIntent_setPortalId() {
        assertEquals(TEST_PORTAL_ID, getShowIntent().getIntExtra(PortalCommandsBundler.Keys.PORTAL_ID, -1));
    }

    // Test cases for hideIntent() method
    private Intent getHideIntent() {
        return Portals.hideIntent(TEST_PORTAL_ID, context, TEST_SERVICE_CLASS);
    }

    @Test
    public void hideIntent_setServiceClass() {
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(getHideIntent()));
    }

    @Test
    public void hideIntent_setIntentType() {
        assertEquals(IntentType.HIDE, getHideIntent().getIntExtra(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    @Test
    public void hideIntent_setPortalId() {
        assertEquals(TEST_PORTAL_ID, getHideIntent().getIntExtra(PortalCommandsBundler.Keys.PORTAL_ID, -1));
    }

    // Test cases for closeIntent() method
    private Intent getCloseIntent() {
        return Portals.closeIntent(TEST_PORTAL_ID, context, TEST_SERVICE_CLASS);
    }

    @Test
    public void closeIntent_setServiceClass() {
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(getCloseIntent()));
    }

    @Test
    public void closeIntent_setIntentType() {
        assertEquals(IntentType.CLOSE, getCloseIntent().getIntExtra(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    @Test
    public void closeIntent_setPortalId() {
        assertEquals(TEST_PORTAL_ID, getCloseIntent().getIntExtra(PortalCommandsBundler.Keys.PORTAL_ID, -1));
    }

    // Test cases for closeManagerIntent() method
    private Intent getCloseManagerIntent() {
        return Portals.closeManagerIntent(context, TEST_SERVICE_CLASS);
    }

    @Test
    public void closeManagerIntent_setServiceClass() {
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(getCloseManagerIntent()));
    }

    @Test
    public void closeManagerIntent_setIntentType() {
        assertEquals(IntentType.CLOSE_MANAGER, getCloseManagerIntent().getIntExtra(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    // Test cases for sendToAllIntent() method
    private Intent getSendToAllIntent() {
        return Portals.sendToAllIntent(DATA, context, TEST_SERVICE_CLASS);
    }

    @Test
    public void sendToAllIntent_setServiceClass() {
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(getSendToAllIntent()));
    }

    @Test
    public void sendToAllIntent_setIntentType() {
        assertEquals(IntentType.SEND_TO_ALL, getSendToAllIntent().getIntExtra(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    @Test
    public void sendToAllIntent_setNonNullData() {
        Bundle result = getSendToAllIntent().getBundleExtra(PortalCommandsBundler.Keys.DATA);
        assertEquals(TEST_STRING, result.getString(TEST_KEY));
    }

    // Test cases for startActivityForResultIntent() method
    private Intent testIntent() {
        Intent intent = new Intent(context, Activity.class);
        intent.putExtra(TEST_KEY, TEST_STRING);
        return intent;
    }

    private Intent getStartActivityForResultIntent() {
        return Portals.startActivityForResultIntent(testIntent(), TEST_REQUEST_CODE, context, TEST_SERVICE_CLASS);
    }

    @Test
    public void startActivityForResultIntent_setServiceClass() {
        Intent expected = new Intent(context, TEST_SERVICE_CLASS);
        assertTrue(expected.filterEquals(getStartActivityForResultIntent()));
    }

    @Test
    public void startActivityForResultIntent_setIntentType() {
        assertEquals(IntentType.START_ACTIVITY_FOR_RESULT, getStartActivityForResultIntent().getIntExtra(MockActivityHelperBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    @Test
    public void startActivityForResultIntent_setRequestCode() {
        assertEquals(TEST_REQUEST_CODE, getStartActivityForResultIntent().getIntExtra(MockActivityHelperBundler.Keys.REQUEST_CODE, -1));
    }

    @Test
    public void startActivityForResultIntent_setActivityIntent() {
        assertEquals(testIntent(), getStartActivityForResultIntent().getParcelableExtra(MockActivityHelperBundler.Keys.ACTIVITY_INTENT));
    }

}
