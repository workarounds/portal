package in.workarounds.portal;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by madki on 29/10/15.
 */
public abstract class ForegroundService extends Service {

    @Override
    public void startActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent, options);
    }

    @Override
    public void startActivities(Intent[] intents) {
        if(intents != null) {
            for (Intent intent: intents) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        super.startActivities(intents);
    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {
         if(intents != null) {
            for (Intent intent: intents) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        super.startActivities(intents, options);
    }

    public void startActivityForResult() {

    }
}
