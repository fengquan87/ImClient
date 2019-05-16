package base_adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.imclient.ImApplication;

import impl.InnerOnClickListener;


public class BaseHolder extends RecyclerView.ViewHolder {

    View itemView;
    SparseArray<View> mViews;

    public BaseHolder(final View itemView, final InnerOnClickListener listener) {
        super(itemView);
        this.itemView = itemView;
        mViews = new SparseArray<>();

        if (listener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(itemView,getLayoutPosition());
                }
            });
        }
    }

    public static <T extends BaseHolder> T getHolder(Context context, ViewGroup parent, int layoutId, InnerOnClickListener listener) {
        return (T) new BaseHolder(LayoutInflater.from(context).inflate(layoutId, parent, false), listener);
    }

    //获取view
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getItemView() {
        return itemView;
    }

    public BaseHolder setOnClickListener(int viewId, View.OnClickListener clickListener) {
        getView(viewId).setOnClickListener(clickListener);
        return this;
    }

    public BaseHolder setText(int viewId, String text) {
        ((TextView) getView(viewId)).setText(text);
        return this;
    }

    public BaseHolder setImageView(int imageViewId, String url) {
        Glide.with(ImApplication.mApplicationContext).load(url).into((ImageView) getView(imageViewId));
        return this;
    }

    public BaseHolder setBitmap(int imageViewId, Bitmap bitmap) {
        ((ImageView) getView(imageViewId)).setImageBitmap(bitmap);
        return this;
    }
}
