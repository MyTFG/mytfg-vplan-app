package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.VrrEntry;

public class VrrEntryHolder extends RecyclerView.ViewHolder {
    private TextView line;
    private TextView direction;
    private TextView arrival;

    private Context context;
    private CardView cardView;

    public VrrEntryHolder(View view) {
        super(view);
        context  = view.getContext();
        cardView = (CardView) view;
        line      = (TextView) view.findViewById(R.id.vrr_entry_line);
        direction = (TextView) view.findViewById(R.id.vrr_dir);
        arrival   = (TextView) view.findViewById(R.id.vrr_time);
    }

    public void update(VrrEntry entry) {
        line.setText(entry.getLine());
        direction.setText(entry.getDirection());
        arrival.setText(entry.getArrival());
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}
