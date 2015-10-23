package in.workarounds.portal;

import android.support.annotation.IntDef;

/**
 * Created by manidesto on 23/10/15.
 */
public interface State {
    int ACTIVE = 0;
    int HIDDEN = 1;
    int CLOSED = 2;

    @IntDef({ACTIVE, HIDDEN, CLOSED})
    @interface STATE {
    }
}
