package seojihyun.odya.pineapple.protocol;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import seojihyun.odya.pineapple.BackgroundActivity;
import seojihyun.odya.pineapple.R;
import seojihyun.odya.pineapple.activities.MainActivity;
import seojihyun.odya.pineapple.activities.SignUpActivity;
import seojihyun.odya.pineapple.activities.GrouplistActivity;
import seojihyun.odya.pineapple.activities.LoginActivity;
import seojihyun.odya.pineapple.adapters.NoticeAdapter;
import seojihyun.odya.pineapple.data.LoginSharedPreferences;

/**
 * Created by SEOJIHYUN on 2016-02-17.
 */
public class DataManager extends Application {
    public boolean userType = false; // true=가이드, false=일반사용자
    public UserData userData= new UserData();; //현재 사용자
    public GroupData groupData= new GroupData();; //현재 사용자가 입장한 그룹 방
    public Vector<UserData> users = new Vector<UserData>();//현재 사용자가 입장한 그룹의 그룹원들 정보
    public Vector<GroupData> groups = new Vector<GroupData>(); //현재 서버에 존재하는 모든 그룹 정보
    private String data; //서버로부터 받은 메세지 (프로토콜)
    private Activity activity; //현재 실행중인 액티비티
    public MainActivity mainActivity;
    private BackgroundActivity backgroundActivity=null;

    /**공지추가 **//////////////////////////
    public NoticeData noticeData = new NoticeData();; //현재 사용자가 입장한 그룹 방의 공지 ?
    public Vector<NoticeData> notices = new Vector<NoticeData>(); //현재 서버에 존재하는 모든 공지 정보
    public NoticeAdapter nAdapter;

    /*Destination*/
    public DestinationData destinationData;


    public DataManager() {

    }

    // 액티비티 등록
    public void setActivity(Activity activity) {
        this.activity = activity;
        /*2016-05-17 서지현 추가*/
        if(activity.getClass().getSimpleName().equals("MainActivity")) {
            mainActivity = (MainActivity) activity;
        }
        //if(activity.getClass().getSimpleName().equals("BackgroundActivity")) backgroundActivity = (BackgroundActivity) activity;
        //if(backgroundActivity != null) backgroundActivity.setCurrentActivity(activity);
    }
    // 액티비티 전환
    public void changeActivity(String activityName) {
        Activity preActivity = activity;
        Intent intent;
        switch (activityName) {
            case "LogoActivity":
                break;
            case "LoginActivity":
                intent = new Intent(activity, LoginActivity.class);
                //intent.putExtra(Protocol.KEY_USER_PHONE, params[0]);
                //intent.putExtra(Protocol.KEY_USER_NAME, params[1]);
                intent.putExtra("UserData", userData);
                activity.startActivity(intent);
                //preActivity.overridePendingTransition(R.anim, R.anim.cycle_7);

                break;
            case "SignUpActivity":
                intent = new Intent(activity, SignUpActivity.class);
                intent.putExtra("UserData", userData);
                activity.startActivity(intent);
                break;
            case "BackgroundActivity":
                intent = new Intent(activity, BackgroundActivity.class);
                activity.startActivity(intent);
                break;
            case "GrouplistActivity" :
                intent = new Intent(activity, GrouplistActivity.class);
                activity.startActivity(intent);
                break;
            case "MainActivity":
                intent = new Intent(activity, MainActivity.class);
                intent.putExtra("UserData", userData);
                activity.startActivity(intent);
                break;
        }// end switch
        // 2016-05-04
        //if(!activity.getClass().getSimpleName().equals("BackgroundActivity")) {
            // BackgroundActivity만 종료 하지 않음
            activity.finish();
       // }

    }

