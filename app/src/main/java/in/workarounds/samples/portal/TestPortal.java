package in.workarounds.samples.portal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.workarounds.portal.Portal;

/**
 * Created by madki on 17/09/15.
 */
public class TestPortal extends Portal {
    private static final String TAG = "TestPortal";

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
}
