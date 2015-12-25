package in.workarounds.portal.activity;

import android.content.Intent;
import android.os.Bundle;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.portal.Bundler;
import in.workarounds.portal.IntentResolver;

import static in.workarounds.portal.IntentType.ACTIVITY_RESULT;
import static in.workarounds.portal.IntentType.NO_TYPE;

/**
 * Created by madki on 29/11/15.
 */
@RequireBundler
public class MockActivityHelper implements IntentResolver {
    @Arg
    int intentType;
    @Arg
    int requestCode;
    @Arg
    int resultCode;
    @Arg
    Intent activityResult;

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
        }

        return false;
    }

    public void startActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mockActivity.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        Intent blankActivityIntent = Bundler.blankActivity(
                mockActivity.getClass().getName(),
                requestCode,
                intent
        ).intent(mockActivity.getContext());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mockActivity.startActivity(blankActivityIntent);
    }

    private void resetFields() {
        intentType = NO_TYPE;
        requestCode = -1;
        resultCode = -10;
        activityResult = null;
    }

    public static Bundle activityResult(int requestCode, int resultCode, Intent activityResult) {
        return Bundler.mockActivityHelper(ACTIVITY_RESULT, requestCode, resultCode, activityResult)
                .bundle();
    }

}
