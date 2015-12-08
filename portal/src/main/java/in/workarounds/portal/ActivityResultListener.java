package in.workarounds.portal;

import android.content.Intent;

/**
 * Created by madki on 29/11/15.
 */
public interface ActivityResultListener {
     void startActivityForResult(Intent intent, int requestCode);
     void onActivityResult(int requestCode, int resultCode, Intent data);
}
