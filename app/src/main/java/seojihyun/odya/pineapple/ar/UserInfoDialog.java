package seojihyun.odya.pineapple.ar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import seojihyun.odya.pineapple.R;

/**
 * Created by SEOJIHYUN on 2016-05-09.
 */

public class UserInfoDialog extends Dialog {

    //2016-05-07 getData
    String message;


    private Context context;
    private TextView mTitleView;
    private TextView mContentView;
    private EditText password;
    private Button mLeftButton;
    private Button mRightButton;
    private String mTitle;
    private String mContent;

    private Marker marker;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    public UserInfoDialog(Context context, Marker marker) {
        super(context);

        this.context = context;
        this.marker = marker;

    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK)
            this.dismiss();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_ar_user_info);

        mTitleView = (TextView) findViewById(R.id.dialog_userInfo_title);
        mLeftButton = (Button) findViewById(R.id.ar_button_left);
        mRightButton = (Button) findViewById(R.id.ar_button_right);

        // 사용자 이름 셋팅 테스트중 서지현
        mTitleView.setText(marker.getTitle());

        // 클릭 이벤트 셋팅
        mLeftClickListener = new SOSClickListener();
        mLeftButton.setOnClickListener(mLeftClickListener);
        /*if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }*/


    }
    class SOSClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(context, marker.getUser_phone(), Toast.LENGTH_SHORT).show();

            // 2016-05-07 서버 푸시 테스트 *******************************************
            GetDataJSON connect = new GetDataJSON();
            connect.execute("http://211.58.69.16:8080/pineapple/send_sos.php","01090807091", marker.getUser_phone(), "sos", "도와주세요");
        }
    }
    class GetDataJSON extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        String uri="", sender_phone="", receiver_phone="", type="", msg="";

        @Override
        protected String doInBackground(String... params) {
            try {
                uri = params[0];
                sender_phone = params[1];
                receiver_phone = params[2];
                type = params[3];
                msg = params[4];


                BufferedReader bufferedReader = null;

                String sendData = URLEncoder.encode("sender_phone", "UTF-8") + "=" + URLEncoder.encode(sender_phone, "UTF-8");
                sendData += "&" + URLEncoder.encode("receiver_phone", "UTF-8") + "=" + URLEncoder.encode(receiver_phone, "UTF-8");
                sendData += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8");
                sendData += "&" + URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode(msg, "UTF-8");
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                // 서버로 메세지 전송
                con.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

                wr.write(sendData);
                wr.flush();


                // 서버로부터 데이터 수신
                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            message = result;
            getMessage(message);
        }

        @Override
        protected void onPreExecute() {
            //dialog = ProgressDialog.show(activity, "잠시만 기다려주세요", null, true, true);
        }
    }

    public void getMessage(String data) {

        Toast.makeText(context, "message From Server : " + data, Toast.LENGTH_SHORT).show();
    }


}
