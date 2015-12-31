package in.workarounds.portal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.OptionsForBundler;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.Required;

import static in.workarounds.portal.IntentType.CLOSE;
import static in.workarounds.portal.IntentType.CLOSE_MANAGER;
import static in.workarounds.portal.IntentType.HIDE;
import static in.workarounds.portal.IntentType.NO_TYPE;
import static in.workarounds.portal.IntentType.OPEN;
import static in.workarounds.portal.IntentType.SEND;
import static in.workarounds.portal.IntentType.SEND_TO_ALL;
import static in.workarounds.portal.IntentType.SHOW;

/**
 * Created by madki on 28/11/15.
 */
@OptionsForBundler(packageName = "in.workarounds.portal")
@RequireBundler(requireAll = false)
class PortalCommands implements IntentResolver {
    private static final String TAG = "PortalCommands";
    @Arg @Required
    int intentType = NO_TYPE;
    @Arg
    int portalId = -1;
    @Arg
    Bundle data;

    private PortalAdapter portalAdapter;

    public PortalCommands(PortalAdapter portalAdapter) {
        this.portalAdapter = portalAdapter;
    }

    /**
     * handles the intent, checks the intentType of the intent
     * and performs the relevant action using portalManager
     * @param intent intent to be resolve.
     * @return true if the intent is handled
     */
    @Override
    public boolean handleCommand(@Nullable Intent intent) {
        if(intent == null) return false;

        resetFields();
        // injects fields annotated with @Arg
        Bundler.inject(this, intent.getExtras());

        switch (intentType) {
            case OPEN:
                portalAdapter.open(portalId, data);
                return true;
            case CLOSE:
                portalAdapter.close(portalId);
                return true;
            case SEND:
                portalAdapter.send(portalId, data);
                return true;
            case HIDE:
                portalAdapter.hide(portalId);
                return true;
            case SHOW:
                portalAdapter.show(portalId);
                return true;
            case CLOSE_MANAGER:
                portalAdapter.closeManager();
                return true;
            case SEND_TO_ALL:
                portalAdapter.sendToAll(data);
                return true;
        }

        return false;
    }

    /**
     * Resets the global variables. Call this before using Bundler.inject()
     * This will reset the field values to correct defaults or the values from
     * the last resolveIntent() might still persist.
     */
    private void resetFields() {
        intentType = NO_TYPE;
        portalId = -1;
        data = Bundle.EMPTY;
    }

    public static Bundle open(int portalId, Bundle data) {
        return Bundler.portalCommands(OPEN).portalId(portalId).data(data).bundle();
    }

    public static Bundle close(int portalId) {
        return Bundler.portalCommands(CLOSE).portalId(portalId).bundle();
    }

    public static Bundle show(int portalId) {
        return Bundler.portalCommands(SHOW).portalId(portalId).bundle();
    }

    public static Bundle hide(int portalId) {
        return Bundler.portalCommands(HIDE).portalId(portalId).bundle();
    }

    public static Bundle send(int portalId, Bundle data) {
        return Bundler.portalCommands(SEND).portalId(portalId).data(data).bundle();
    }

    public static Bundle closeManager() {
        return Bundler.portalCommands(CLOSE_MANAGER).bundle();
    }

    public static Bundle sendToAll(@NonNull Bundle data) {
        return Bundler.portalCommands(SEND_TO_ALL).data(data).bundle();
    }


}
