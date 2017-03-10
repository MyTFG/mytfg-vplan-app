package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.TfgEventsEntry;

public class EventsEntryHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView date;
    private TextView day;
    private TextView month;
    private TextView year;
    private TextView location;

    private Context context;
    private CardView cardView;

    public EventsEntryHolder(View view) {
        super(view);
        context  = view.getContext();
        cardView = (CardView) view;
        title    = (TextView) view.findViewById(R.id.event_title);
        //date     = (TextView) view.findViewById(R.id.event_date);
        day     = (TextView) view.findViewById(R.id.event_day);
        month     = (TextView) view.findViewById(R.id.event_month);
        year     = (TextView) view.findViewById(R.id.event_year);

        location  = (TextView) view.findViewById(R.id.event_location);
    }

    public void update(TfgEventsEntry entry) {
        //titleView.setText(baseObject.getName());
        title.setText(entry.getTitle());
        long ts = entry.getTimestamp();
        day.setText(MyTFGApi.getDay(ts));
        month.setText(MyTFGApi.getMonth(ts));
        year.setText(MyTFGApi.getYear(ts));
        String time = entry.getTime();
        String loc = entry.getLocation();
        if (time.length() > 0) {
            loc += loc.length() > 0 ? ", " + time : time;
        }

        location.setText(loc);
        if (loc.length() == 0) {
            location.setVisibility(View.GONE);
        } else {
            location.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}
