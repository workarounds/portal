package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by madki on 18/09/15.
 */
public abstract class IntentBuilder<T extends IntentBuilder> {
    private static final String TAG = "IntentBuilder";
    protected Context context;
    protected @PortalManager.PM_INTENT_ID int intentType;
    protected Class<? extends AbstractPortal> type;
    protected Bundle data;
    protected Class<? extends PortalManager> manager;

    public IntentBuilder(Context context) {
        this.context = context;
    }

    public abstract T data(Bundle data);

    public abstract  <S extends PortalManager> T manager(Class<S> managerType);

    protected abstract T intentType(@PortalManager.PM_INTENT_ID int intentType);

    public abstract void open(Class<? extends AbstractPortal> type);
    public abstract void show();
    public abstract void hide();
    public abstract void close();
    public abstract void send(Class<? extends AbstractPortal> type);


    public Intent intent() {
        if(requireType() && type == null) {
            throw new IllegalArgumentException("Must provide a type to build Portal/Portlet");
        }
        if(manager == null) {
            manager = PortalManager.class;
        }
        if(data == null) {
            data = new Bundle();
        }
        Intent intent = new Intent(context, manager);
        if(requireType()) {
            intent.putExtra(PortalManager.INTENT_KEY_CLASS, type.getName());
        }
        intent.putExtra(PortalManager.INTENT_KEY_DATA, data);
        intent.putExtra(PortalManager.INTENT_KEY_INTENT_TYPE, intentType);
        return intent;
    }

    protected void start() {
        context.startService(intent());
    }

    protected boolean requireType() {
        return (intentType == PortalManager.INTENT_TYPE_OPEN_PORTAL
                || intentType == PortalManager.INTENT_TYPE_OPEN_PORTLET
                || intentType == PortalManager.INTENT_TYPE_PORTAL_DATA
                || intentType == PortalManager.INTENT_TYPE_PORTLET_DATA
        );
    }

}
