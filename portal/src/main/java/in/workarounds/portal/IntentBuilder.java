package in.workarounds.portal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by madki on 18/09/15.
 */
public abstract class IntentBuilder {
    private static final String TAG = "IntentBuilder";
    protected Context context;
    protected @PortalManager.PM_INTENT_ID int intentType;
    protected Class<? extends AbstractPortal> type;
    protected Bundle data;
    protected Class<? extends PortalManager> manager;

    public IntentBuilder(Context context) {
        this.context = context;
    }

    public IntentBuilder data(Bundle data) {
        this.data = data;
        return this;
    }

    public <S extends PortalManager> IntentBuilder manager(Class<S> managerType) {
        this.manager = managerType;
        return this;
    }

    protected IntentBuilder intentType(@PortalManager.PM_INTENT_ID int intentType) {
        this.intentType = intentType;
        return this;
    }

    public abstract void open();
    public abstract void show();
    public abstract void hide();
    public abstract void close();


    protected Intent intent() {
        if(type == null) {
            throw new IllegalArgumentException("Must provide a type to build Portal/Portlet");
        }
        if(manager == null) {
            manager = PortalManager.class;
        }
        Intent intent = new Intent(context, manager);
        intent.putExtra(PortalManager.INTENT_KEY_CLASS, type.getName());
        intent.putExtra(PortalManager.INTENT_KEY_DATA, data);
        intent.putExtra(PortalManager.INTENT_KEY_INTENT_TYPE, intentType);
        return intent;
    }

    protected void build() {
        context.startService(intent());
    }
}