    // 가이드 or 일반 여행자 구분
    public void setUserType() {
        if(groupData.getUser_phone().equals(userData.getUser_phone())) {
            //가이드일 경우
            userType=true;
        }
        else {
            //일반 사용자인 경우
            userType=false;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    // network검사
    public boolean checkNetwork() {
        try {
            ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // mobile ConnectivityManager.TYPE_MOBILE
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            }

        } catch (NullPointerException e) {
            return false;
        }

        //네트워크 꺼져있는 경우
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity);

        alertDialog.setTitle("NETWORK 사용유무셋팅");
        alertDialog
                .setMessage("Network 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIFI_SETTINGS);
                        activity.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
        return false;
    }

    // task 분류 (서버와 통신 / 어플리케이션측에서 해결)
    // (Protocol , UserData, groupData) group_name 은 한번만
    // 서버로부터 요청 시작
    public void command(String... params) {
        if(checkNetwork()) {
            String protocol = params[0];
            switch (protocol) {
                case Protocol.URL_CHECK_USER:
                    connectURL(params);
                    break;
                case Protocol.URL_SIGN_UP:
                    connectURL(params);
                    break;
                case Protocol.URL_LOGIN:
                    connectURL(params);
                    break;
                case Protocol.URL_SET_LOCATION:
                    connectURL(params);
                    break;
                case Protocol.URL_GET_MY_GROUP_USERS_DATA:
                    connectURL(params);
                    break;
                case Protocol.URL_ENTER_Group:
                    connectURL(params);
                    break;
                case Protocol.URL_EXIT_GROUP:
                    /*가이드인 경우와 일반 사용자인 경우의 구분이 필요함*/
                    //1. users clear
                    users.removeAllElements();
                    //2.groupData clear
                    groupData.updateGroup("", "", "", "", "");
                    //3. userData의 group_name을 ""로ㅗ 표시
                    userData.setGroup_name("");

                    //서버와의 통신
                    //.userData의 group_name을 NULL로 표시
                    connectURL(params);
                    break;
                case Protocol.URL_AR_GET_USER_LOCATION:
                    connectURL(params);
                    break;
                case Protocol.URL_AR_GET_ALL_USER_LOCATION:
                    connectURL(params);
                    break;
                /*2016-05-17 목적지 기능 추가*/
                case Protocol.URL_CREATE_DESTINATION:
                    connectURL3(params);
                    break;
                case Protocol.URL_GET_DESTINATION_DATA:
                    connectURL3(params);
                    break;
            } //end switch
        }// end if
    } //end command

    // url 연결 (Protocol url, String user_phone, )
    public void connectURL(String... params) {

        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog dialog;
            String uri="", user_phone="", user_name="", latitude="", longitude="", group_name="", group_pwd="", number="1";

            @Override
            protected String doInBackground(String... params) {
                try {
                    uri = params[0];
                    user_phone = params[1];
                    user_name = params[2];
                    latitude = params[3];
                    longitude = params[4];
                    group_name = params[5];
                    group_pwd = params[6];
                    number = params[7];


                    BufferedReader bufferedReader = null;

                    String sendData = URLEncoder.encode("user_phone", "UTF-8") + "=" + URLEncoder.encode(user_phone, "UTF-8");
                    sendData += "&" + URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8");
                    sendData += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8");
                    sendData += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8");
                    sendData += "&" + URLEncoder.encode("group_name", "UTF-8") + "=" + URLEncoder.encode(group_name, "UTF-8");
                    sendData += "&" + URLEncoder.encode("group_pwd", "UTF-8") + "=" + URLEncoder.encode(group_pwd, "UTF-8");
                    sendData += "&" + URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8");
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
                data = result;
                getMessage(data);
            }

            @Override
            protected void onPreExecute() {
                //dialog = ProgressDialog.show(activity, "잠시만 기다려주세요", null, true, true);
            }
        }

        String url = params[0];
        String user_phone = params[1];
        String user_name = params[2];
        String latitude = params[3];
        String longitude = params[4];
        String group_name = params[5];

        String group_pwd = "";
        String number = "1";

        if(params.length > 6) {
            group_pwd= params[6];
            number = params[7];
        }

        //UserData Update
        userData.updateUserData(user_phone, user_name, latitude, longitude, group_name);
        //groupData update
        if(Protocol.URL_CREATE_GROUP.equals(url)) {
            groupData.updateGroup(group_name, group_pwd, number, user_phone, user_name);
        }
        else if(Protocol.URL_ENTER_Group.equals(url)) {
            // 가이드 이름을 찾기위해서!
            int i=0;
            for( ; i<groups.size(); i++) {
                if(groups.get(i).getGroup_name().equals(group_name))
                    break;
            }
            groupData.updateGroup(group_name, group_pwd, number, groups.get(i).getUser_phone(), groups.get(i).getUser_name());

        }
        //Task 실행
        GetDataJSON g = new GetDataJSON();
        g.execute(url, user_phone, user_name, latitude, longitude, group_name, group_pwd, number);
        //테스트용 토스트
        //Toast.makeText(activity, user_phone+ user_name+latitude+longitude+group_name+group_pwd+number, Toast.LENGTH_SHORT).show();
    }

    /**공지추가 **//////////////////////////
    public void connectURL2(String... params) {
        class GetDataJSON2 extends AsyncTask<String, Void, String> {
            ProgressDialog dialog;
            /**추가3/31: group_name="" **/
            String uri="", notice_title="", notice_content="", notice_time="", group_name="";

            @Override
            protected String doInBackground(String... params) {
                try {
                    uri = params[0];
                    notice_title = params[1];
                    notice_content  = params[2];
                    notice_time = params[3];
                    group_name = params[4]; /**추가3/31 **/

                    BufferedReader bufferedReader = null;

                    String sendData = URLEncoder.encode("notice_title", "UTF-8") + "=" + URLEncoder.encode(notice_title, "UTF-8");
                    sendData += "&" + URLEncoder.encode("notice_content", "UTF-8") + "=" + URLEncoder.encode(notice_content, "UTF-8");
                    sendData += "&" + URLEncoder.encode("notice_time", "UTF-8") + "=" + URLEncoder.encode(notice_time, "UTF-8");
                    sendData += "&" + URLEncoder.encode("group_name", "UTF-8") + "=" + URLEncoder.encode(group_name, "UTF-8");

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
                data = result;
                getMessage(data);
            }

            @Override
            protected void onPreExecute() {
                //dialog = ProgressDialog.show(activity, "잠시만 기다려주세요", null, true, true);
            }
        }

        String url = params[0];
        String notice_title = params[1];
        String notice_content = params[2];
        String notice_time = params[3];
        String group_name = params[4];

        //noticeData update
        noticeData.updateNotice(notice_title, notice_content, notice_time, group_name);

        //Task 실행
        GetDataJSON2 g = new GetDataJSON2();
        g.execute(url, notice_title, notice_content, notice_time, group_name);
    }

    /*Destination 서지현 2016-05-17*/
    public void connectURL3(String... params) {
        class GetDataJSON3 extends AsyncTask<String, Void, String> {
            ProgressDialog dialog;
            /**추가3/31: group_name="" **/
            String uri="", group_name="", latitude="", longitude="", address="", content="", timeset="";

            @Override
            protected String doInBackground(String... params) {
                try {
                    uri = params[0];
                    group_name = params[1];
                    latitude = params[2];
                    longitude = params[3];
                    address = params[4];
                    content = params[5];
                    timeset = params[6];

                    BufferedReader bufferedReader = null;

                    String sendData = URLEncoder.encode("group_name", "UTF-8") + "=" + URLEncoder.encode(group_name, "UTF-8");
                    sendData += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8");
                    sendData += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8");
                    sendData += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(group_name, "UTF-8");
                    sendData += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
                    sendData += "&" + URLEncoder.encode("timeset", "UTF-8") + "=" + URLEncoder.encode(timeset, "UTF-8");

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
                data = result;
                getMessage(data);
            }

            @Override
            protected void onPreExecute() {
                //dialog = ProgressDialog.show(activity, "잠시만 기다려주세요", null, true, true);
            }
        }

        String url = params[0];
        String group_name = params[1];
        String latitude = params[2];
        String longitude = params[3];
        String address = params[4];
        String content = params[5];
        String timeset = params[6];

        //noticeData update
        //noticeData.updateNotice(notice_title, notice_content, notice_time, group_name);

        //Task 실행
        GetDataJSON3 g = new GetDataJSON3();
        g.execute(url, group_name, latitude, longitude, address, content, timeset);
    }






    //서버로부터 받은 JSON 객체 디코딩 - (1) Message 로 Task 분류
    // 서버로부터 메세지 받은후의 작업
    public void getMessage(String data) {
        try {
            JSONObject jsonObj = new JSONObject(data);
            String message = jsonObj.getString(Protocol.KEY_MESSAGE); // KEY_MESSAGE 값얻기
            String result = jsonObj.getString(Protocol.KEY_RESULT); // KEY_RESULT 값 얻기
            switch (message) {
                case Protocol.MESSAGE_ERROR: //0 :Error
                    Toast.makeText(activity, "MESSAGE_ERROR", Toast.LENGTH_SHORT).show();
                    break;

                case Protocol.MESSAGE_CHECK_USER: //1 :logo Activity 에서 회원 정보 이미 존재여부
                    result = jsonObj.getString(Protocol.KEY_RESULT);
                    if (Protocol.RESULT_SUCCESS.equals(result)) { //회원정보 이미 있음
                        changeActivity("LoginActivity");
                    } else {
                        changeActivity("SignUpActivity");
                    }
                    break;

                case Protocol.MESSAGE_SIGN_UP: //2 :SIGNUP ACTIVITY 에서 회원가입 성공여부(RESULT)
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        changeActivity("LoginActivity");
                    } else {
                        Toast.makeText(activity, "RESULT_FAILURE", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Protocol.MESSAGE_LOGIN: //3 :LOGIN ACTIVITY 에서 로그인 성공여부(RESULT)
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        Toast.makeText(activity, "로그인 성공", Toast.LENGTH_SHORT).show();
                        LoginSharedPreferences.setLogin(activity, userData.getUser_phone(), userData.getUser_name()); //2016-05-19 자동 로그인 - 서지현
                        //changeActivity("MapActivity");//*************************Test중***************
                        /////////0503////////////connectURL(Protocol.URL_GET_ALL_GROUP_DATA, userData.getUser_phone(), userData.getUser_name(), userData.getLatitude(), userData.getLongitude(), userData.getGroup_name());
                        changeActivity("BackgroundActivity");
                    } else {
                        Toast.makeText(activity, "RESULT_FAILURE", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Protocol.MESSAGE_MAP_SETLOCATION:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        if( !activity.getClass().getSimpleName().equals("MainActivity")) { break; } //***2016-03-07 수정
                        Toast.makeText(activity, "서버로 내위치 전송 성공", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "RESULT_FAILURE", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Protocol.MESSAGE_MAP_GETALLUSERLOCATION:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        Toast.makeText(activity, "유저들 정보받기 성공", Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray peoples = jsonObj.getJSONArray(Protocol.KEY_ARRAY);

                            for (int i = 0; i < peoples.length(); i++) {
                                JSONObject c = peoples.getJSONObject(i);
                                String user_phone = c.getString(Protocol.KEY_USER_PHONE);
                                String user_name = c.getString(Protocol.KEY_USER_NAME);
                                String latitude = c.getString(Protocol.KEY_LATITUDE);
                                String longitude = c.getString(Protocol.KEY_LONGITUDE);
                                String group_name = c.getString(Protocol.KEY_GROUP_NAME);

                                //user 추가
                                users.add(new UserData(user_phone, user_name, latitude, longitude, group_name));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(activity, "RESULT_FAILURE", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Protocol.MESSAGE_GET_ALL_GROUP_DATA:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        //groups객체 초기화
                        groups.removeAllElements();
                        //Toast.makeText(activity, "그룹 정보받기 성공", Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray groupsArray = jsonObj.getJSONArray(Protocol.KEY_ARRAY);

                            for (int i = 0; i < groupsArray.length(); i++) {
                                JSONObject c = groupsArray.getJSONObject(i);
                                String group_name = c.getString(Protocol.KEY_GROUP_NAME);
                                String group_pwd = c.getString(Protocol.KEY_GROUP_PWD);
                                String number = c.getString(Protocol.KEY_NUMBER);
                                String user_phone = c.getString(Protocol.KEY_USER_PHONE);
                                String user_name = c.getString(Protocol.KEY_USER_NAME);

                                //group 추가
                                groups.add(new GroupData(group_name, group_pwd, number, user_phone, user_name));

                            }//end for
                            if(LoginSharedPreferences.isGroup(activity)) {
                                String user_phone = LoginSharedPreferences.getPreferences(this, getString(R.string.user_phone));
                                String user_name = LoginSharedPreferences.getPreferences(this, getString(R.string.user_name));
                                String group_name = LoginSharedPreferences.getPreferences(this, getString(R.string.group_name));
                                userData.updateUserData(user_phone, user_name, "", "", group_name);
                                connectURL(Protocol.URL_ENTER_Group, user_phone,
                                        user_name, userData.getLatitude(), userData.getLongitude(), group_name);
                            }
                            else {
                                changeActivity("GrouplistActivity");
                            }
                            Toast.makeText(activity, "그룹 정보받기 성공", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Protocol.MESSAGE_ENTER_GROUP:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        Toast.makeText(activity, "그룹 입장 성공", Toast.LENGTH_SHORT).show();
                        LoginSharedPreferences.enterGroup(activity, userData.getGroup_name()); //2016-05-19 자동 로그인 - 서지현

                        //2016-03-06 테스트
                        setUserType();
                        //*********

                        changeActivity("MainActivity"); //***** 테스트중
                    } else {
                        Toast.makeText(activity, "그룹 입장 실패", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Protocol.MESSAGE_CREATE_GROUP:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        Toast.makeText(activity, "그룹 생성 성공", Toast.LENGTH_SHORT).show();
                        LoginSharedPreferences.enterGroup(activity, userData.getGroup_name()); //2016-05-19 자동 로그인 - 서지현
                        // 2016-03-06 테스트
                        setUserType(); //가이드로 설정
                        //******
                        changeActivity("MainActivity"); //***** 테스트중
                    } else {
                        Toast.makeText(activity, "그룹 생성 실패* connect 쪽", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Protocol.MESSAGE_EXIT_GROUP:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        //2016-03-06 테스트중
                        Toast.makeText(activity, "그룹 나가기 성공", Toast.LENGTH_SHORT).show();
                        // GpsInfo 종료
                        LoginSharedPreferences.exitGroup(activity); //2016-05-19 자동 로그인 - 서지현
                        //GroupList화면으로 이동
                        changeActivity("GrouplistActivity");
                    }else {
                        Toast.makeText(activity, "그룹 나가기 실패", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Protocol.MESSAGE_GET_MY_GROUP_USERS_DATA:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        if( !activity.getClass().getSimpleName().equals("MainActivity")) { break; } //***2016-03-07 수정
                        Toast.makeText(activity, "그룹원들의 정보받기 성공", Toast.LENGTH_SHORT).show();
                        //users 객체 클리어
                        users.removeAllElements();
                        try {
                            JSONArray peoples = jsonObj.getJSONArray(Protocol.KEY_ARRAY);

                            for (int i = 0; i < peoples.length(); i++) {
                                JSONObject c = peoples.getJSONObject(i);
                                String user_phone = c.getString(Protocol.KEY_USER_PHONE);
                                String user_name = c.getString(Protocol.KEY_USER_NAME);
                                String latitude = c.getString(Protocol.KEY_LATITUDE);
                                String longitude = c.getString(Protocol.KEY_LONGITUDE);
                                String group_name = c.getString(Protocol.KEY_GROUP_NAME);

                                //user 추가
                                users.add(new UserData(user_phone, user_name, latitude, longitude, group_name));
                            }//end for

                            //**********************2016_02_29 테스트중 (위치 업데이트 주기 때문에 스레드 런타임 충돌발생)
                            if(activity.getClass().getSimpleName().equals("MainActivity")) {
                                ((MainActivity)activity).marking();
                                //((MainActivity) activity).notifyDataChanged();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case Protocol.MESSAGE_CREATE_DESTINATION:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        Toast.makeText(activity, "목적지 추가 성공~~~.", Toast.LENGTH_SHORT).show();
                        if( !activity.getClass().getSimpleName().equals("MainActivity")) { break; }
                        //connectURL2(Protocol.URL_GET_ALL_NOTICE_DATA, "", "", "", groupData.getGroup_name());
                        command(Protocol.URL_GET_DESTINATION_DATA, groupData.getGroup_name(), "", "", "", "", "");

                    } else {
                        Toast.makeText(activity, "목적지 추가 실패", Toast.LENGTH_SHORT).show();
                    }
                    break;
                /*2016-05-17*/
                case Protocol.MESSAGE_GET_DESTINATION_DATA:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        Toast.makeText(activity, "목적지 데이터 받기 성공", Toast.LENGTH_SHORT).show();
                        //notice 받기
                        //notices.removeAllElements();
                        try {
                            JSONArray array_destination = jsonObj.getJSONArray(Protocol.KEY_ARRAY);

                            for (int i = 0; i < array_destination.length(); i++) {
                                JSONObject c = array_destination.getJSONObject(i);
                                String group_name = c.getString(Protocol.KEY_GROUP_NAME);
                                String latitude = c.getString(Protocol.KEY_LATITUDE);
                                String longitude = c.getString(Protocol.KEY_LONGITUDE);
                                String address = c.getString(Protocol.KEY_ADDRESS);
                                String content = c.getString(Protocol.KEY_CONTENT);
                                String timeset = c.getString(Protocol.KEY_TIMESET);

                                destinationData = new DestinationData(group_name, latitude, longitude, address, content, timeset);
                            }
                            // 2016-05-17 서지현 수정 (아 래 nAdapter 삭제하고 밑에 추가했어)
                            if(activity.getClass().getSimpleName().equals("MainActivity")) {
                                final MyHandler mHandler = new MyHandler((MainActivity) activity);
                                //((MainActivity)activity).markingDestination();
                                mHandler.handleMessage(mHandler.obtainMessage());
                                Toast.makeText(activity, "목적지 마킹 성공", Toast.LENGTH_SHORT).show();
                                //((MainActivity) activity).notifyDataChanged();
                            }
                            else { //테스트용 삭제 해도됨
                                Toast.makeText(activity, "목적지 마킹 실패", Toast.LENGTH_SHORT).show();
                                Toast.makeText(activity, activity.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(activity, "목적지 데이터 받기 실패", Toast.LENGTH_SHORT).show();
                    }
                    break;
                /**공지추가 **///////////////////////////////////////////////////////////////////////
                case Protocol.MESSAGE_CREATE_NOTICE:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        if( !activity.getClass().getSimpleName().equals("MainActivity")) { break; }
                        Toast.makeText(activity, "공지생성 성공", Toast.LENGTH_SHORT).show();
                        connectURL2(Protocol.URL_GET_ALL_NOTICE_DATA, "", "", "", groupData.getGroup_name());

                    } else {
                        Toast.makeText(activity, "공지생성 실패", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Protocol.MESSAGE_GET_MY_NOTICE_DATA:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        Toast.makeText(activity, "공지받기 성공", Toast.LENGTH_SHORT).show();
                        Context context = activity;
                        MainActivity mainActivity = (MainActivity)context;
                        //notice 받기
                        notices.removeAllElements();
                        try {
                            JSONArray array_notice = jsonObj.getJSONArray(Protocol.KEY_ARRAY_NOTICE);

                            for (int i = 0; i < array_notice.length(); i++) {
                                JSONObject c = array_notice.getJSONObject(i);
                                String notice_title = c.getString(Protocol.KEY_NOTICE_TITLE);
                                String notice_content = c.getString(Protocol.KEY_NOTICE_CONTENT);
                                String notice_time = c.getString(Protocol.KEY_NOTICE_TIME);
                                String notice_group_name = c.getString(Protocol.KEY_NOTICE_GROUP_NAME);
                                //notices 추가
                                notices.add(new NoticeData(notice_title, notice_content, notice_time, notice_group_name));

                            }
                            nAdapter = mainActivity.getNoticeAdapter();
                            nAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(activity, "공지받기 실패", Toast.LENGTH_SHORT).show();
                    }
                    break;
                /**추가4/14 **/
                case Protocol.MESSAGE_DELETE_NOTICE:
                    if (Protocol.RESULT_SUCCESS.equals(result)) {
                        if( !activity.getClass().getSimpleName().equals("MainActivity")) { break; }
                        Toast.makeText(activity, "공지가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        connectURL2(Protocol.URL_GET_ALL_NOTICE_DATA, "", "", "", groupData.getGroup_name());

                    } else {
                        Toast.makeText(activity, "공지삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // 핸들러 객체 만들기 2016-05-19 UI 스레드
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mainActivity;
        public MyHandler(MainActivity activity) {
            mainActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mainActivity.get();

            if (activity != null) {
                //activity.handleMessage(msg);
                activity.markingDestination();
            }
        }
    }

}
