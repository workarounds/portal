package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;

import static in.workarounds.portal.IntentType.ACTIVITY_RESULT;
import static in.workarounds.portal.IntentType.NO_TYPE;

/**
 * Created by madki on 29/11/15.
 */
@RequireBundler
public class ActivityHelper<T extends Context & ActivityResultListener> {
    @Arg
    int intentType;
    @Arg
    int requestCode;
    @Arg
    int resultCode;
    @Arg
    Intent activityResult;

    private T activityResultListener;

    public ActivityHelper(T activityResultListener) {
        this.activityResultListener = activityResultListener;
    }

    public boolean onStartCommand(Intent intent) {
        if (intent == null) return false;

        resetFields();
        Bundler.inject(this, intent.getExtras());

        if (intentType == ACTIVITY_RESULT) {
            activityResultListener.onActivityResult(requestCode, resultCode, activityResult);
            return true;
        }

        return false;
    }

    public void startActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityResultListener.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        Intent blankActivityIntent = Bundler.blankActivity(
                activityResultListener.getClass().getName(),
                requestCode,
                intent
        ).intent(activityResultListener);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activityResultListener.startActivity(blankActivityIntent);
    }

    private void resetFields() {
        intentType = NO_TYPE;
        requestCode = -1;
        resultCode = -10;
        activityResult = null;
    }

    public static Bundle activityResult(int requestCode, int resultCode, Intent activityResult) {
        return Bundler.activityStarterHelper(ACTIVITY_RESULT, requestCode, resultCode, activityResult)
                .bundle();
    }

}
