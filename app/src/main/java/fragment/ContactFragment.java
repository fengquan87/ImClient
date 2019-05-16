package fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imclient.R;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import net.NetworkUtils;

import java.io.IOException;
import java.util.List;

import base_adapter.BaseAdapter;
import base_adapter.BaseHolder;
import chart.ChartActivity;

import impl.InnerOnClickListener;
import moduel.Friends;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ContactFragment extends Fragment {

    private Handler mHandler = new Handler() {

        private BaseAdapter<String> adapter;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Friends friends = (Friends) msg.obj;
                    final List<String> data = friends.getData();


                    adapter = new BaseAdapter<String>(getContext(), data, R.layout.contact_recycler_item, new InnerOnClickListener() {
                        @Override
                        public void onItemClickListener(View itemView,int position) {
                            Intent intent = new Intent(getContext(), ChartActivity.class);
                            intent.putExtra("userName",data.get(position));
                            getContext().startActivity(intent);
                        }
                    }) {
                        @Override
                        protected void onBindData(BaseHolder baseHolder, final String s, int postion) {
                            baseHolder.setText(R.id.userNameTV,s);
                        }
                    };
                    recyclerView.setAdapter(adapter);
                    break;
            }
        }
    };

    Gson gson = new Gson();
    private RecyclerView recyclerView;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //加载数据
            lazyLoad();
        }
    }

    private void lazyLoad() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String name = sharedPreferences.getString("name", "");
        if (!TextUtils.isEmpty(token)) {
            OkHttpClient client = NetworkUtils.getInstance();
            Request request = new Request.Builder().url("http://192.168.2.100:8080/ZHYCImServer/FriendServlet?user="+name).header("Authorization", token).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("fqLog", "获取数据失败：" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.i("fqLog", "result:" + result);
                    Friends friends = gson.fromJson(result, Friends.class);
                    Message message = mHandler.obtainMessage(1, friends);
                    message.sendToTarget();
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }
}
