package in.workarounds.samples.portal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by manidesto on 25/10/15.
 */
public class CastingView extends View {
    public CastingView(Context context) {
        super(context);
        testContext(context);
    }

    public CastingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        testContext(context);
    }

    public CastingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        testContext(context);
    }

    private void testContext(Context context){
        if(context instanceof TestPortal){
            Log.d("testing", "service is testportal");
        }
    }
}
