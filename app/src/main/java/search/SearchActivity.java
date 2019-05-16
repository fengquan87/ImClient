package search;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.imclient.R;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import net.NetworkUtils;

import java.io.IOException;
import java.util.List;

import base_adapter.BaseAdapter;
import base_adapter.BaseHolder;
import moduel.Friends;
import moduel.Person;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Person person = (Person) msg.obj;
            List<Person.PersonDetail> entities = person.getEntities();
            recyclerView.setAdapter(new BaseAdapter<Person.PersonDetail>(SearchActivity.this,entities,R.layout.search_item,null) {

                @Override
                protected void onBindData(BaseHolder baseHolder, final Person.PersonDetail personDetail, int postion) {
                    baseHolder.setText(R.id.userNameTV,personDetail.getUsername());
                    baseHolder.setOnClickListener(R.id.addBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                EMClient.getInstance().contactManager().addContact(personDetail.getUsername(), "我是一个超级大帅哥");
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    };

    private EditText searchET;
    Gson gson = new Gson();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchET = findViewById(R.id.searchET);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void search(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String userName = searchET.getText().toString().trim();
        if (!TextUtils.isEmpty(token)) {
            OkHttpClient client = NetworkUtils.getInstance();
            Request request = new Request.Builder().url("http://192.168.2.100:8080/ZHYCImServer/SearchServlet?username=" + userName).header("Authorization", token).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("fqLog", "获取数据失败：" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Person person = gson.fromJson(result, Person.class);
                    Message message = mHandler.obtainMessage(1, person);
                    message.sendToTarget();
                }
            });

        }
    }
}
