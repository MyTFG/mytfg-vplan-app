package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.VrrEntry;

public class VrrEntryHolder extends RecyclerView.ViewHolder {
    private TextView line;
    private TextView direction;
    private TextView arrival;
    private TextView type;
    private TextView delay;
    private ImageView typeImg;


    private Context context;
    private CardView cardView;

    public VrrEntryHolder(View view) {
        super(view);
        context  = view.getContext();
        cardView = (CardView) view;
        line      = (TextView) view.findViewById(R.id.vrr_entry_line);
        direction = (TextView) view.findViewById(R.id.vrr_dir);
        arrival   = (TextView) view.findViewById(R.id.vrr_time);
        type      = (TextView) view.findViewById(R.id.vrr_type);
        typeImg = (ImageView) view.findViewById(R.id.vrr_type_img);
        delay     = (TextView) view.findViewById(R.id.vrr_delay);
    }

    public void update(VrrEntry entry) {
        line.setText(entry.getLine());
        direction.setText(entry.getDirection());
        arrival.setText(entry.getArrival());
        if ("U-Bahn".equals(entry.getType())) {
            typeImg.setImageResource(R.drawable.ic_vrr_tram);
        } else {
            typeImg.setImageResource(R.drawable.ic_vrr_bus);
        }
        type.setText(entry.getType());
        if (entry.getDelay() > 0) {
            delay.setVisibility(View.VISIBLE);
            delay.setText(entry.getDelayText());
        } else {
            delay.setVisibility(View.GONE);
        }
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}
