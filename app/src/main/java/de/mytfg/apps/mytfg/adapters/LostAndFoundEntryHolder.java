package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.LostAndFoundEntry;

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
