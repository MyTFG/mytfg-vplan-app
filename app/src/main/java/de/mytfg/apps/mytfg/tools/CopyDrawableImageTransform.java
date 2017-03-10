package de.mytfg.apps.mytfg.tools;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.ChangeImageTransform;
import android.transition.TransitionValues;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CopyDrawableImageTransform extends ChangeImageTransform {
    private Drawable drawable;

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        if (transitionValues.view instanceof ImageView) {
            drawable = ((ImageView) transitionValues.view).getDrawable();
        }
        super.captureStartValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        if (transitionValues.view instanceof ImageView) {
            ImageView imageView = (ImageView) transitionValues.view;
            if (imageView.getDrawable() == null) {
                imageView.setImageDrawable(drawable);
            }
        }
        super.captureEndValues(transitionValues);
    }
}
