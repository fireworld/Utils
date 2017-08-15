package cc.colorcat.toolbox.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by cxx on 17-6-30.
 * xx.ch@outlook.com
 */
public abstract class RvOnLoadMoreScrollListener extends RecyclerView.OnScrollListener {
    private boolean mUpOnLast = false;

    public RvOnLoadMoreScrollListener() {
        super();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
//        L.i("onScrollStateChanged, newState = " + newState);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int total = manager.getItemCount();
        View view = manager.findViewByPosition(total - 1);
        if (mUpOnLast && view != null && newState == RecyclerView.SCROLL_STATE_IDLE) {
//            L.e("you need more");
            onLoadMore();
        }
//        L.d("total = " + total + ", the last view + " + view);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mUpOnLast = dy > 0;
//        L.e("dx = " + dx + ", dy = " + dy + ", mUpOnLast = " + mUpOnLast);
    }

    public abstract void onLoadMore();
}
