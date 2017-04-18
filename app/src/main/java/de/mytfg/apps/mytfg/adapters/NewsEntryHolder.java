package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.TfgNewsEntry;
import de.mytfg.apps.mytfg.tools.Settings;

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

    public void update(TfgNewsEntry entry, boolean extended) {
        //titleView.setText(baseObject.getName());
        title.setText(entry.getTitle());
        date.setText(entry.getDateString());
        if (extended) {
            summary.setText(Html.fromHtml(entry.getHtml()));
        } else {
            summary.setText(entry.getSummary());
        }
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
