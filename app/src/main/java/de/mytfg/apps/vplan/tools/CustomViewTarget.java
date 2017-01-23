package de.mytfg.apps.vplan.tools;


import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

/**
 * Used to support offsets
 */

public class CustomViewTarget implements Target {

    private final View mView;
    private int offsetX;
    private int offsetY;
    private float percX;
    private float percY;
    private boolean perc;

    public CustomViewTarget(View view) {
        mView = view;
    }

    public CustomViewTarget(int viewId, int offsetX, int offsetY, Activity activity) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        mView = activity.findViewById(viewId);
    }

    public CustomViewTarget(View view, float percX, float percY, Activity activity) {
        mView = view;
        this.percX = percX;
        this.percY = percY;
        perc = true;
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        if (perc) {
            int midX = mView.getWidth() / 2;
            int midY = mView.getHeight() / 2;
            int posX = (int) (percX * mView.getWidth());
            int posY = (int) (percY * mView.getHeight());
            this.offsetX = posX - midX;
            this.offsetY = posY - midY;
            Log.d("MX", "" + midX);
            Log.d("OX", "" + offsetX);
            Log.d("PX", "" + percX);
        }
        int x = location[0] + mView.getWidth() / 2 + offsetX;
        int y = location[1] + mView.getHeight() / 2 + offsetY;
        return new Point(x, y);
    }
}
