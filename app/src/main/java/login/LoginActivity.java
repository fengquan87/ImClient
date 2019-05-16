package login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.imclient.MainActivity;
import com.example.imclient.R;

public class LoginActivity extends AppCompatActivity {

    private EditText nameET;
    private EditText passwordET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nameET = findViewById(R.id.nameET);
        passwordET = findViewById(R.id.passwordET);
    }

    public void login(View view) {
        String name = nameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(password)){
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

//            if (TextUtils.isEmpty(sharedPreferences.getString("name",""))){
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("name",name);
                // edit.putString("name","kbqf");
                edit.commit();
//            }

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


    }
}
