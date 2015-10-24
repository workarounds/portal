package in.workarounds.portal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by manidesto on 24/10/15.
 */
class PortalState {
    private static final String PORTAL_STATES_PREFS = "portal_states_prefs";

    private static PortalState INSTANCE;

    private SharedPreferences sharedPreferences;

    @NonNull
    public static PortalState getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new PortalState(context);
        }
        return INSTANCE;
    }

    private PortalState(Context context){
        sharedPreferences = context.getSharedPreferences(
                PORTAL_STATES_PREFS, Context.MODE_PRIVATE
        );
    }

    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }

    public void clear(){
        sharedPreferences.edit().clear().apply();
    }

    public void setState(Class<? extends Portal> type, @State.STATE int state){
        sharedPreferences.edit()
                .putInt(type.getCanonicalName(), state)
                .apply();
    }

    public @State.STATE int getState(Class<? extends Portal> type){
        int state = sharedPreferences.getInt(type.getCanonicalName(), State.CLOSED);
        switch (state){
            case State.ACTIVE:
                return State.ACTIVE;
            case State.CLOSED:
                return State.CLOSED;
            case State.HIDDEN:
                return State.HIDDEN;
            default:
                return State.CLOSED;
        }
    }
}
