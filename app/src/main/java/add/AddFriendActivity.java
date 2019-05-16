package add;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.imclient.R;

import java.util.Arrays;
import java.util.List;

import group.MyLayoutManager;

public class AddFriendActivity extends Activity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        List<String> datas = Arrays.asList(imageThumbUrls);
        Log.i("fqLog","size:"+datas.size());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new MyLayoutManager());
        recyclerView.setAdapter(new MyAdapter(this,datas));
    }


    public  String[] imageThumbUrls = new String[]{
            "https://img-my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383248_3693.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383243_5120.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383242_3127.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383242_9576.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383242_1721.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383219_5806.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383214_7794.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383213_4418.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383213_3557.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383210_8779.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383172_4577.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383166_3407.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383166_2224.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383166_7301.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383165_7197.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383150_8410.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383131_3736.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383130_5094.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383130_7393.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383129_8813.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383100_3554.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383093_7894.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383092_2432.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383092_3071.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383091_3119.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383059_6589.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383059_8814.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383059_2237.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383058_4330.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383038_3602.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382942_3079.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382942_8125.jpg"
    };


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private Context mContext;
        private List<String> mDatas;
        private final LayoutInflater inflater;

        public MyAdapter(Context context,List<String> datas){
            this.mContext = context;
            this.mDatas = datas;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.recycler_invite_item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {


            Glide.with(mContext).load(mDatas.get(position)).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private final ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }


}
