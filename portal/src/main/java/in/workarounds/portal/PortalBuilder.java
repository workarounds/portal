package in.workarounds.portal;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by madki on 18/09/15.
 */
public class PortalBuilder extends IntentBuilder<PortalBuilder> {

    public PortalBuilder(Context context) {
        super(context);
    }

    public <T extends Portal> PortalBuilder type(Class<T> type) {
        this.type = type;
        return this;
    }

    @Override
    public PortalBuilder data(Bundle data) {
        this.data = data;
        return this;
    }

    @Override
    public <S extends PortalManager> PortalBuilder manager(Class<S> managerType) {
        this.manager = managerType;
        return this;
    }

    @Override
    protected PortalBuilder intentType(@PortalManager.PM_INTENT_ID int intentType) {
        this.intentType = intentType;
        return this;
    }

    @Override
    protected PortalBuilder setOpenType(Class<? extends AbstractPortal> type){
        this.type = type;
        return intentType(PortalManager.INTENT_TYPE_OPEN_PORTAL);
    }

    @Override
    protected PortalBuilder setShowType(){
        return intentType(PortalManager.INTENT_TYPE_SHOW_PORTAL);
    }

    @Override
    protected PortalBuilder setHideType(){
        return intentType(PortalManager.INTENT_TYPE_HIDE_PORTAL);
    }

    @Override
    protected PortalBuilder setCloseType(){
        return intentType(PortalManager.INTENT_TYPE_CLOSE_PORTAL);
    }

    @Override
    protected PortalBuilder setSendType(Class<? extends AbstractPortal> type){
        this.type = type;
        return intentType(PortalManager.INTENT_TYPE_SEND_PORTAL);
    }
}
