package de.mytfg.apps.vplan.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

import de.mytfg.apps.vplan.R;
import de.mytfg.apps.vplan.objects.TfgNewsEntry;
import de.mytfg.apps.vplan.objects.VplanEntry;
import de.mytfg.apps.vplan.tools.Settings;

import static android.view.View.GONE;

public class NewsEntryHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView date;
    private TextView summary;

    private Context context;
    private CardView cardView;

    public NewsEntryHolder(View view) {
        super(view);
        context  = view.getContext();
        cardView = (CardView) view;
        title    = (TextView) view.findViewById(R.id.news_title);
        date     = (TextView) view.findViewById(R.id.news_date);
        summary  = (TextView) view.findViewById(R.id.news_summary);
    }

    public void update(TfgNewsEntry entry) {
        //titleView.setText(baseObject.getName());
        title.setText(entry.getTitle());
        date.setText(entry.getDateString());
        summary.setText(entry.getSummary());
        final String link = entry.getLink();
        Settings settings = new Settings(context);
        if (!link.isEmpty() && settings.getBool("news_browser")) {
            setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(link));
                    context.startActivity(i);
                }
            });
        } else {
            setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}
