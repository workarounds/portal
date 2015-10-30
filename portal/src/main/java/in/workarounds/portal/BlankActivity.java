package in.workarounds.portal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;

/**
 * Created by madki on 29/10/15.
 */
@Freighter
public class BlankActivity extends AppCompatActivity {
    private static final String TAG = "BlankActivity";
    private boolean firstTime;
    private boolean resultRecieved;

    @Cargo
    String className;
    @Cargo
    int requestCode;
    @Cargo
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        firstTime = true;
        resultRecieved = false;
        FreighterBlankActivity.inject(this);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if (!firstTime) {
            finish();
        } else {
            firstTime = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        if (!resultRecieved) {
            sendToService(requestCode, AbstractPortal.RESULT_DESTROYED, null);
        }
    }

    private void sendToService(int requestCode, int resultCode, Intent data) {
        try {
            Intent intent = new Intent(this, Class.forName(className));
            intent.putExtras(FreighterForegroundService.supply()
                            .requestCode(requestCode)
                            .resultCode(resultCode)
                            .activityResult(data)
                            .bundle()
            );
            startService(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resultRecieved = true;
        Log.d(TAG, "onActivityResult called");
        sendToService(requestCode, resultCode, data);
        finish();
    }

    @Override
    public void finish() {
        Log.d(TAG, "finish called");
        super.finish();
        overridePendingTransition(0, 0);
    }

}
