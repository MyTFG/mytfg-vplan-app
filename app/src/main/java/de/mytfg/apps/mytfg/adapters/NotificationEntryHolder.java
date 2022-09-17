package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.fragments.PlanFragment;
import de.mytfg.apps.mytfg.tools.TimeUtils;

public class NotificationEntryHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView date;
    private TextView text;

    private Context context;
    private CardView cardView;

    private JSONObject extras;

    public NotificationEntryHolder(View view) {
        super(view);
        context  = view.getContext();
        cardView = (CardView) view;
        title    = view.findViewById(R.id.notification_title);
        date     = view.findViewById(R.id.notification_date);
        text  = view.findViewById(R.id.notification_text);
    }

    public void update(JSONObject entry) {
        title.setText(entry.optString("title", ""));
        date.setText(TimeUtils.format(entry.optLong("date", 0), "dd.MM.yyyy HH:mm:ss"));
        text.setText(entry.optString("text", ""));
        extras = entry.optJSONObject("bundle");
        if (extras != null) {
            String type = extras.optString("type", "");
            if ("vplan-skipped".equals(type)) {
                title.setText(context.getResources().getString(R.string.notification_skipped));
                text.setText(entry.optString("title", "") + "\n" + entry.optString("text", ""));
            }
        }
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getSummary() {
        return text;
    }

    public CardView getCardView() {
        return cardView;
    }

    public void onClick() {
        extras = extras == null ? new JSONObject() : extras;

        String type = extras.optString("type", "");
        switch (type) {
            default:
                return;
            case "message":
                AlertDialog.Builder notify = new AlertDialog.Builder(context);
                notify.setTitle(title.getText());
                notify.setMessage(text.getText());
                notify.setIcon(R.drawable.tfg2_round);
                notify.show();
                break;
            case "vplan":
                ((MainActivity) context).getNavi().navigate(new PlanFragment(), R.id.fragment_container);
                break;
            case "mytfg-notification":
                String url = extras.optString("url", null);
                if (url != null) {
                    url = "https://mytfg.de/" + url;
                    ((MainActivity) context).getNavi().toWebView(url, (MainActivity) context);
                }
                break;
        }
    }
}
