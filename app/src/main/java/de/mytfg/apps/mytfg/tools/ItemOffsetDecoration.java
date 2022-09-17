package de.mytfg.apps.mytfg.tools;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
    private static int defaultOffset = 10;

    private int mItemOffset;

    public ItemOffsetDecoration() {
        this(defaultOffset);
    }

    public ItemOffsetDecoration(int mItemOffset) {
        this.mItemOffset = mItemOffset;
    }

    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int mItemOffset) {
        this(context.getResources().getDimensionPixelSize(mItemOffset));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int childCount = parent.getChildCount();
        final int itemPosition = parent.getChildAdapterPosition(view);
        final int itemCount = state.getItemCount();

        if (itemCount > 0 && itemPosition == itemCount - 1) {
            // Last Element
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        } else {
            outRect.set(mItemOffset, mItemOffset, mItemOffset, 0);
        }
    }
}
