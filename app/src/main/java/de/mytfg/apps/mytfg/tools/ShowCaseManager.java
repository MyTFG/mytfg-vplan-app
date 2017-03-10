package de.mytfg.apps.mytfg.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.LinkedList;
import java.util.List;

import de.mytfg.apps.mytfg.R;

/**
 * Manages storage of settings for easy access
 */

public class ShowCaseManager {
    private Context context;
    private SharedPreferences preferences;
    private List<ChainElement> chain;
    private Fragment fragment;
    private static ShowcaseView showcaseView;

    private class ChainElement {
        private String title;
        private String text;
        private Target target;
        private Fragment fragment;
        private boolean buttonLeft;
    }

    public ShowCaseManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("showcase", Context.MODE_PRIVATE);
    }

    public void clear(Fragment fragment) {
        preferences.edit()
                .remove(fragment.getClass().toString())
                .apply();
    }

    public void setDone(Fragment fragment) {
        preferences.edit()
                .putBoolean(fragment.getClass().toString(), true)
                .apply();

    }

    public boolean isDone(Fragment fragment) {
        return preferences.getBoolean(fragment.getClass().toString(), false);
    }

    public void show(Fragment fragment, int viewId, String title, String text) {
        show(fragment, new ViewTarget(viewId, fragment.getActivity()), title, text);
    }



    public void show(Fragment fragment, Target target, String title, String text) {
        if (isDone(fragment)) {
            return;
        }

        if (showcaseView != null) {
            showcaseView.hide();
        }

        Button btn = new Button(fragment.getActivity());
        btn.setVisibility(View.GONE);

        showcaseView = new ShowcaseView.Builder(fragment.getActivity())
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .replaceEndButton(btn)
                .setTarget(target)
                .setContentTitle(title)
                .setContentText(text)
                .hideOnTouchOutside()
                .build();
    }

    public ShowCaseManager createChain(Fragment fragment) {
        this.fragment = fragment;
        this.chain = new LinkedList<>();
        if (showcaseView != null) {
            showcaseView.hide();
        }
        return this;
    }

    public ShowCaseManager add(Target target, String title, String text) {
        return add(target, title, text, false);
    }

    public ShowCaseManager add(Target target, @StringRes int title, @StringRes int text) {
        return add(target, title, text, false);
    }

    public ShowCaseManager add(Target target, @StringRes int title, @StringRes int text, boolean buttonLeft) {
        return add(target, context.getString(title), context.getString(text), buttonLeft);
    }

    public ShowCaseManager add(Target target, String title, String text, boolean buttonLeft) {
        ChainElement elem = new ChainElement();
        elem.fragment = fragment;
        elem.target = target;
        elem.title = title;
        elem.text = text;
        elem.buttonLeft = buttonLeft;
        this.chain.add(elem);
        return this;
    }

    public void showChain() {
        if (isDone(fragment)) {
            return;
        }

        if (this.chain.size() > 0) {
            ChainElement elem = this.chain.get(0);
            this.chain.remove(0);

            Button btn = new Button(fragment.getActivity());
            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showChain();
                }
            });

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            if (elem.buttonLeft) {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }

            showcaseView = new ShowcaseView.Builder(fragment.getActivity())
                    .withNewStyleShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nextChain();
                        }
                    })
                    .setTarget(elem.target)
                    .setContentTitle(elem.title)
                    .setContentText(elem.text)
                    .build();
            showcaseView.setButtonPosition(params);

            if (chain.size() == 0) {
                showcaseView.setButtonText(context.getString(R.string.hide_showcase));
            } else {
                showcaseView.setButtonText(context.getString(R.string.next_showcase));
            }
        }
    }

    private void nextChain() {
        if (chain.size() > 0) {
            ChainElement elem = this.chain.get(0);
            this.chain.remove(0);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            if (elem.buttonLeft) {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            showcaseView.setButtonPosition(params);
            showcaseView.setShowcase(elem.target, true);
            if (chain.size() == 0) {
                showcaseView.setButtonText(context.getString(R.string.hide_showcase));
            }
            showcaseView.setContentTitle(elem.title);
            showcaseView.setContentText(elem.text);
        } else {
            // Do not repeat the tutorial for this fragment
            setDone(fragment);
            showcaseView.hide();
        }
    }

}
