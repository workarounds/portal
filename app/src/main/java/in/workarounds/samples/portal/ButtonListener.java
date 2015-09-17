package in.workarounds.samples.portal;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import in.workarounds.portal.PortalManager;

/**
 * Created by madki on 17/09/15.
 */
public class ButtonListener implements View.OnClickListener {
    private Context context;
    private EditText editText;

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
        switch (v.getId()) {
            case R.id.btn_open_portal:
                PortalManager.openPortal(context, TestPortal.class, new Bundle());
                break;
            case R.id.btn_show_portal:
                PortalManager.showPortal(context);
                break;
            case R.id.btn_hide_portal:
                PortalManager.hidePortal(context);
                break;
            case R.id.btn_close_portal:
                PortalManager.closePortal(context);
                break;
            case R.id.btn_open_portlet:
                PortalManager.openPortlet(context, TestPortlet.class, getIdFromET(), new Bundle());
                break;
            case R.id.btn_show_portlet:
                PortalManager.showPortlet(context, getIdFromET());
                break;
            case R.id.btn_hide_portlet:
                PortalManager.hidePortlet(context, getIdFromET());
                break;
            case R.id.btn_close_portlet:
                PortalManager.closePortlet(context, getIdFromET());
                break;
            case R.id.btn_close_service:
                PortalManager.closeManager(context);
                break;
        }
    }
}
