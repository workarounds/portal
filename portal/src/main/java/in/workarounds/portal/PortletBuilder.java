package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;

/**
 * Created by madki on 18/09/15.
 */
public class PortletBuilder extends IntentBuilder {
    protected int id;

    public PortletBuilder(Context context) {
        super(context);
    }

    public <T extends Portlet> IntentBuilder type(Class<T> type) {
        this.type = type;
        return this;
    }

    public IntentBuilder id(int id) {
        if(!Portlet.isValidID(id)) {
            throw new IllegalArgumentException("Portlet id should be a positive integer");
        }
        this.id = id;
        return this;
    }

    @Override
    protected Intent intent() {
        Intent intent = super.intent();
        intent.putExtra(PortalManager.INTENT_KEY_PORTLET_ID, id);
        return intent;
    }

    @Override
    public void open() {
        this.intentType(PortalManager.INTENT_TYPE_OPEN_PORTLET).build();
    }

    @Override
    public void show() {
        this.intentType(PortalManager.INTENT_TYPE_SHOW_PORTLET).build();
    }

    @Override
    public void hide() {
        this.intentType(PortalManager.INTENT_TYPE_HIDE_PORTLET).build();
    }

    @Override
    public void close() {
        this.intentType(PortalManager.INTENT_TYPE_CLOSE_PORTLET).build();
    }
}
