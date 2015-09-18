package in.workarounds.portal;

import android.content.Context;

/**
 * Created by madki on 18/09/15.
 */
public class PortalBuilder extends IntentBuilder {

    public PortalBuilder(Context context) {
        super(context);
    }

    public <T extends Portal> IntentBuilder type(Class<T> type) {
        this.type = type;
        return this;
    }

    @Override
    public void open() {
        this.intentType(PortalManager.INTENT_TYPE_OPEN_PORTAL).build();
    }

    @Override
    public void show() {
        this.intentType(PortalManager.INTENT_TYPE_SHOW_PORTAL).build();
    }

    @Override
    public void hide() {
        this.intentType(PortalManager.INTENT_TYPE_HIDE_PORTAL).build();
    }

    @Override
    public void close() {
        this.intentType(PortalManager.INTENT_TYPE_CLOSE_PORTAL).build();
    }
}
