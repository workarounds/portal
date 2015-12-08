package in.workarounds.portal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
        firstTime = true;
        resultReceived = false;

        Bundler.inject(this);

        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstTime) {
            finish();
        } else {
            firstTime = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!resultReceived) {
            sendToService(requestCode, AbstractPortal.RESULT_DESTROYED, null);
        }
    }

    private void sendToService(int requestCode, int resultCode, Intent data) {
        try {
            Intent intent = new Intent(this, Class.forName(className));
            intent.putExtras(ActivityHelper.activityResult(requestCode, resultCode, data));
            startService(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
