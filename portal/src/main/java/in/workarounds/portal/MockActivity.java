package in.workarounds.portal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Interface to be implemented by service/context objects that want to emulate the functionality of an
 * Activity. Use this along with {@link MockActivityHelper}. For an example see {@link PortalService}
 */
public interface MockActivity {
    /**
     * Similar usage as {@link Activity#RESULT_OK}
     */
    int RESULT_OK = Activity.RESULT_OK;
    /**
     * Similar usage as {@link Activity#RESULT_CANCELED}
     */
    int RESULT_CANCELLED = Activity.RESULT_CANCELED;
    /**
     * This is returned when the opened activity has been cleared from recent apps
     */
    int RESULT_DESTROYED = -2;

    /**
     * Similar to {@link Activity#startActivity(Intent)}
     * Note: The method {@link android.app.Service#startActivity(Intent)} needs to be modified to
     * start an activity. An additional flag {@link Intent#FLAG_ACTIVITY_NEW_TASK} needs to be added
     * See {@link PortalService#startActivity(Intent)} for more details.
     *
     * @param intent intent to start the activity
     */
    void startActivity(Intent intent);

    /**
     * Similar to {@link Activity#startActivityForResult(Intent, int, Bundle)}
     * Call {@link MockActivityHelper#startActivityForResult(Intent, int)} inside this method.
     * See {@link PortalService} for an example
     *
     * @param intent      intent of the activity
     * @param requestCode request code of the request
     */
    void startActivityForResult(Intent intent, int requestCode);

    /**
     * Similar to {@link Activity#onActivityResult(int, int, Intent)} except this returns a boolean
     * to indicate that the result has been handled.
     *
     * @param requestCode request code of the request
     * @param resultCode  result code one of {@link #RESULT_OK}, {@link #RESULT_CANCELLED},
     *                    {@link #RESULT_DESTROYED}
     * @param data        data returned by the activity
     * @return true if result handled
     */
    boolean onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * A convenience method to return the context
     * @return context
     */
    Context getContext();
}
