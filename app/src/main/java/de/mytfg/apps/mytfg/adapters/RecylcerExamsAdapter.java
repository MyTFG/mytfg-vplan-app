package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.ExamEntry;
import de.mytfg.apps.mytfg.objects.VplanEntry;

public class RecylcerExamsAdapter extends RecyclerView.Adapter<ExamEntryHolder> {
    private Context context;
    private ArrayList<ExamEntry> elements = new ArrayList<>();

    private int lastPosition = -1; // None

    private int endOffset;

    public RecylcerExamsAdapter(Context c) {
        this.context = c;
    }

    public void addItem(ExamEntry item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public ExamEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_examentry, null);
        return new ExamEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(final ExamEntryHolder holder, int position) {
        if (elements.size() == 0) {
            holder.update(null);
            return;
        }
        final ExamEntry entry = elements.get(position);
        holder.update(entry);
    }



    @Override
    public int getItemCount() {
        return Math.max(1, elements.size());
    }

    public void clear() {
        elements.clear();
    }

    @Override
    public void onViewDetachedFromWindow(ExamEntryHolder holder) {
        holder.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }
}
