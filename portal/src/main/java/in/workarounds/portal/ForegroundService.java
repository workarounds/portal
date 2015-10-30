package in.workarounds.portal;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;

/**
 * Created by madki on 29/10/15.
 */
@Freighter
public abstract class ForegroundService extends Service {
    @Cargo
    int requestCode;
    @Cargo
    int resultCode;
    @Cargo
    Intent activityResult;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FreighterForegroundService.Retriever retriever = FreighterForegroundService.retrieve(intent);
        if(retriever.hasRequestCode() && retriever.hasRequestCode()) {
            onActivityResult(retriever.requestCode(-1), retriever.resultCode(-1), retriever.activityResult());
        }
        return super.onStartCommand(intent, flags, startId);
    }

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
        if (intents != null) {
            for (Intent intent : intents) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        super.startActivities(intents);
    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        if (intents != null) {
            for (Intent intent : intents) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        super.startActivities(intents, options);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        Intent blankActivity = FreighterBlankActivity.supply()
                .className(getClass().getName())
                .requestCode(requestCode)
                .intent(intent)
                .intent(this);
        startActivity(blankActivity);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
