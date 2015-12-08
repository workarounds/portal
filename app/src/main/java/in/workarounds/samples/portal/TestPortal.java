package in.workarounds.samples.portal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.workarounds.portal.Portal;

/**
 * Created by madki on 17/09/15.
 */
public class TestPortal extends Portal implements View.OnClickListener {
    private static final String TAG = "TestPortal";
    private static final int PICK_CONTACT_REQUEST = 1;

    @Bind(R.id.btn_open_portal)
    Button openButton;
    @Bind(R.id.btn_show_portal)
    Button showButton;
    @Bind(R.id.btn_hide_portal)
    Button hideButton;
    @Bind(R.id.btn_close_portal)
    Button closeButton;
    @Bind(R.id.btn_close_service)
    Button closeServiceButton;
    @Bind(R.id.btn_activity_for_result)
    Button activityForResultButton;

    public TestPortal(Context base) {
        super(base);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.portal_test);
        Log.d(TAG, "onCreate called");

        ButterKnife.bind(this, getView());
        ButtonListener listener = new ButtonListener(this);
        openButton.setOnClickListener(listener);
        showButton.setOnClickListener(listener);
        hideButton.setOnClickListener(listener);
        closeButton.setOnClickListener(listener);
        closeServiceButton.setOnClickListener(listener);

        activityForResultButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
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
    }

    @Override
    protected void onData(Bundle data) {
        super.onData(data);
        Toast.makeText(this, data.getString("key"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_for_result:
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            Log.d(TAG, "result received");
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "result ok");
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            } else if(resultCode == RESULT_CANCELLED) {
                Log.d(TAG, "result cancelled");
            } else  if(resultCode == RESULT_DESTROYED) {
                Log.d(TAG, "result destroyed");
            }
        }
    }
}
