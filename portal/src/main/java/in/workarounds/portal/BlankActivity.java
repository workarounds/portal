package in.workarounds.portal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;

/**
 * Created by madki on 29/10/15.
 */
@RequireBundler
public class BlankActivity extends AppCompatActivity {
    private static final String TAG = "BlankActivity";
    private boolean firstTime;
    private boolean resultReceived;

    @Arg
    String className;
    @Arg
    int requestCode;
    @Arg
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        firstTime = true;
        resultReceived = false;

        Bundler.inject(this);

        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (!firstTime) {
            finish();
        } else {
            firstTime = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        if (!resultReceived) {
            sendToService(requestCode, MockActivity.RESULT_DESTROYED, null);
        }
    }

    private void sendToService(int requestCode, int resultCode, Intent data) {
        try {
            Intent intent = new Intent(this, Class.forName(className));
            intent.putExtras(MockActivityHelper.deliverActivityResult(requestCode, resultCode, data));
            startService(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: requestCode: " + requestCode + ", resultCode " + resultCode);
        resultReceived = true;
        sendToService(requestCode, resultCode, data);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}
