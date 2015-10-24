package in.workarounds.samples.portal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.workarounds.portal.Portlet;

/**
 * Created by madki on 17/09/15.
 */
public class TestPortlet extends Portlet {
    private static final String TAG = "TestPortlet";
    @Bind(R.id.tv_id)
    TextView textView;

    public TestPortlet(Context base, int id) {
        super(base, id);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.portlet_test);
        Toast.makeText(this, bundle.getString("key"), Toast.LENGTH_LONG).show();
        ButterKnife.bind(this, getView());
        textView.setText(Integer.toString(getId()));

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Portlet Clicked");
            }
        });
    }

    @Override
    protected void onData(Bundle data) {
        super.onData(data);
        Toast.makeText(this, data.getString("key"), Toast.LENGTH_LONG).show();
    }
}
