package seojihyun.odya.pineapple.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import seojihyun.odya.pineapple.R;
import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.protocol.Protocol;

public class SignUpActivity extends AppCompatActivity {

    private DataManager dataManager;
    //UserData userData;
    TextView phoneText, nameText;
    ImageView signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //UserManager 객체 얻기
        dataManager=(DataManager)getApplicationContext();
        dataManager.setActivity(this);

        //UserData 객체 전달 ** UserData implements Serializable  참고!!
        //Intent intent = getIntent();
        // userData = (UserData)intent.getSerializableExtra("UserData");

        phoneText = (TextView)findViewById(R.id.signup_edit_phone);
        nameText = (TextView)findViewById(R.id.signup_edit_name);
        //signUpButton = (ImageView)findViewById(R.id.signUpButton);

        // UserManager 객체 얻어오기

        //userManager.setUserData(userData);
        phoneText.setText(dataManager.userData.getUser_phone());
        nameText.setText(dataManager.userData.getUser_name());

    }

    //signUp 버튼 클릭시
    public void signUp(View v) {
        String phone = phoneText.getText().toString();
        String name = nameText.getText().toString();

        //dataManager.connectURL(Protocol.URL_SIGN_UP, phone, name, "", "", "");
        dataManager.command(Protocol.URL_SIGN_UP, phone, name, "", "", "");
    }

    public void goToLogin(View v) {
        dataManager.changeActivity("LoginActivity");
    }
}
