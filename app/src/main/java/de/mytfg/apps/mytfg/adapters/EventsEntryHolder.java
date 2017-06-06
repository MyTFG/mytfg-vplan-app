package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
    private TextView dayname;
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
        dayname     = (TextView) view.findViewById(R.id.event_dayname);
        month     = (TextView) view.findViewById(R.id.event_month);
        year     = (TextView) view.findViewById(R.id.event_year);

        location  = (TextView) view.findViewById(R.id.event_location);
    }

    public void update(final TfgEventsEntry entry) {
        //titleView.setText(baseObject.getName());
        title.setText(entry.getTitle());
        long ts = entry.getTimestamp();
        day.setText(MyTFGApi.getDay(ts));
        month.setText(MyTFGApi.getMonth(ts));
        year.setText(MyTFGApi.getYear(ts));
        dayname.setText(MyTFGApi.getDayname(ts));
        String time = entry.getTime();
        String loc = entry.getLocation();

        long fullTs = ts;
        boolean fullDay;
        if (!time.isEmpty()) {
            try {
                String[] parts = time.split(":");
                int hour = Integer.parseInt(parts[0]);
                int min = Integer.parseInt(parts[1]);
                fullDay = false;
                fullTs += 60 * min;
                fullTs += 60 * 60 * hour;
            } catch (Exception ex) {
                fullDay = true;
            }
        } else {
            fullDay = true;
        }
        final long calendarTs = fullTs * 1000;
        final boolean calendarFullDay = fullDay;


        if (time.length() > 0) {
            loc += loc.length() > 0 ? ", " + time : time;
        }

        location.setText(loc);
        if (loc.length() == 0) {
            location.setVisibility(View.GONE);
        } else {
            location.setVisibility(View.VISIBLE);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage(context.getString(R.string.event_calendar_confirm_text))
                        .setTitle(context.getString(R.string.event_calendar_confirm_title))
                        .setPositiveButton(context.getString(R.string.event_calendar_confirm_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_EDIT);
                                intent.setType("vnd.android.cursor.item/event");
                                intent.putExtra("beginTime", calendarTs);
                                intent.putExtra("allDay", calendarFullDay);
                                intent.putExtra("endTime", calendarTs + 60*60*1000);
                                intent.putExtra("title", entry.getTitle());
                                intent.putExtra("eventLocation", entry.getLocation());
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.event_calendar_confirm_no), null)
                        .setIcon(R.drawable.ic_menu_plan).show();



            }
        });
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }
}
