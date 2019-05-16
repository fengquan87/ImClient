package com.example.imclient;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import org.greenrobot.eventbus.EventBus;

public class ImApplication extends Application {

    public static Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationContext = this;
        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
// 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
// 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);

//初始化
        EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);



        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {


            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                Log.i("fqLog","username:"+username+",reason:"+reason);
                EventBus.getDefault().postSticky(username);
            }

            @Override
            public void onFriendRequestAccepted(String s) {
                //同意添加好友
                Log.i("fqLog","onFriendRequestAccepted:"+s);
            }

            @Override
            public void onFriendRequestDeclined(String s) {
                //拒绝添加好友
                Log.i("fqLog","onFriendRequestDeclined:"+s);
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                Log.i("fqLog","onContactDeleted:"+username);
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                Log.i("fqLog","onContactAdded:"+username);
            }
        });
    }
}
