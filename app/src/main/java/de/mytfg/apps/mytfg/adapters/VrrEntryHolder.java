package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
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
    private FrameLayout typeFrame;

    private Context context;
    private CardView cardView;

    public VrrEntryHolder(View view) {
        super(view);
        context   = view.getContext();
        cardView  = (CardView) view;
        line      = view.findViewById(R.id.vrr_entry_line);
        direction = view.findViewById(R.id.vrr_dir);
        arrival   = view.findViewById(R.id.vrr_time);
        type      = view.findViewById(R.id.vrr_type);
        typeImg   = view.findViewById(R.id.vrr_type_img);
        delay     = view.findViewById(R.id.vrr_delay);
        typeFrame = view.findViewById(R.id.vrr_type_background);
    }

    public void update(VrrEntry entry) {
        line.setText(entry.getLine());
        direction.setText(entry.getDirection());
        arrival.setText(entry.getArrival());
        if ("U-Bahn".equals(entry.getType())) {
            typeImg.setImageResource(R.drawable.ic_vrr_tram_white);
            typeFrame.setBackgroundResource(R.drawable.circle_background_blue);
        } else {
            typeImg.setImageResource(R.drawable.ic_vrr_bus_white);
            typeFrame.setBackgroundResource(R.drawable.circle_background_red);
        }
        type.setText(entry.getType());
        /*if (entry.getDelay() > 0) {
            delay.setVisibility(View.VISIBLE);
            delay.setText(entry.getDelayText());
        } else {*/
        delay.setVisibility(View.GONE);

    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }

    public TextView getLine() {
        return line;
    }

    public TextView getDirection() {
        return direction;
    }

    public TextView getArrival() {
        return arrival;
    }

    public TextView getType() {
        return type;
    }

    public TextView getDelay() {
        return delay;
    }

    public ImageView getTypeImg() {
        return typeImg;
    }

    public FrameLayout getTypeFrame() {
        return typeFrame;
    }
}
