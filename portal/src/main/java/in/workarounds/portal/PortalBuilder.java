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
    public void open() {
        this.intentType(PortalManager.INTENT_TYPE_OPEN_PORTAL).start();
    }

    @Override
    public void show() {
        this.intentType(PortalManager.INTENT_TYPE_SHOW_PORTAL).start();
    }

    @Override
    public void hide() {
        this.intentType(PortalManager.INTENT_TYPE_HIDE_PORTAL).start();
    }

    @Override
    public void close() {
        this.intentType(PortalManager.INTENT_TYPE_CLOSE_PORTAL).start();
    }

    @Override
    public void send() {
        this.intentType(PortalManager.INTENT_TYPE_PORTAL_DATA).start();
    }
}
