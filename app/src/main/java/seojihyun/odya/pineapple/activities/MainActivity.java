package seojihyun.odya.pineapple.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import seojihyun.odya.pineapple.ar.MixView;
import seojihyun.odya.pineapple.protocol.GpsInfo;
import seojihyun.odya.pineapple.R;
import seojihyun.odya.pineapple.adapters.UserAdapter;
import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.protocol.Protocol;
import seojihyun.odya.pineapple.protocol.UserData;

public class MainActivity extends AppCompatActivity {

    private DataManager dataManager;

    /* 탭 관련 변수 */
    TabHost mtabHost;

    /*네비게이션 드로어 관련 변수*/
    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ListView userListDrawer; //서지현

    /*Map 관련 변수*/
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GpsInfo gps;
    private Button gpsBtn, insertBtn, getBtn, markBtn;
    private double lat, lon;

    /* list 관련 변수*/
    private ListView userList;
    private UserData selectedUser;
    private UserAdapter userAdapter, userAdapter1;

    /*infowindow 관련 변수*/
    HashMap<Marker, UserData> mMarkersHashMap; //마커를 통해 UserData find
    HashMap<UserData, Marker> mUserDatasHashMap; // UserData를 통해 마커 find // 2016-03-31 서지현
    MapInfoWindowAdapter mMapInfoWindowAdapter; //어뎁터

    /*AR 관련 변수*/
    Button arButton;



    /* create 순서 (Map Tab 기준)
    * 1. dataManager 객체 얻기
    * 2. HashMap<Marker, UserData> 와 users 셋팅                       ////////초기화 ( 탭호스트, 네비게이션 드로어, 탭부분)
    * 3.내위치와 그룹원들의 위치정보 얻어오기 (서버와 연결)
    * 4. HashMap에 모두 add
    * 5. mMap.setInfowindowAdapter(new Adapter());
    * 6. marker.showinfowindow();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 0.UserManager 객체 얻기
        dataManager = (DataManager) getApplicationContext();
        dataManager.setActivity(this);

        if (dataManager.userType) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User Type")        // 제목 설정
                    .setMessage("당신은 가이드 입니다")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        // 취소 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User Type")        // 제목 설정
                    .setMessage("당신은 손님 입니다")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        // 취소 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();
        }

        // 1. 네비게이션 드로어 초기화
        initDrawer();
        // 2. 탭호스트 부분 초기화
        initTabHost();
        // 3. list 탭 부분 초기화
        initList();


        // 4. map 탭 부분 초기화
        setUpMapIfNeeded();
        // 5. gpsInfo 초기화
        mMap.clear();
        gps = new GpsInfo(MainActivity.this);
        getGps(); //********************테스트중


        //Map 관련
        //서버간의 통신관련 변수




        /*map tab 구현 부분*/
        mMarkersHashMap = new HashMap<Marker, UserData>();
        mUserDatasHashMap = new HashMap<UserData, Marker>(); //2016-03-31 서지현

