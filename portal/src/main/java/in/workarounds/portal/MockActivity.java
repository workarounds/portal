package in.workarounds.portal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by madki on 29/11/15.
 */
public interface MockActivity {
    int RESULT_OK = Activity.RESULT_OK;
    int RESULT_CANCELLED = Activity.RESULT_CANCELED;
    int RESULT_DESTROYED = -2;

    void startActivity(Intent intent);

    void startActivityForResult(Intent intent, int requestCode);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    Context getContext();
}
