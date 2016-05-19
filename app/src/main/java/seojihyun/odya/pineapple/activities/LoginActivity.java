package seojihyun.odya.pineapple.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import seojihyun.odya.pineapple.data.LoginSharedPreferences;
import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.protocol.Protocol;
import seojihyun.odya.pineapple.R;

public class LoginActivity extends AppCompatActivity {
    DataManager dataManager;
    TextView phoneText, nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //dataManager 객체 얻기
        dataManager=(DataManager)getApplicationContext();
        dataManager.setActivity(this);

        phoneText = (TextView)findViewById(R.id.login_edit_phone);
        nameText = (TextView)findViewById(R.id.login_edit_name);
        //loginButton = (ImageView)findViewById(R.id.loginButton);

        //인텐트 값 전달 받기 - UserData
        //Intent intent = getIntent();
        //userData = (UserData) intent.getSerializableExtra("UserData");

        //이미 가입된 정보 표시
        phoneText.setText(dataManager.userData.getUser_phone());
        nameText.setText(dataManager.userData.getUser_name());


    }

    //login 버튼 클릭시
    public void login(View v) {
        Toast.makeText(this, "login..", Toast.LENGTH_SHORT).show();
        String phone = phoneText.getText().toString();
        String name = nameText.getText().toString();


        //userManager.signInToDatabase(phone, name);
        //************************************************************
        //dataManager.connectURL(Protocol.URL_LOGIN, phone, name, "", "", "");
        dataManager.command(Protocol.URL_LOGIN, phone, name, "", "", "");
    }
}
