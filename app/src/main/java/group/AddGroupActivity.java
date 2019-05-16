package group;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.imclient.R;
import com.google.gson.Gson;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

import net.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import base_adapter.BaseAdapter;
import base_adapter.BaseHolder;
import impl.InnerOnClickListener;
import moduel.Friends;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private BaseAdapter<String> adapter;

    private List<String> groups = new ArrayList<>();
    private String name;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Friends friends = (Friends) msg.obj;
                    final List<String> data = friends.getData();
                    adapter.setData(data);
                    break;
                default:
                    break;
            }
        }
    };
    private Button okBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);

        recyclerView = findViewById(R.id.recyclerView);
        okBtn = findViewById(R.id.okBtn);
        okBtn.setEnabled(false);
        okBtn.setOnClickListener(this);
        name = getIntent().getStringExtra("name");

        loadData();

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        List<String> datas = new ArrayList<>();

        adapter = new BaseAdapter<String>(this, datas, R.layout.group_add_item, new InnerOnClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                CheckBox checkBox = view.findViewById(R.id.checkbox);
                if (!checkBox.isChecked()) {
                    checkBox.setChecked(true);
                    groups.add(adapter.getData().get(position));

                } else {
                    checkBox.setChecked(false);
                    groups.remove(adapter.getData().get(position));

                }
                if (groups.size() > 0) {
                    okBtn.setEnabled(true);
                } else {
                    okBtn.setEnabled(false);
                }
            }
        }) {
            @Override
            protected void onBindData(BaseHolder baseHolder, String s, int postion) {
                baseHolder.setText(R.id.nameTv, s);
                CheckBox checkBox = baseHolder.getItemView().findViewById(R.id.checkbox);
                if (groups.contains(s)) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            }
        };

        recyclerView.setAdapter(adapter);
    }

    Gson gson = new Gson();

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        if (!TextUtils.isEmpty(token)) {
            OkHttpClient client = NetworkUtils.getInstance();
            Request request = new Request.Builder().url("http://192.168.2.100:8080/ZHYCImServer/FriendServlet?user=" + name).header("Authorization", token).build();
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

    @Override
    public void onClick(View v) {
        final EMGroupOptions option = new EMGroupOptions();
        option.maxUsers = 200;
        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
        String[] strings = new String[groups.size()];
        final String[] allMembers = groups.toArray(strings);

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    EMGroup group = EMClient.getInstance().groupManager().createGroup("dsadsada", "这个群全部都是帅哥和美女", allMembers, "这个是帅哥", option);

                    Log.i("fqLog","group:"+group.getGroupId());
                } catch (HyphenateException e) {
                    Log.i("fqLog","HyphenateException:"+e.toString());
                    e.printStackTrace();
                }
            }
        }.start();



        //        EMClient.getInstance().groupManager().asyncCreateGroup("这是我的群", "这个群全部都是帅哥和美女", allMembers, "这个是帅哥", option, new EMValueCallBack<EMGroup>() {
//            @Override
//            public void onSuccess(EMGroup emGroup) {
//                Log.i("fqLog","emGroup:"+emGroup.getGroupId());
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                Log.i("fqLog","s:"+s);
//            }
//        });

    }
}
