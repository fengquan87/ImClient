package fragment;

import android.os.Bundle;
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
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import base_adapter.BaseAdapter;
import base_adapter.BaseHolder;

public class MeFragment extends Fragment {

    private RecyclerView recyclerView;
    private BaseAdapter<String> adapter;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        initView();
    }
    List<String> datas = new ArrayList<>();

    private void initView() {
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        adapter = new BaseAdapter<String>(getContext(),datas,R.layout.search_item,null) {
            @Override
            protected void onBindData(BaseHolder baseHolder, final String s, int postion) {
                baseHolder.setText(R.id.userNameTV,s);
                baseHolder.setOnClickListener(R.id.addBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(s);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        boolean registered = EventBus.getDefault().isRegistered(this);
        if (!registered){
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onMessageEvent(String messageEvent) {
        adapter.addData(messageEvent);
    }
}
