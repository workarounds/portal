package in.workarounds.samples.portal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.btn_open_portal)
    Button openButton;
    @Bind(R.id.btn_show_portal)
    Button showButton;
    @Bind(R.id.btn_hide_portal)
    Button hideButton;
    @Bind(R.id.btn_close_portal)
    Button closeButton;
    @Bind(R.id.btn_send_portal)
    Button sendButton;
    @Bind(R.id.et_portlet_id)
    EditText editText;
    @Bind(R.id.btn_open_portlet)
    Button openButtonPortlet;
    @Bind(R.id.btn_show_portlet)
    Button showButtonPortlet;
    @Bind(R.id.btn_hide_portlet)
    Button hideButtonPortlet;
    @Bind(R.id.btn_close_portlet)
    Button closeButtonPortlet;
    @Bind(R.id.btn_send_portlet)
    Button sendButtonPortlet;
    @Bind(R.id.btn_send_to_all)
    Button sendToAllButton;
    @Bind(R.id.btn_close_service)
    Button closeServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButtonListener listener = new ButtonListener(this);
        listener.setEditText(editText);

        openButton.setOnClickListener(listener);
        showButton.setOnClickListener(listener);
        hideButton.setOnClickListener(listener);
        closeButton.setOnClickListener(listener);
        sendButton.setOnClickListener(listener);
        openButtonPortlet.setOnClickListener(listener);
        showButtonPortlet.setOnClickListener(listener);
        hideButtonPortlet.setOnClickListener(listener);
        closeButtonPortlet.setOnClickListener(listener);
        sendButtonPortlet.setOnClickListener(listener);
        sendToAllButton.setOnClickListener(listener);
        closeServiceButton.setOnClickListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
