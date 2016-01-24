package in.workarounds.portal;

import android.content.Intent;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Created by madki on 24/01/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PortalCommandsTests {
    @Mock
    PortalAdapter portalAdapter;

    PortalCommands portalCommands;

    static Bundle DATA;

    private static final int TEST_PORTAL_ID = 1;
    private static final String TEST_KEY = "key";
    private static final String TEST_STRING = "string";


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DATA = new Bundle();
        DATA.putString(TEST_KEY, TEST_STRING);
        portalCommands = new PortalCommands(portalAdapter);
    }

    // testing static methods
    // PortalCommands.open()
    @Test
    public void open_testIntentType() {
        Bundle result = PortalCommands.open(TEST_PORTAL_ID, DATA);
        assertIntentType(IntentType.OPEN, result);
    }

    @Test
    public void open_testPortalId() {
        Bundle result = PortalCommands.open(TEST_PORTAL_ID, DATA);
        assertPortalId(TEST_PORTAL_ID, result);
    }

    @Test
    public void open_testNonNullData() {
        Bundle result = PortalCommands.open(TEST_PORTAL_ID, DATA);
        assertNonNullData(DATA, result);
    }

    @Test
    public void open_testNullData() {
        Bundle result = PortalCommands.open(TEST_PORTAL_ID, null);
        assertNullData(result);
    }

    // PortalCommands.send()
    @Test
    public void send_testIntentType() {
        Bundle result = PortalCommands.send(TEST_PORTAL_ID, DATA);
        assertIntentType(IntentType.SEND, result);
    }

    @Test
    public void send_testPortalId() {
        Bundle result = PortalCommands.send(TEST_PORTAL_ID, DATA);
        assertPortalId(TEST_PORTAL_ID, result);
    }

    @Test
    public void send_testNonNullData() {
        Bundle result = PortalCommands.send(TEST_PORTAL_ID, DATA);
        assertNonNullData(DATA, result);
    }

    @Test
    public void send_testNullData() {
        Bundle result = PortalCommands.send(TEST_PORTAL_ID, null);
        assertNullData(result);
    }

    // PortalCommands.close()
    @Test
    public void close_testIntentType() {
        Bundle result = PortalCommands.close(TEST_PORTAL_ID);
        assertIntentType(IntentType.CLOSE, result);
    }

    @Test
    public void close_testPortalId() {
        Bundle result = PortalCommands.close(TEST_PORTAL_ID);
        assertPortalId(TEST_PORTAL_ID, result);
    }

    // PortalCommands.show()
    @Test
    public void show_testIntentType() {
        Bundle result = PortalCommands.show(TEST_PORTAL_ID);
        assertIntentType(IntentType.SHOW, result);
    }

    @Test
    public void show_testPortalId() {
        Bundle result = PortalCommands.show(TEST_PORTAL_ID);
        assertPortalId(TEST_PORTAL_ID, result);
    }

    // PortalCommands.hide()
    @Test
    public void hide_testIntentType() {
        Bundle result = PortalCommands.hide(TEST_PORTAL_ID);
        assertIntentType(IntentType.HIDE, result);
    }

    @Test
    public void hide_testPortalId() {
        Bundle result = PortalCommands.hide(TEST_PORTAL_ID);
        assertPortalId(TEST_PORTAL_ID, result);
    }

    // PortalCommands.closeManager()
    @Test
    public void closeManager_testIntentType() {
        Bundle result = PortalCommands.closeManager();
        assertIntentType(IntentType.CLOSE_MANAGER, result);
    }

    // PortalCommands.sendToAll()
    @Test
    public void sendToAll_testIntentType() {
        Bundle result = PortalCommands.sendToAll(DATA);
        assertIntentType(IntentType.SEND_TO_ALL, result);
    }

    @Test
    public void sendToAll_testNonNullData() {
        Bundle result = PortalCommands.sendToAll(DATA);
        assertNonNullData(DATA, result);
    }

    // TODO add test for sendToAllNullData

    // test instance methods
    @Test
    public void handleCommand_nullIntent() {
        assertFalse(portalCommands.handleCommand(null));
    }

    @Test
    public void handleCommand_IntentTypeNoType() {
        Bundle bundle = new Bundle();
        bundle.putInt(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE);
        assertFalse(portalCommands.handleCommand(getIntent(bundle)));
    }

    // handleCommand() with IntentType OPEN
    @Test
    public void handleCommand_openNonNullData() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.open(TEST_PORTAL_ID, DATA))
        );
        verify(portalAdapter).open(TEST_PORTAL_ID, DATA);
        assertTrue(result);
    }

    @Test
    public void handleCommand_openNullData() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.open(TEST_PORTAL_ID, null))
        );
        // TODO change Bundle.EMPTY to null
        verify(portalAdapter).open(TEST_PORTAL_ID, Bundle.EMPTY);
        assertTrue(result);
    }

    // handleCommand() with IntentType SEND
    @Test
    public void handleCommand_sendNonNullData() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.send(TEST_PORTAL_ID, DATA))
        );
        verify(portalAdapter).send(TEST_PORTAL_ID, DATA);
        assertTrue(result);
    }

    @Test
    public void handleCommand_sendNullData() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.send(TEST_PORTAL_ID, null))
        );
        // TODO change Bundle.EMPTY to null
        verify(portalAdapter).send(TEST_PORTAL_ID, Bundle.EMPTY);
        assertTrue(result);
    }

    // handleCommand() with IntentType CLOSE
    public void handleCommand_close() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.close(TEST_PORTAL_ID))
        );
        verify(portalAdapter).close(TEST_PORTAL_ID);
        assertTrue(result);
    }

    // handleCommand() with IntentType HIDE
    public void handleCommand_hide() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.hide(TEST_PORTAL_ID))
        );
        verify(portalAdapter).hide(TEST_PORTAL_ID);
        assertTrue(result);
    }

    // handleCommand() with IntentType SHOW
    public void handleCommand_show() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.show(TEST_PORTAL_ID))
        );
        verify(portalAdapter).show(TEST_PORTAL_ID);
        assertTrue(result);
    }

    // handleCommand() with IntentType SHOW
    public void handleCommand_closeManager() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.closeManager())
        );
        verify(portalAdapter).closeManager();
        assertTrue(result);
    }

    // handleCommand() with IntentType SHOW
    public void handleCommand_sendToAllNonNullData() {
        boolean result = portalCommands.handleCommand(
                getIntent(PortalCommands.sendToAll(DATA))
        );
        verify(portalAdapter).sendToAll(DATA);
        assertTrue(result);
    }

    // TODO add test for sendToAllNullData

    // helper methods
    private void assertPortalId(int id, Bundle result) {
        assertEquals(id, result.getInt(PortalCommandsBundler.Keys.PORTAL_ID, -1));
    }

    private void assertIntentType(int intentType, Bundle result) {
        assertEquals(intentType, result.getInt(PortalCommandsBundler.Keys.INTENT_TYPE, IntentType.NO_TYPE));
    }

    private void assertNonNullData(Bundle data, Bundle result) {
        assertEquals(data, result.getBundle(PortalCommandsBundler.Keys.DATA));
    }

    private void assertNullData(Bundle result) {
        assertFalse(result.containsKey(PortalCommandsBundler.Keys.DATA));
    }

    private Intent getIntent(Bundle extras) {
        return new Intent()
                .putExtras(extras);
    }
}
