package group;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class MyLayoutManager extends RecyclerView.LayoutManager {

    private final static int VISIABLE_COUNT = 10;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        Log.i("fqLog","onMeasure:");
    }

    boolean isLayout =false;
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

//        if (state.getItemCount() == 0) {
//            removeAndRecycleAllViews(recycler);
//            return;
//        }
//        int offsetY = 0;
//        for (int i = 0; i < getItemCount(); i++) {
//            View view = recycler.getViewForPosition(i);
//            addView(view);
//            measureChildWithMargins(view, 0, 0);
//            int width = getDecoratedMeasuredWidth(view);
//            int height = getDecoratedMeasuredHeight(view);
//         //   layoutDecorated(view, 0, offsetY, width, offsetY + height);
//            layoutDecoratedWithMargins(view,0, offsetY, width, offsetY + height);
//            offsetY += height;
//        }

        if (!isLayout) {
            for (int i = 0; i < VISIABLE_COUNT; i++) {
                View child = recycler.getViewForPosition(i);
                addView(child);
                int width = getWidth();
                int height = getHeight();
                measureChildWithMargins(child, 0, 0);
                int width2 = getDecoratedMeasuredWidth(child);
                int height2 = getDecoratedMeasuredHeight(child);
                int childLeft = width / 2 - width2 / 2;
                int childTop = height / 2 - height2 / 2;
                layoutDecorated(child, childLeft, childTop, childLeft + width2, childTop + height2);
                // layoutDecoratedWithMargins(child,childLeft,childTop,childLeft+width2,childTop+height2);
                isLayout = true;
            }
        }

//        int offsetY = 0;
//        for (int i = 0; i < getItemCount(); i++) {
//            View view = recycler.getViewForPosition(i);
//            addView(view);
//            measureChildWithMargins(view, 0, 0);
//            int width = getDecoratedMeasuredWidth(view);
//            int height = getDecoratedMeasuredHeight(view);
//            layoutDecorated(view, 0, offsetY, width, offsetY + height);
//            offsetY += height;
//        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.i("fqLog","dx:"+dx);
        if (dx<0){
            View child = getChildAt(getChildCount() - 1);
            measureChild(child,0,0);
            layoutDecorated(child, child.getLeft()-dx, child.getTop(), child.getLeft()-dx+child.getMeasuredWidth(), child.getBottom());
        }
        return dx;
    }
}
