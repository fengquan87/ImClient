package com.example.imclient;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import net.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.MainPagerFragmentAdapter;
import fragment.ContactFragment;
import fragment.MeFragment;
import fragment.MessageFragment;
import moduel.Token;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private ViewPager viewPager;
    private RadioGroup radioGroup;

    private List<Fragment> fragmentList = new ArrayList<>();
    Gson gson = new Gson();
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        radioGroup = findViewById(R.id.radioGroup);

        fragmentList.add(new MessageFragment());
        fragmentList.add(new ContactFragment());
        fragmentList.add(new MeFragment());

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        name = sharedPreferences.getString("name", "");

        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);

        EMClient.getInstance().login(name, "123456", new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.i("fqLog", "登录聊天服务器成功！");

                //开启服务接收即时通讯的消息


                try {
                    List<EMGroup> joinedGroupsFromServer = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                   // Log.i("fqLog","joinedGroupsFromServer:"+joinedGroupsFromServer.size());
                    for (EMGroup group:joinedGroupsFromServer
                         ) {
                        String groupId = group.getGroupId();
                        Log.i("fqLog","groupId:"+groupId);
                        List<String> members = group.getMembers();
                        for (String ss:members
                             ) {
                            Log.i("fqLog","ss:"+ss);
                        }
                        String owner = group.getOwner();
                        Log.i("fqLog","owner:"+owner);
                        String groupName = group.getGroupName();
                        Log.i("fqLog","groupName:"+groupName);

                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }


//                try {
//                    List<EMGroup> joinedGroupsFromServer = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
//                } catch (HyphenateException e) {
//                    e.printStackTrace();
//                }
                //从服务器获取token并保存起来
                OkHttpClient client = NetworkUtils.getInstance();
                Request request = new Request.Builder().url("http://192.168.2.100:8080/ZHYCImServer/TokenServlet").build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("fqLog", "获取数据失败："+e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Token token = gson.fromJson(response.body().string(), Token.class);
                        Log.i("fqLog", "token："+token);
                        String access_token = token.getAccess_token();

                        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

                        if (TextUtils.isEmpty(sharedPreferences.getString("token",""))){
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putString("token",access_token);
                           // edit.putString("name","kbqf");
                            edit.commit();
                        }
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.i("fqLog", "登录聊天服务器失败！"+message);
            }
        });

        radioGroup.setOnCheckedChangeListener(this);
        viewPager.setAdapter(new MainPagerFragmentAdapter(getSupportFragmentManager(),fragmentList));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rgMessage:
                viewPager.setCurrentItem(0);
                break;
            case R.id.rgContact:
                viewPager.setCurrentItem(1);
                break;
            case R.id.rgMe:
                viewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }
}
