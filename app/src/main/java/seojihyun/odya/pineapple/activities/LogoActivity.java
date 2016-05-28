package seojihyun.odya.pineapple.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;

import seojihyun.odya.pineapple.data.LoginSharedPreferences;
import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.protocol.Protocol;
import seojihyun.odya.pineapple.R;

public class LogoActivity extends AppCompatActivity {

    private DataManager dataManager;
    private String user_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        ImageView a = (ImageView)findViewById(R.id.img_logo);

        //UserManager 객체 얻기
        dataManager = (DataManager) getApplicationContext();
        dataManager.setActivity(this);


    }

    public void onClick(View v) {
        try {// checkID 를 위한 단말기 전화번호 얻어오기
            //TelephonyManager telManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            //user_phone = telManager.getLine1Number();


            // 서버로부터 checkID
            //dataManager.command(Protocol.URL_CHECK_USER, user_phone, "", "", "", "");

            /* 자동 로그인 */
            // 1. 로그인 여부 확인
            if(LoginSharedPreferences.isLogin(this)) {
                String user_phone = LoginSharedPreferences.getPreferences(this, getString(R.string.user_phone));
                String user_name = LoginSharedPreferences.getPreferences(this, getString(R.string.user_name));
                    dataManager.userData.updateUserData(user_phone, user_name, "", "", "");
                    dataManager.command(Protocol.URL_LOGIN, user_phone, user_name, "", "", "");
                   // dataManager.connectURL(Protocol.URL_GET_ALL_GROUP_DATA, dataManager.userData.getUser_phone(), dataManager.userData.getUser_name(), dataManager.userData.getLatitude(), dataManager.userData.getLongitude(), dataManager.userData.getGroup_name());


            } else {
                TelephonyManager telManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
                user_phone = telManager.getLine1Number();
                if (user_phone == null) {
                    user_phone = "";
                }
                dataManager.command(Protocol.URL_CHECK_USER, user_phone, "", "", "", "");
            }
        } catch(Exception e) {

        }
    }

/*    public void onClick(View v) {
        try {// checkID 를 위한 단말기 전화번호 얻어오기
            TelephonyManager telManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
            user_phone = telManager.getLine1Number();


            // (1) Usim이 없거나 단말기 전화번호 얻어오지 못한 경우
            if (user_phone == null) {
                user_phone = " ";
            }

            // 서버로부터 checkID
            dataManager.command(Protocol.URL_CHECK_USER, user_phone, "", "", "", "");
            //***********2014-04-13 끝
            // 서버로부터 checkID
            //userManager.connectURL(Protocol.URL_CHECK_USER, user_phone, "", "", "", "");
        } catch(Exception e) {

        }
    }
*/
}
