package in.workarounds.portal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.portal.activity.MockActivity;

/**
 * Created by madki on 12/12/15.
 */
@RequireBundler
public class CommandHelper implements IntentResolver {
    @Arg
    int intentType;
    @Arg
    Intent activityIntent;
    @Arg
    int requestCode;

    MockActivity mockActivity;

    public CommandHelper(MockActivity mockActivity) {
        this.mockActivity = mockActivity;
    }

    @Override
    public boolean handleCommand(@Nullable Intent intent) {
        if (intent == null) return false;

        resetFields();
        Bundler.inject(this, intent.getExtras());
        if (intentType == IntentType.START_ACTIVITY_FOR_RESULT) {
            if(requestCode == -1) {
                Log.e("CommandHelper", "requestId is not provided for startActivityForResult using default -1");
            }
            mockActivity.startActivityForResult(activityIntent, requestCode);
            return true;
        }

        return false;
    }

    protected void resetFields() {
        intentType = IntentType.NO_TYPE;
        requestCode = -1;
        activityIntent = null;
    }

    public static Bundle startActivityForResult(Intent activityIntent, int requestCode) {
        return Bundler.commandHelper(IntentType.START_ACTIVITY_FOR_RESULT, activityIntent, requestCode).bundle();
    }
}
