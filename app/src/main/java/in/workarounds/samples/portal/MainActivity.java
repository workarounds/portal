package in.workarounds.samples.portal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.et_portlet_id)
    EditText editText;

    ButtonListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        listener = new ButtonListener(this);
        listener.setEditText(editText);
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

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @OnClick({
            R.id.btn_open_portal,
            R.id.btn_show_portal,
            R.id.btn_hide_portal,
            R.id.btn_close_portal,
            R.id.btn_send_portal,
            R.id.btn_send_if_portal_open,
            R.id.btn_open_portlet,
            R.id.btn_show_portlet,
            R.id.btn_hide_portlet,
            R.id.btn_close_portlet,
            R.id.btn_send_portlet,
            R.id.btn_send_to_all,
            R.id.btn_close_service
    })
    public void onButtonClicked(View v){
        listener.onClick(v);
    }
}
