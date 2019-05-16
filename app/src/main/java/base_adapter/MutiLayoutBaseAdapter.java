package base_adapter;

import android.content.Context;


import java.util.List;

import impl.InnerOnClickListener;

public abstract class MutiLayoutBaseAdapter<T> extends BaseAdapter<T> {

    public MutiLayoutBaseAdapter(Context context, List<T> datas, int[] layoutId, InnerOnClickListener listener) {
        super(context, datas, layoutId,listener);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemType(position);
    }

    @Override
    protected void onBindData(BaseHolder baseHolder, T t, int postion) {
        onBind(baseHolder,t,postion,getItemViewType(postion));
    }

    public abstract int getItemType(int position);

    public abstract void onBind(BaseHolder baseHolder,T t,int position,int itemViewType);
}
