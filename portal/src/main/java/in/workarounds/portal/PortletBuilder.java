package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by madki on 18/09/15.
 */
public class PortletBuilder extends IntentBuilder<PortletBuilder> {
    protected int id;

    public PortletBuilder(Context context) {
        super(context);
    }

    public <T extends Portlet> PortletBuilder type(Class<T> type) {
        this.type = type;
        return this;
    }

    @Override
    public PortletBuilder data(Bundle data) {
        this.data = data;
        return this;
    }

    @Override
    public <S extends PortalManager> PortletBuilder manager(Class<S> managerType) {
        this.manager = managerType;
        return this;
    }

    @Override
    protected PortletBuilder intentType(@PortalManager.INTENT_TYPE int intentType) {
        this.intentType = intentType;
        return this;
    }

    public PortletBuilder id(int id) {
        if(!Portlet.isValidID(id)) {
            throw new IllegalArgumentException("Portlet id should be a positive integer");
        }
        this.id = id;
        return this;
    }

    @Override
    public Intent intent() {
        Intent intent = super.intent();
        if(!Portlet.isValidID(id)) {
            throw new IllegalArgumentException("Portlet id invalid");
        }
        intent.putExtra(FreighterPortalManager.Keys.PORTLET_ID, id);
        return intent;
    }

    @Override
    protected PortletBuilder setOpenType(Class<? extends AbstractPortal> type){
        this.type = type;
        return intentType(PortalManager.IntentType.OPEN_PORTLET);
    }

    @Override
    protected PortletBuilder setShowType(){
        return intentType(PortalManager.IntentType.SHOW_PORTLET);
    }

    @Override
    protected PortletBuilder setHideType(){
        return intentType(PortalManager.IntentType.HIDE_PORTLET);
    }

    @Override
    protected PortletBuilder setCloseType(){
        return intentType(PortalManager.IntentType.CLOSE_PORTLET);
    }

    @Override
    protected PortletBuilder setSendType(Class<? extends AbstractPortal> type){
        this.type = type;
        return intentType(PortalManager.IntentType.SEND_PORTLET);
    }
}
