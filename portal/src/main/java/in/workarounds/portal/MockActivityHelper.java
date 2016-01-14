package in.workarounds.portal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.Required;

import static in.workarounds.portal.IntentType.ACTIVITY_RESULT;
import static in.workarounds.portal.IntentType.NO_TYPE;
import static in.workarounds.portal.IntentType.START_ACTIVITY_FOR_RESULT;

/**
 * Helper class that is useful for implementing {@link MockActivity}. Instantiate this class and use
 * it's {@link #handleCommand(Intent)} and {@link #startActivityForResult(Intent, int)} in the
 * {@link android.app.Service#onStartCommand(Intent, int, int)}
 * and {@link MockActivity#startActivityForResult(Intent, int)} respectively. The helper would
 * open {@link BlankActivity} which in turn would open the desired activity for result and then
 * parse the returned intent and calls {@link MockActivity#onActivityResult(int, int, Intent)}.
 * This class uses a library to construct and parse intents,
 * see <a href="https://github.com/workarounds/bundler">Bundler</a>
 */
@RequireBundler(requireAll = false)
public class MockActivityHelper implements IntentResolver {
    /**
     * log tag
     */
    private static final String TAG = "MockActivityHelper";
    /**
     * fields to parse intent extras and create {@link MockActivityHelperBundler}
     */
    @Arg
    @Required
    int intentType;
    @Arg
    @Required
    int requestCode;
    @Arg
    int resultCode;
    @Arg
    Intent activityResult;
    @Arg
    Intent activityIntent;

    /**
     * The service/context object that is using this helper. The helper calls the {@link MockActivity#startActivity(Intent)}
     * and {@link MockActivity#onActivityResult(int, int, Intent)} functions
     */
    private MockActivity mockActivity;

    public MockActivityHelper(MockActivity mockActivity) {
        this.mockActivity = mockActivity;
    }

    /**
     * handles the intent if it's of type {@link IntentType#ACTIVITY_RESULT} or
     * {@link IntentType#START_ACTIVITY_FOR_RESULT}
     * @param intent the intent to be handled
     * @return true if intent is handled
     */
    @Override
    public boolean handleCommand(Intent intent) {
        if (intent == null) return false;

        resetFields();
        Bundler.inject(this, intent.getExtras());

        if (intentType == ACTIVITY_RESULT) {
            mockActivity.onActivityResult(requestCode, resultCode, activityResult);
            return true;
        } else if (intentType == START_ACTIVITY_FOR_RESULT) {
            if (requestCode == -1) {
                Log.e("MockActivityHelper", "requestId is not provided for startActivityForResult using default -1");
            }
            mockActivity.startActivityForResult(activityIntent, requestCode);
            return true;
        }

        return false;
    }

    /**
     * Opens {@link BlankActivity} and passes it the intent to open an Activity for result.
     * @param intent intent of the activity to be opened
     * @param requestCode request code of the request
     */
    public void startActivityForResult(Intent intent, int requestCode) {
        Intent blankActivityIntent = Bundler.blankActivity(
                mockActivity.getClass().getName(),
                requestCode,
                intent
        ).intent(mockActivity.getContext());

        mockActivity.startActivity(blankActivityIntent);
    }

    private void resetFields() {
        intentType = NO_TYPE;
        requestCode = -1;
        resultCode = -10;
        activityResult = null;
        activityIntent = null;
    }

    public static Bundle deliverActivityResult(int requestCode, int resultCode, Intent activityResult) {
        return Bundler.mockActivityHelper(ACTIVITY_RESULT, requestCode)
                .resultCode(resultCode).activityResult(activityResult)
                .bundle();
    }

    public static Bundle startActivityForResultBundle(Intent activityIntent, int requestCode) {
        return Bundler.mockActivityHelper(START_ACTIVITY_FOR_RESULT, requestCode)
                .activityIntent(activityIntent).bundle();
    }

}
