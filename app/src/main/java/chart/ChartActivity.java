package chart;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imclient.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import net.NetworkUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import base_adapter.BaseHolder;
import base_adapter.MutiLayoutBaseAdapter;
import group.AddGroupActivity;
import moduel.Message;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import search.SearchActivity;
import utils.PermissionUtil;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int READ_EXTERNAL_STORAGE = 1;
    private TextView titleTV;
    private RecyclerView recyclerView;
    private EditText contentET;
    private Button sendBtn;
    private String userName;
    private EMMessageListener msgListener;
    private LinearLayoutManager linearLayoutManager;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Message message = (Message) msg.obj;
                    adapter.addData(message);
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    break;
            }
        }
    };
    private ImageButton imageButton;
    private static final int ALBUM_RESULT_CODE = 0;
    private MutiLayoutBaseAdapter<Message> adapter;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        titleTV = findViewById(R.id.titleTV);
        recyclerView = findViewById(R.id.recyclerView);
        contentET = findViewById(R.id.contentET);
        sendBtn = findViewById(R.id.sendBtn);

        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE);
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        name = sharedPreferences.getString("name", "");
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        titleTV.setText(userName);
        sendBtn.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        int[] layoutId = {R.layout.chat_from, R.layout.chat_to, R.layout.chat_from_file, R.layout.chat_to_file};
        //文字
        //发出的信息
        //收到的信息
        //图片
        //发出的照片
        //收到的照片
        adapter = new MutiLayoutBaseAdapter<Message>(this, new ArrayList<Message>(), layoutId, null) {
            @Override
            public int getItemType(int position) {
                int layoutId = -1;
                if (mDatas.size() != 0) {
                    Message message = mDatas.get(position);
                    if (message.getContent().startsWith("txt:")) {
                        //文字
                        if (message.getMessageType() == 1) {
                            //发出的信息
                            layoutId = 0;
                        } else {
                            //收到的信息
                            layoutId = 1;
                        }

                    } else if (message.getContent().startsWith("image:")) {
                        //图片
                        if (message.getMessageType() == 1) {
                            //发出的照片
                            layoutId = 2;
                        } else {
                            //收到的照片
                            layoutId = 3;
                        }
                    }
                }
                return layoutId;
            }

            @Override
            public void onBind(final BaseHolder baseHolder, final Message message, final int position, int itemViewType) {
                switch (itemViewType) {
                    case 0:
                        String fromContent = message.getContent().substring("txt:".length());
                        baseHolder.setText(R.id.nameTv, message.getName());
                        baseHolder.setText(R.id.fromTV, fromContent);
                        break;
                    case 1:
                        String toContent = message.getContent().substring("txt:".length());
                        baseHolder.setText(R.id.toTV, message.getName());
                        baseHolder.setText(R.id.contentTV, toContent);
                        break;
                    case 2:
                        baseHolder.setText(R.id.nameTv, message.getName());
                        if (message.getBitmap() != null) {
                            baseHolder.setBitmap(R.id.fromIV, message.getBitmap());
                        } else {
                            Log.i("fqLog", "content:" + message.getContent().toString());
//                            int fromIndex = message.getContent().indexOf("thumbnail:");
//                            String image = message.getContent().substring(fromIndex + "thumbnail:".length());
                            String substring = "";
                            String[] split = message.getContent().split(",");
                            for (String s:split
                                 ) {
                                if (s.contains("remoteurl:")){
                                    substring = s.substring("remoteurl:".length() + 1);
                                }
                            }

                            OkHttpClient client = NetworkUtils.getInstance();
                            Request request = new Request.Builder().url(substring).build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.i("fqLog", "e:" + e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    InputStream inputStream = response.body().byteStream();
                                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    int byteCount = bitmap.getByteCount();
                                    Log.i("fqLog", "byteCount:" + byteCount);
                                    ((ChartActivity) mContext).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            baseHolder.setBitmap(R.id.fromIV, bitmap);
                                        }
                                    });
                                }
                            });
                        }

                        break;
                    case 3:
                        int toIndex = message.getContent().indexOf("thumbnail:");
                        String image = message.getContent().substring(toIndex + "thumbnail:".length());
                        OkHttpClient client = NetworkUtils.getInstance();
                        Request request = new Request.Builder().url(image).build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("fqLog", "e:" + e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                InputStream inputStream = response.body().byteStream();
                                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                int byteCount = bitmap.getByteCount();
                                Log.i("fqLog", "byteCount:" + byteCount);
                                ((ChartActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        baseHolder.setText(R.id.nameTv, message.getName());
                                        baseHolder.setBitmap(R.id.fromIV, bitmap);
                                    }
                                });
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        };

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(userName);
        //获取此会话的所有消息
        if (conversation != null) {
            List<EMMessage> messages = conversation.getAllMessages();
            if (messages != null) {

                for (EMMessage message : messages
                ) {
                    //Message transform = transform(message);
                    Message meMessage = new Message();
                    String from = message.getFrom();
                    String to = message.getTo();
                    if (name.equals(from)) {
                        meMessage.setName(from);
                        meMessage.setMessageType(1);
                    } else {
                        meMessage.setName(from);
                        meMessage.setMessageType(0);
                    }
                    meMessage.setContent(message.getBody().toString());
                    messagess.add(meMessage);
                    adapter.setData(messagess);
                }
            }
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ALBUM_RESULT_CODE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    String imagePath = c.getString(columnIndex);
                    File file = new File(imagePath);
                    Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Log.i("fqLog", "bm:" + bm);

                    Message message = new Message();
                    message.setMessageType(1);
                    message.setName(name);
                    message.setBitmap(bm);
                    message.setContent("image:");
                    adapter.addData(message);
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());

                    EMMessage emMessage = EMMessage.createImageSendMessage(imagePath, true, userName);
                    EMClient.getInstance().chatManager().sendMessage(emMessage);

                    emMessage.setMessageStatusCallback(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Log.i("fqLog", "发送图片成功");
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.i("fqLog", "i:" + i + ",s:" + s);
                        }

                        @Override
                        public void onProgress(int i, String s) {
                            Log.i("fqLog", "i:" + i + ",s:" + s);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private void requestPermission(String permission, int requestCode) {
        if (!PermissionUtil.isGranted(this, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            //直接执行相应操作了
            Intent albumIntent = new Intent(Intent.ACTION_PICK);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(albumIntent, ALBUM_RESULT_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent albumIntent = new Intent(Intent.ACTION_PICK);
                albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(albumIntent, ALBUM_RESULT_CODE);
            } else {
                // Permission Denied
                Toast.makeText(ChartActivity.this, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //收到消息
        //收到透传消息
        //收到已读回执
        //收到已送达回执
        //消息被撤回
        //消息状态变动
        msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                String name = Thread.currentThread().getName();
                Log.i("fqLog", "name:" + name);
                //收到消息
                for (EMMessage message : messages
                ) {
                    String content = message.getBody().toString();
                    Log.i("fqLog", "content:" + content);
                    Message fromMessage = new Message();
                    fromMessage.setMessageType(2);
                    fromMessage.setName(message.getFrom());

                    fromMessage.setContent(content);
                    android.os.Message obtainMessage = mHandler.obtainMessage(1, fromMessage);
                    obtainMessage.sendToTarget();
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
                for (EMMessage message : messages
                ) {
                    Log.i("fqLog", "onCmdMessageReceived:" + message.getFrom() + ",content" + message.getBody().toString());
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
                for (EMMessage message : messages
                ) {
                    Log.i("fqLog", "onMessageRead:" + message.getFrom() + ",content" + message.getBody().toString());
                }
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
                for (EMMessage msg : message
                ) {
                    Log.i("fqLog", "onMessageRead:" + msg.getFrom() + ",content" + msg.getBody().toString());
                }
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
                for (EMMessage message : messages
                ) {
                    Log.i("fqLog", "onMessageRead:" + message.getFrom() + ",content" + message.getBody().toString());
                }
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
                Log.i("fqLog", "onMessageChanged:" + message.getFrom() + ",content" + message.getBody().toString());
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    private List<Message> messagess = new ArrayList<>();

    @Override
    public void onClick(View v) {
        String content = contentET.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            EMMessage message = EMMessage.createTxtSendMessage(content, userName);
            Message meMessage = new Message();
            meMessage.setName(name);
            meMessage.setMessageType(1);
            meMessage.setContent("txt:" + content);
            //  messagess.add(meMessage);
            adapter.addData(meMessage);
            // adapter.setData(messagess);
            recyclerView.smoothScrollToPosition(adapter.getItemCount());

            //发送消息
            EMClient.getInstance().chatManager().sendMessage(message);
            message.setMessageStatusCallback(new EMCallBack() {
                @Override
                public void onSuccess() {
                    Log.i("fqLog", "发送成功");
                }

                @Override
                public void onError(int i, String s) {
                    Log.i("fqLog", "i:" + i + ",s:" + s);
                }

                @Override
                public void onProgress(int i, String s) {
                    Log.i("fqLog", "i:" + i + ",s:" + s);
                }
            });

            contentET.setText("");

        }
    }

    public void doAction(View view) {

        int[] location = new int[2];
        int[] winlocation = new int[2];
        view.getLocationOnScreen(location);
        view.getLocationInWindow(winlocation);
        int measuredHeight = view.getMeasuredHeight();

        View pop_View = LayoutInflater.from(this).inflate(R.layout.pop_window, null);
        PopupWindow popupWindow = new PopupWindow(pop_View, 400, 400, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAsDropDown(view,500,10);

        TextView addTv = pop_View.findViewById(R.id.addTv);
        TextView groupTv = pop_View.findViewById(R.id.groupTv);
        addTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChartActivity.this, SearchActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });

        groupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChartActivity.this, AddGroupActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });




    }
}
