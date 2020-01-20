package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.fragments.VrrDetailFragment;
import de.mytfg.apps.mytfg.objects.VrrEntry;

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
