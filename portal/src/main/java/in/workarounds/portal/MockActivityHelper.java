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
 * Created by madki on 29/11/15.
 */
@RequireBundler(requireAll = false)
public class MockActivityHelper implements IntentResolver {
    private static final String TAG = "MockActivityHelper";
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

    private MockActivity mockActivity;

    public MockActivityHelper(MockActivity mockActivity) {
        this.mockActivity = mockActivity;
    }

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
