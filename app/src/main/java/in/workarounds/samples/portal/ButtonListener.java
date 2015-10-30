package in.workarounds.samples.portal;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import in.workarounds.portal.Portal;
import in.workarounds.portal.PortalManager;
import in.workarounds.portal.Portlet;
import in.workarounds.portal.State;

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
        Bundle bundle = new Bundle();
        bundle.putString("key", "hi");
        switch (v.getId()) {
            case R.id.btn_open_portal:
                Portal.with(context).open(TestPortal.class);
                break;
            case R.id.btn_show_portal:
                Portal.with(context).show();
                break;
            case R.id.btn_hide_portal:
                Portal.with(context).hide();
                break;
            case R.id.btn_close_portal:
                Portal.with(context).close();
                break;
            case R.id.btn_send_portal:
                Portal.with(context).data(bundle).send(TestPortal.class);
                break;
            case R.id.btn_send_if_portal_open:
                int state = PortalManager.getPortalState(context, TestPortal.class);
                if(state != State.CLOSED){
                    Portal.with(context).data(bundle).send(TestPortal.class);
                }
                break;
            case R.id.btn_open_portlet:
                Portlet.with(context).id(getIdFromET()).open(TestPortlet.class);
                break;
            case R.id.btn_show_portlet:
                Portlet.with(context).id(getIdFromET()).show();
                break;
            case R.id.btn_hide_portlet:
                Portlet.with(context).id(getIdFromET()).hide();
                break;
            case R.id.btn_close_portlet:
                Portlet.with(context).id(getIdFromET()).close();
                break;
            case R.id.btn_send_portlet:
                Portlet.with(context).id(getIdFromET()).data(bundle).send(TestPortlet.class);
                break;
            case R.id.btn_close_service:
                PortalManager.close(context);
                break;
            case R.id.btn_send_to_all:
                PortalManager.send(context, bundle);
        }
    }
}
