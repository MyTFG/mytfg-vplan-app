package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;

public class RecyclerNotificationsAdapter extends RecyclerView.Adapter<NotificationEntryHolder> {
    private Context context;
    private ArrayList<JSONObject> elements = new ArrayList<>();

    private String unique;

    public RecyclerNotificationsAdapter(Context c) {
        this.context = c;
        unique = UUID.randomUUID().toString();
    }

    public void addItem(JSONObject item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public NotificationEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_notificationentry, null);
        return new NotificationEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificationEntryHolder holder, int position) {
        final JSONObject entry = elements.get(position);
        holder.update(entry);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.onClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void clear() {
        elements.clear();
    }
}
