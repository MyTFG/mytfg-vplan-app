package de.mytfg.apps.vplan.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.activities.MainActivity;
import de.mytfg.apps.vplan.adapters.RecylcerNewsAdapter;
import de.mytfg.apps.vplan.api.MyTFGApi;
import de.mytfg.apps.vplan.api.SuccessCallback;
import de.mytfg.apps.vplan.objects.TfgNews;
import de.mytfg.apps.vplan.objects.TfgNewsEntry;
import de.mytfg.apps.vplan.objects.User;
import de.mytfg.apps.vplan.objects.Vplan;
import de.mytfg.apps.vplan.toolbar.ToolbarManager;
import de.mytfg.apps.vplan.tools.CustomViewTarget;
import de.mytfg.apps.vplan.tools.ItemOffsetDecoration;
import de.mytfg.apps.vplan.tools.ShowCaseManager;

public class StartFragment extends Fragment {
    private View view;
    private RecylcerNewsAdapter adapter;
    private RecyclerView recyclerView;

    public StartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity context = (MainActivity)this.getActivity();
        setHasOptionsMenu(true);
        context.getToolbarManager()
                .clear()
                .setTitle(getString(R.string.menutitle_home))
                .setExpandable(true, true);

        MyTFGApi api = new MyTFGApi(context);
        if (api.isLoggedIn()) {
            User user = api.getUser();
            TextView infotext = (TextView) view.findViewById(R.id.home_login_status);
            String text = String.format(
                    getString(R.string.home_welcome_text),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getLevel(),
                    user.getUsername(),
                    user.getGrade(),
                    api.getExpireString()
            );
            infotext.setText(Html.fromHtml(text));
        }

        this.displayNews();

        ShowCaseManager scm = new ShowCaseManager(context);
        View toolbar = ((MainActivity)getActivity()).getToolbarManager().getToolbar();
        scm.createChain(this)
                .add(new CustomViewTarget(toolbar, 50, 0, CustomViewTarget.Type.ABS_MID_L), R.string.sc_home_navi_title, R.string.sc_home_navi_text)
                .showChain();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void displayNews() {
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.news_loading_bar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_news);
        pb.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getContext(), R.dimen.cardview_spacing));

        final TfgNews news = new TfgNews(getContext());
        news.load(new SuccessCallback() {
            @Override
            public void callback(boolean success) {
                pb.setVisibility(View.GONE);
                if (success) {
                    adapter = new RecylcerNewsAdapter(getContext());
                    recyclerView.setAdapter(adapter);
                    for (TfgNewsEntry entry : news.getEntries()) {
                        adapter.addItem(entry);
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    ((MainActivity)getActivity()).getNavi().snackbar(getString(R.string.api_news_error));
                }
            }
        });
    }
}
