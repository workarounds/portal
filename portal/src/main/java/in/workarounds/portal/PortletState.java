package in.workarounds.portal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by manidesto on 24/10/15.
 */
class PortletState {
    private static final String PORTLET_STATES_PREFS = "portlet_states_prefs";

    private static PortletState INSTANCE;

    private SharedPreferences sharedPreferences;

    @NonNull
    public static PortletState getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new PortletState(context);
        }
        return INSTANCE;
    }

    private PortletState(Context context){
        sharedPreferences = context.getSharedPreferences(
                PORTLET_STATES_PREFS, Context.MODE_PRIVATE
        );
    }

    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }

    public void clear(){
        sharedPreferences.edit().clear().apply();
    }

    public void setState(int id, @State.STATE int state){
        sharedPreferences.edit()
                .putInt(String.valueOf(id), state)
                .apply();
    }

    public @State.STATE int getState(int id){
        int state = sharedPreferences.getInt(String.valueOf(id), State.CLOSED);
        switch (state){
            case State.ACTIVE:
                return State.ACTIVE;
            case State.HIDDEN:
                return State.HIDDEN;
            case State.CLOSED:
                return State.CLOSED;
            default:
                return State.CLOSED;
        }
    }
}