        //어뎁터 부착
        mMapInfoWindowAdapter = new MapInfoWindowAdapter();
        mMap.setInfoWindowAdapter(mMapInfoWindowAdapter);

    }


    // 1. 초기화 - 탭호스트부분
    public void initTabHost() {

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Map"); //탭버튼에 들어갈 이름
        tabHost.addTab(spec); //탭호스트에 탭 추가

        spec = tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("List");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("notice");
        tabHost.addTab(spec);

        // tab1부분이 초기화면으로 설정
        tabHost.setCurrentTab(0);
    }

    // 2. 초기화 - 네비게이션 드로어 부분
    public void initDrawer() {
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // 네비게이션 드로어 안에 user List 부착
        userListDrawer = (ListView) findViewById(R.id.navigation_list);

        userAdapter = new UserAdapter(this, R.layout.item_user, dataManager.users, dataManager); //서지현
        userListDrawer.setAdapter(userAdapter);
        //데이터가 변했을때 호출
        userAdapter.notifyDataSetChanged();

        ////////

        TextView user_name = (TextView) findViewById(R.id.user_name);
        TextView user_phone = (TextView) findViewById(R.id.user_phone);

        user_name.setText(dataManager.userData.getUser_name());
        user_phone.setText(dataManager.userData.getUser_phone());

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    /*
                    case R.id.navigation_item_home:
                        Toast.makeText(getApplicationContext(), "Home Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_item_notice:
                        Toast.makeText(getApplicationContext(), "notice Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_item_setting:
                        Toast.makeText(getApplicationContext(), "setting Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_item_help:
                        Toast.makeText(getApplicationContext(), "help Selected", Toast.LENGTH_SHORT).show();
                        return true;
                        */
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    //case R.id.inbox:
                    //Toast.makeText(getApplicationContext(),"Inbox Selected",Toast.LENGTH_SHORT).show();
                    //ContentFragment fragment = new ContentFragment();
                    //android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    //fragmentTransaction.replace(R.id.frame,fragment);
                    //fragmentTransaction.commit();
                    // return true;

                    // For rest of the options we just show a toast on click

                    //case R.id.starred:
                    //   Toast.makeText(getApplicationContext(),"Stared Selected",Toast.LENGTH_SHORT).show();
                    //   return true;
                    //case R.id.sent_mail:
                    // Toast.makeText(getApplicationContext(),"Send Selected",Toast.LENGTH_SHORT).show();
                    // return true;
                    // case R.id.drafts:
                    // Toast.makeText(getApplicationContext(),"Drafts Selected",Toast.LENGTH_SHORT).show();
                    // return true;
                    //case R.id.allmail:
                    //Toast.makeText(getApplicationContext(),"All Mail Selected",Toast.LENGTH_SHORT).show();
                    //return true;
                    //case R.id.trash:
                    //Toast.makeText(getApplicationContext(),"Trash Selected",Toast.LENGTH_SHORT).show();
                    //return true;
                    //case R.id.spam:
                    // Toast.makeText(getApplicationContext(),"Spam Selected",Toast.LENGTH_SHORT).show();
                    //return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);


        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    // 3. 초기화 - list 탭 부분
    public void initList() {
        //list 관련
        userList = (ListView) findViewById(R.id.list_user);

        //userAdapter = new UserAdapter(this, R.layout.list_user_item, dataManager.users); // 2016-03-28 어뎁터 하나로 사용 - 테스트중 _ 서지현
        userList.setAdapter(userAdapter);
        //데이터가 변했을때 호출
        userAdapter.notifyDataSetChanged();
    }

    // MAP Fragment 관련 메소드
    // 1. setting
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpMap() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
        // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    //각 버튼별 리스너 // (테스트용)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_map_setting:
                getGps();
                break;
        }
    }

    /* Map 초기화 - 1. getGps
    // Map 초기화 - 2. 서버로 나의 위치 전송
    // Map 초기화 - 3. 서버로 그룹원들의 위치정보 요청
    // Map 초기화 - 4. group원들 지도에 마크
    */

    // Map 초기화 - 1. getGps
    public void getGps() {
        gps.getLocation(); // 2016-03-07 수정
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            //**************************** insert를 위한 적용
            lat = latitude;
            lon = longitude;
            // UserData  업데이트
            String lll = Double.toString(lat);
            String loo = Double.toString(lon);
            //****************************


            // Showing the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Map을 zoom 합니다
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            //마커 설정
            MarkerOptions optFirst = new MarkerOptions();
            // 마커 위치 지정
            optFirst.position(latLng); //위도*경도
            //optFirst.title(dataManager.userData.getUser_name()); //제목 미리보기
            //optFirst.snippet(dataManager.userData.getUser_phone());
            optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3));


            //optFirst.icon(BitmapDescriptorFactory.fromResource(R.dra));

            //마커를 추가하고 말풍선 표시한것을 보여주도록 호출
            //mMap.addMarker(optFirst).showInfoWindow();


            Toast.makeText(
                    getApplicationContext(),
                    "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                    Toast.LENGTH_LONG).show();
        } else {

            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();

        }

        setMyLocation(lat, lon); //*******테스트중
    }

    // Map 초기화 - 2. 서버로 나의 위치 전송
    public void setMyLocation(double lat, double lon) {
        String latitude = Double.toString(lat);
        String longitude = Double.toString(lon);
        String phone = dataManager.userData.getUser_phone();
        //dataManager.connectURL(Protocol.URL_SET_LOCATION, phone, dataManager.userData.getUser_name(), latitude, longitude, dataManager.userData.getGroup_name());
        dataManager.command(Protocol.URL_SET_LOCATION, phone, dataManager.userData.getUser_name(), latitude, longitude, dataManager.userData.getGroup_name());
        //********테스트중
        getGroupLocation();
    }

    // Map 초기화 - 3. 서버로 그룹원들의 위치정보 요청
    public void getGroupLocation() {
        dataManager.command(Protocol.URL_GET_MY_GROUP_USERS_DATA, dataManager.userData.getUser_phone(), dataManager.userData.getUser_name(), dataManager.userData.getLatitude(), dataManager.userData.getLongitude(), dataManager.userData.getGroup_name());

    }

    // Map 초기화 - 4. group원들 지도에 마크 // dataManager에서 3번 초기화 완료후 호출함
    public void marking() {
        // 마크 remove
        mMap.clear();
        for (int i = 0; i < dataManager.users.size(); i++) {
            UserData u = dataManager.users.get(i);
            String name = u.getUser_name();
            String phone = u.getUser_phone();
            Double lat = Double.parseDouble(u.getLatitude());
            Double lon = Double.parseDouble(u.getLongitude());
            LatLng latLng = new LatLng(lat, lon);

            //마커 설정
            MarkerOptions optFirst = new MarkerOptions();
            optFirst.position(latLng); //위도*경도
            optFirst.title(phone); //제목 미리보기
            optFirst.snippet(name);
            //가이드와 일반 사용자들의 마커아이콘 차이
            if (dataManager.groupData.getUser_phone().equals(dataManager.users.get(i).getUser_phone())) {
                //가이드일 경우
                optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin1));
            } else if (dataManager.userData.getUser_phone().equals(dataManager.users.get(i).getUser_phone())) {
                //자기 자신인 경우
                optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3));
            } else {
                //일반 사용자인 경우
                optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2));
            }

            Marker currentMarker = mMap.addMarker(optFirst);
            mMarkersHashMap.put(currentMarker, u);
            mUserDatasHashMap.put(u, currentMarker); //2016-03-31 서지현 - 네비게이션 드로어 "클릭"버튼 기능을 위한 설정

            mMap.setInfoWindowAdapter(mMapInfoWindowAdapter); ///*************2016_02-27테스트중

            //mMap.addMarker(optFirst).showInfoWindow();
        }
    }

    //2016-03-31 테스트중_서지현
    /* 네비게이션드로어에서 클릭버튼 누를 경우 해당 marker focus 처리 */

    public void focusMarker(UserData item) {
        // UserAdapter.java에서 호출됨
        // Showing the current location in Google Map
        //클릭을 누른 해당 User의 마커
        Marker focusedUserMarker = mUserDatasHashMap.get(item);

        // 카메라 이동 해당 UserMarker의 위치로
        mMap.moveCamera(CameraUpdateFactory.newLatLng(focusedUserMarker.getPosition()));

        // Map을 zoom 합니다
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        // 해당 마커 focus
        focusedUserMarker.showInfoWindow();

        // ********************************2016-03-31 아 ..이게문제야..
        mMapInfoWindowAdapter.getInfoContents(focusedUserMarker);

        //네비게이션 드로어 닫기
        drawerLayout.closeDrawers();

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        // Map Tab으로 이동
        if (tabHost.getCurrentTab() != 0) {
            tabHost.setCurrentTab(0);
        }

    }


    // 2016-03-28 테스트중_서지현
    /* 모든 데이터 업데이트 주기 셋팅
    * 1. gps 셋팅 - 10초주기 GpsInfo 클래스 참고
    * 2. List 셋팅 - 새로운 사용자 입장 정보 update
    * 3. Notice 셋팅 - 공지 올라오거나 삭제될때마다 update
    * */
    public void updateAllData() {
        //1. gps셋팅은 이미 되있음

        //데이터가 변했을때 호출
        userAdapter.notifyDataSetChanged();
    }


    // custom infowindow Adapter
    public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public final View mapContentsView;
        Marker selectedMarker;

        MapInfoWindowAdapter() {
            mapContentsView = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
            //custom infowindow 크기 지정 //레이아웃부분에서 적용이 안됨.(왜지)
            mapContentsView.setMinimumHeight(300);
            mapContentsView.setMinimumWidth(350);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            // 선택된 마커 *** 테스트중
            selectedMarker = marker;
            // 텍스트, 이미지등 설정
            try {
                TextView name = (TextView) mapContentsView.findViewById(R.id.text_infowindow_name);
                TextView phone = (TextView) mapContentsView.findViewById(R.id.text_infowindow_phone);

                ///**********2016-2-29 테스트중 (프로필 fragment 관련)
                TextView name2 = (TextView) findViewById(R.id.text_infowindow_name);
                TextView phone2 = (TextView) findViewById(R.id.text_infowindow_phone);
                LinearLayout layout_profile = (LinearLayout) findViewById(R.id.layout_map_profile);
                Button button_ar = (Button) findViewById(R.id.button_infowindow_ar);
                Button button_call = (Button) findViewById(R.id.button_infowindow_call);
                layout_profile.setVisibility(View.VISIBLE);
                //*************

                final UserData focusUserData = mMarkersHashMap.get(marker);
                name.setText(focusUserData.getUser_name());
                phone.setText(focusUserData.getUser_phone());

                // **********2016-2-29 테스트중 (프로필 fragment 관련)
                name2.setText(focusUserData.getUser_name());
                phone2.setText(focusUserData.getUser_phone());
                button_ar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //AR로 연결 - activity
                        //2016-05-09
                        String user_phone_to_track = ((TextView) findViewById(R.id.text_infowindow_phone)).getText().toString();
                        String user_name_to_track = ((TextView) findViewById(R.id.text_infowindow_name)).getText().toString();
                        Toast.makeText(
                                getApplicationContext(),
                                user_name_to_track + "님의 위치 추적(AR)",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), MixView.class);
                        startActivity(intent);
                        finish();
                    }
                });
                button_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + focusUserData.getUser_phone()));
                        startActivity(intent);
                    }
                });
                //**********
            }catch(NullPointerException e) {
                Toast.makeText(getApplicationContext(), "InfoWindow NullException Error", Toast.LENGTH_SHORT).show();
            }
            //phone.setText(dataManager.userData.getUser_phone());
            // name.setText(dataManager.userData.getUser_name()); //NullPointException 임... 해결 필요함 --> 해결책: view.findViewById() 로 해야됨 (not findViewById() )
            // phone.setText(dataManager.userData.getUser_phone());
            //프로필 설정 ********* 추가 예정
            return mapContentsView;
        }

    }// end CustomInfoWindow Adapter


    // 뒤로가기 버튼 클릭시 처리
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("종료 확인 대화 상자")        // 제목 설정
                .setMessage("그룹을 나가시 겠습니까?")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        //finish();
                        dataManager.command(Protocol.URL_EXIT_GROUP, dataManager.userData.getUser_phone(), dataManager.userData.getUser_name(), dataManager.userData.getLatitude(), dataManager.userData.getLongitude(), dataManager.userData.getGroup_name());
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
        //super.onBackPressed();
    }

    // 2016-05-09 서지현

    class ArClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String user_phone_to_track = ((TextView) findViewById(R.id.text_infowindow_phone)).getText().toString();
            String user_name_to_track = ((TextView) findViewById(R.id.text_infowindow_name)).getText().toString();
            Toast.makeText(
                    getApplicationContext(),
                    user_name_to_track + "님의 위치 추적(AR)",
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), MixView.class);
            startActivity(intent);
            finish();
        }
    }
    //id button_infowindow_ar 에서 onClick시 호출
    // 특정 유저 위치 추적 (AR로 연결)
    public void particularUserLocation(View view) {
        String user_phone_to_track = ((TextView) findViewById(R.id.text_infowindow_phone)).getText().toString();
        String user_name_to_track = ((TextView) findViewById(R.id.text_infowindow_name)).getText().toString();
        Toast.makeText(
                getApplicationContext(),
                user_name_to_track + "님의 위치 추적(AR)",
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MixView.class);
        startActivity(intent);
        this.finish();
        //dataManager.command(Protocol.URL_AR_GET_USER_LOCATION, user_phone_to_track, "", "", "", "");

    }

}
