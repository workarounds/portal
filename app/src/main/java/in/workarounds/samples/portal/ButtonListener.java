package in.workarounds.samples.portal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import in.workarounds.portal.Portals;

/**
 * Created by madki on 17/09/15.
 */
public class ButtonListener implements View.OnClickListener {
    private static final String TAG = "ButtonListener";
    private Context context;
    private EditText editText;
    private static final int id = 1;

    public ButtonListener(Context context) {
        this.context = context;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    private int getIdFromET() {
        return Integer.parseInt(editText.getText().toString());
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        Bundle bundle = new Bundle();
        bundle.putString("key", "hi");
        switch (v.getId()) {
            case R.id.btn_open_portal:
                Portals.open(id, bundle, context, TestService.class);
                break;
            case R.id.btn_show_portal:
                Portals.show(id, context, TestService.class);
                break;
            case R.id.btn_hide_portal:
                Portals.hide(id, context, TestService.class);
                break;
            case R.id.btn_close_portal:
                Portals.close(id, context, TestService.class);
                break;
            case R.id.btn_send_portal:
                Portals.send(id, bundle, context, TestService.class);
                break;
            case R.id.btn_close_service:
                Portals.closeManager(context, TestService.class);
                break;
            case R.id.btn_send_to_all:
                Portals.sendToAll(bundle, context, TestService.class);
        }
    }
}
