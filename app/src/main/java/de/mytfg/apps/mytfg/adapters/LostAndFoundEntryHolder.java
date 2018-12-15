package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.LostAndFound;
import de.mytfg.apps.mytfg.objects.LostAndFoundEntry;
import de.mytfg.apps.mytfg.objects.VrrEntry;

public class LostAndFoundEntryHolder extends RecyclerView.ViewHolder {
    private TextView description;
    private TextView date;

    private Context context;
    private CardView cardView;

    public LostAndFoundEntryHolder(View view) {
        super(view);
        context     = view.getContext();
        cardView    = (CardView) view;
        description = view.findViewById(R.id.laf_description);
        date        = view.findViewById(R.id.laf_registerdate);
    }

    public void update(LostAndFoundEntry entry) {
        description.setText(entry.getDescription());
        date.setText(MyTFGApi.tsToString(entry.getRegisterdateTS()));
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}
