package group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.imclient.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class GroupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);
        final String groupId = getIntent().getStringExtra("groupId");

        // Log.i("fqLog","groupId:"+groupId);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                    String owner = group.getOwner();//获取群主
                    Log.i("fqLog", "owner:" + owner);
                    List<String> members = group.getMembers();//获取内存中的群成员

                    for (String member : members
                    ) {
                        Log.i("fqLog", "member:" + member);
                    }

                } catch (HyphenateException e) {
                    Log.i("fqLog", "e:" + e.toString());
                    e.printStackTrace();
                }

            }
        }.start();

    }
}
