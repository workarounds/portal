package in.workarounds.portal.util;

import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by madki on 17/09/15.
 */
public class ParamUtils {

    public static void transferMarginAndGravity(WindowManager.LayoutParams wmParams, FrameLayout.LayoutParams flParams) {
        wmParams.gravity = flParams.gravity;
        wmParams.height = flParams.height;
        wmParams.width = flParams.width;
        int gravity = flParams.gravity;
        if(isRight(gravity)) {
            wmParams.x = flParams.rightMargin;
        } else {
            wmParams.x = flParams.leftMargin;
        }

        if(isBottom(gravity)) {
            wmParams.y = flParams.bottomMargin;
        } else {
            wmParams.y = flParams.topMargin;
        }
    }

    public static boolean isRight(int gravity) {
        // TODO include support for Gravity.START and Gravity.END
        return (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.RIGHT;
    }

    public static boolean isBottom(int gravity) {
        return (gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM;
    }

}
