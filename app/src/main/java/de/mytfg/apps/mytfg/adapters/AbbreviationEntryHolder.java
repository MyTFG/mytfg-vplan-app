package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.Abbreviation;
import de.mytfg.apps.mytfg.objects.TfgEventsEntry;

public class AbbreviationEntryHolder extends RecyclerView.ViewHolder {
    private TextView abbr;
    private TextView fullText;
    private TextView addon;
    private ImageView type;

    private Context context;
    private CardView cardView;

    public AbbreviationEntryHolder(View view) {
        super(view);
        context  = view.getContext();
        cardView = (CardView) view;
        abbr = (TextView) view.findViewById(R.id.abbr_abbr);
        fullText = (TextView) view.findViewById(R.id.abbr_fulltext);
        addon = (TextView) view.findViewById(R.id.abbr_addon);
        type = (ImageView) view.findViewById(R.id.abbr_type);

    }

    public void update(Abbreviation entry) {
        switch (entry.getType()) {
            default:
            case "teachers":
                type.setImageResource(R.drawable.abbr_teacher);
                break;
            case "subjects":
                type.setImageResource(R.drawable.abbr_subject);
                break;
        }
        abbr.setText(entry.getAbbreviation());
        fullText.setText(entry.getFulltext());
        if (entry.getAddon().length() > 0) {
            addon.setVisibility(View.VISIBLE);
            addon.setText(entry.getAddon());
        } else {
            addon.setVisibility(View.GONE);
        }
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}
