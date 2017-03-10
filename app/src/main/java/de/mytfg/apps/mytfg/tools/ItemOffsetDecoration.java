package de.mytfg.apps.mytfg.tools;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffset;

    public ItemOffsetDecoration(int mItemOffset) {
        this.mItemOffset = mItemOffset;
    }

    public ItemOffsetDecoration(@NonNull Context context, @DimenRes int mItemOffset) {
        this(context.getResources().getDimensionPixelSize(mItemOffset));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }
}
