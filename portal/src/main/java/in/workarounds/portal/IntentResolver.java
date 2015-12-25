package in.workarounds.portal;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by madki on 08/12/15.
 */
public interface IntentResolver {
    boolean handleCommand(@Nullable Intent intent);
}
