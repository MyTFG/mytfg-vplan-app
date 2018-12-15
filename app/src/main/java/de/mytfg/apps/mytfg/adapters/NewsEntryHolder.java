package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.tools.ColorTool;
import de.mytfg.apps.mytfg.tools.Settings;

public class NewsEntryHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView date;
    private TextView summary;

    private Context context;
    private CardView cardView;

    private LinearLayout headerLayout;

    public NewsEntryHolder(View view) {
        super(view);
        context  = view.getContext();
        cardView = (CardView) view;
        title    = view.findViewById(R.id.news_title);
        date     = view.findViewById(R.id.news_date);
        summary  = view.findViewById(R.id.news_summary);
        headerLayout = view.findViewById(R.id.news_header);
    }

    public void update(TfgNewsEntry entry) {
        title.setText(entry.getTitle());
        date.setText(entry.getDateString());
        summary.setText(entry.getSummary());

        //int color = ColorTool.getColor(0, context);

        //headerLayout.setBackgroundColor(color);
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getSummary() {
        return summary;
    }

    public CardView getCardView() {
        return cardView;
    }
}
