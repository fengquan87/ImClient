package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imclient.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import base_adapter.BaseAdapter;
import base_adapter.BaseHolder;
import group.GroupActivity;
import impl.InnerOnClickListener;
import moduel.MyEMGroup;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private List<EMGroup> datas = new ArrayList<>();
    private BaseAdapter<EMGroup> adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    List<EMGroup> joinedGroupsFromServer = (List<EMGroup>) msg.obj;
                    adapter.setData(joinedGroupsFromServer);
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadData();
        }
    }

    private void loadData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    List<EMGroup> joinedGroupsFromServer = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    Message message = handler.obtainMessage(1, joinedGroupsFromServer);
                    message.sendToTarget();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BaseAdapter<EMGroup>(getActivity(), datas, R.layout.group_list, new InnerOnClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                String groupId = adapter.getData().get(position).getGroupId();
                Intent intent = new Intent(getActivity(), GroupActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        }) {
            @Override
            protected void onBindData(BaseHolder baseHolder, EMGroup emGroup, int postion) {
                baseHolder.setText(R.id.groupTv, emGroup.getGroupName());
            }
        };
        recyclerView.setAdapter(adapter);
    }
}
