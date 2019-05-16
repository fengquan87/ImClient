package base_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import impl.InnerOnClickListener;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder> {

    public Context mContext;
    public List<T> mDatas;
    int[] layoutIds;
    private InnerOnClickListener listener;


    //一种类型的RecyclerView
    public BaseAdapter(Context context, List<T> datas, int layoutId, InnerOnClickListener listener) {
        this.mContext = context;
        this.mDatas = datas;
        this.layoutIds = new int[]{layoutId};
        this.listener = listener;
    }

    //多种类型的RecyclerView
    public BaseAdapter(Context context, List<T> datas, int[] layoutId,InnerOnClickListener listener) {
        this.mContext = context;
        this.mDatas = datas;
        this.layoutIds = layoutId;
        this.listener = listener;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseHolder holder = BaseHolder.getHolder(mContext, parent, layoutIds[viewType],listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        onBindData(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    protected abstract void onBindData(BaseHolder baseHolder, T t, int postion);

    public void setData(List<T> data) {
        this.mDatas = data;
        notifyDataSetChanged();
    }

    public void addData(T t) {
        mDatas.add(t);
        notifyDataSetChanged();
    }

    public List<T> getData(){
        return mDatas;
    }
}
