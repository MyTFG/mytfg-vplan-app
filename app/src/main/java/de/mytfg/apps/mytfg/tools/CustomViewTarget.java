package de.mytfg.apps.mytfg.tools;


import android.app.Activity;
import android.graphics.Point;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

/**
 * Used to support offsets
 */

public class CustomViewTarget implements Target {
    public enum Type {
        ABS_B_R, // Pos from bottom right
        ABS_B_L, // Pos from bottom left
        ABS_T_R, // Pos from top right
        ABS_T_L, // Pos from top left
        ABS_MID_L, // Pos from Mid Left
        ABS_MID_R, // Pos from Mid right
        ABS_MID_T, // Pos from Mid top
        ABS_MID_B, // Pos from Mid bottom
        ABS_MID, // Pos from mid
        PERC     // Pos from top left (perc)

    }


    private final View mView;
    private Type type = Type.ABS_MID;
    private int xval;
    private int yval;
    private double percX;
    private double percY;

    private int offsetX;
    private int offsetY;

    public CustomViewTarget(View view) {
        mView = view;
    }

    public CustomViewTarget(int viewId, int xval, int yval, Activity activity, Type type) {
        this(activity.findViewById(viewId), xval, yval, type);
    }

    public CustomViewTarget(View view, int xval, int yval, Type type) {
        mView = view;
        this.xval = xval;
        this.yval = yval;
        this.type = type;
    }

    public CustomViewTarget(View view, double percX, double percY) {
        mView = view;
        this.percX = percX;
        this.percY = percY;
        this.type = Type.PERC;
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        // Position of the (corner of the) view
        int posX = location[0];
        int posY = location[1];

        int x;
        int y;
        int midX = mView.getWidth() / 2;
        int midY = mView.getHeight() / 2;

        switch (type) {
            case PERC:
                x = posX + (int) (percX * mView.getWidth());
                y = posY + (int) (percY * mView.getHeight());
                break;
            case ABS_MID:
                x = posX + midX + xval;
                y = posY + midY + yval;
                break;
            case ABS_T_L:
                x = posX + xval;
                y = posY + yval;
                break;
            case ABS_B_L:
                x = posX + xval;
                y = posY + mView.getHeight() - yval;
                break;
            case ABS_B_R:
                x = posX + mView.getWidth() - xval;
                y = posY + mView.getHeight() - yval;
                break;
            case ABS_T_R:
                x = posX + mView.getWidth() - xval;
                y = posY + yval;
                break;
            case ABS_MID_B:
                x = posX + midX;
                y = posY + mView.getHeight() - yval;
                break;
            case ABS_MID_T:
                x = posX + midX;
                y = posY + yval;
                break;
            case ABS_MID_L:
                x = posX + xval;
                y = posY + midY;
                break;
            case ABS_MID_R:
                x = posX + mView.getWidth() - xval;
                y = posY + midY;
                break;
            default:
                x = midX;
                y = midY;
                break;
        }

        return new Point(x, y);
    }
}
