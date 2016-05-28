package seojihyun.odya.pineapple.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import seojihyun.odya.pineapple.NotesAdapter;
import seojihyun.odya.pineapple.SOSActivity;
import seojihyun.odya.pineapple.SharedPreferencesManager;
import seojihyun.odya.pineapple.WakeLocker;
import seojihyun.odya.pineapple.adapters.NoticeAdapter;
import seojihyun.odya.pineapple.ar.view.MixView;
import seojihyun.odya.pineapple.dialogs.NoticeDialog;
import seojihyun.odya.pineapple.protocol.DestinationData;
import seojihyun.odya.pineapple.protocol.GpsInfo;
import seojihyun.odya.pineapple.R;
import seojihyun.odya.pineapple.adapters.UserAdapter;
import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.protocol.NoticeData;
import seojihyun.odya.pineapple.protocol.Protocol;
import seojihyun.odya.pineapple.protocol.UserData;

//네비게이션 드로어 관련

import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;



import static seojihyun.odya.pineapple.CommonUtilities.DISPLAY_MESSAGE_ACTION;

public class MainActivity extends AppCompatActivity {

    private DataManager dataManager;

    /*네비게이션 드로어 관련 변수 - new */
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    /* 탭 관련 변수 */
    TabHost mtabHost;
    int prevTab = 0;

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
    private UserAdapter userAdapter;

    /*infowindow 관련 변수*/
    HashMap<Marker, UserData> mMarkersHashMap; //마커를 통해 UserData find
    HashMap<UserData, Marker> mUserDatasHashMap; // UserData를 통해 마커 find // 2016-03-31 서지현
    MapInfoWindowAdapter mMapInfoWindowAdapter; //어뎁터

    /*AR 관련 변수*/
    Button arButton;

    /*notice 관련 변수*/
    ListView noticeList;
    NotesAdapter noticeAdapter;
    NoticeDialog notice;
    View.OnClickListener noticelistener;

    /*Destination 관련 변수*/
    MarkerOptions destinationMarker;




    /* create 순서 (Map Tab 기준)
    * 1. dataManager 객체 얻기
    * 2. HashMap<Marker, UserData> 와 users 셋팅                       ////////초기화 ( 탭호스트, 네비게이션 드로어, 탭부분)
    * 3.내위치와 그룹원들의 위치정보 얻어오기 (서버와 연결)
    * 4. HashMap에 모두 add
    * 5. mMap.setInfowindowAdapter(new Adapter());
    * 6. marker.showinfowindow();*/
    /**
     * Receiving push messages
     * */
    private  BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString("message");
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            //lblMessage.append(newMessage + "\n");
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            String message = intent.getExtras().getString("price"); //기본
            String sos =  intent.getExtras().getString("sos"); //sos
            String destination = intent.getExtras().getString("destination"); //destination
            String notice = intent.getExtras().getString("notice"); // notice 일반 공지
            String user = intent.getExtras().getString("user"); // user update


            // 2016-05-17 메세지 분류

            if(sos != null) {
                // 서지현
                Intent i = new Intent(context, SOSActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("message", sos);
                context.startActivity(i);

                return;
            }
            if(newMessage.equals("destination")) {
                getDestination();
                return;

            }
            if(newMessage.equals("notice")) {
                //initNotice(); //2016-05-19
                getNoticeData();
                return;
            }


            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            //Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataManager.setActivity(this);
    }
    public void initBottomSheet() {
        // 1 . bottom _ sheet 설정
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.layout_map_profile));
        /* 레아이웃에 설정 되어있음
        behavior.setPeekHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 72.f, getResources().getDisplayMetrics()));
        behavior.setHideable(false);
        */

        // 색상 지정
        //normal state color
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.findappleAccent)));
        /// pressed state color
        fab.setRippleColor(getResources().getColor(R.color.findappleAccent));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.layout_map_profile));
                switch(behavior.getState()) {
                    case BottomSheetBehavior.STATE_EXPANDED:// if bottom_sheet is opened
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    // if bottom_sheet is closed
                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 0.UserManager 객체 얻기
        dataManager = (DataManager) getApplicationContext();
        dataManager.setActivity(this);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));


        //2016-05-22 UI 관련 - 서지현
        initBottomSheet();

        // new - 네비게이션 드로어 재디자인
        initNavigationDrawer(savedInstanceState);

        if (dataManager.userType) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User Type")        // 제목 설정
                    .setMessage("가이드 입니다")        // 메세지 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                        // 확인 버튼 클릭시 설정
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("네", new DialogInterface.OnClickListener() {
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
        //initDrawer();
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



        /**공지추가 **//////////////////////////
        // 4. notice 탭 부분 초기화
        initNotice();
        //Map 관련
        //서버간의 통신관련 변수

        initDestination();



        /*map tab 구현 부분*/
        mMarkersHashMap = new HashMap<Marker, UserData>();
        mUserDatasHashMap = new HashMap<UserData, Marker>(); //2016-03-31 서지현

        //어뎁터 부착
        mMapInfoWindowAdapter = new MapInfoWindowAdapter();
        mMap.setInfoWindowAdapter(mMapInfoWindowAdapter);

    }

    // new - 네비게이션 드로어 초기화
    public void initNavigationDrawer(Bundle savedInstanceState) {
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("파인애플");

        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        final IProfile profile = new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");
        final IProfile profile2 = new ProfileDrawerItem().withName("Bernat Borras").withEmail("alorma@github.com").withIcon(Uri.parse("https://avatars3.githubusercontent.com/u/887462?v=3&s=460"));

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile, profile2
                )
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("HOME").withIcon(R.drawable.drawer_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName("가이드").withIcon(R.drawable.drawer_guide).withBadge("guide").withBadgeStyle(new BadgeStyle(Color.RED, Color.RED)).withIdentifier(2).withSelectable(false),
                        new PrimaryDrawerItem().withName("목적지").withIcon(R.drawable.drawer_destination).withIdentifier(3),
                        new PrimaryDrawerItem().withName("일행추적").withIcon(R.drawable.drawer_guide).withBadge("all").withBadgeStyle(new BadgeStyle(Color.RED, Color.RED)).withIdentifier(4),
                        new PrimaryDrawerItem().withDescription("A more complex sample").withName("두번째줄").withIcon(R.drawable.drawer_mygroup).withIdentifier(5),
                        new PrimaryDrawerItem().withName("모두추적").withIcon(R.drawable.drawer_guide).withBadge("22").withBadgeStyle(new BadgeStyle(Color.RED, Color.RED)).withIdentifier(3).withIdentifier(6),
                        new SectionDrawerItem().withName("섹션 헤더"),
                        new SecondaryDrawerItem().withName("open Source").withIcon(R.drawable.drawer_mygroup),
                        new SecondaryDrawerItem().withName("Contact").withIcon(R.drawable.drawer_exit).withTag("Bullhorn")
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                        }
                        //we do not consume the event and want the Drawer to continue with the event chain
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .withSliderBackgroundColor(getResources().getColor(R.color.findappleBase)) // 네비게이션 드로어 배경 색깔
                .build();


        //get the CrossfadeDrawerLayout which will be used as alternative DrawerLayout for the Drawer
        //the CrossfadeDrawerLayout library can be found here: https://github.com/mikepenz/CrossfadeDrawerLayout
        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        //add second view (which is the miniDrawer)
        final MiniDrawer miniResult = result.getMiniDrawer();
        //build the view for the MiniDrawer
        View view = miniResult.build(this);
        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(getResources().getColor(R.color.findappleBackground)); // 네비게이션드로어 배경 색깔
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    // 1. 초기화 - 탭호스트부분
    public void initTabHost() {

        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        ImageView tabMap = new ImageView(this); // 탭버튼 초기 이미지 부여
        tabMap.setImageResource(R.drawable.tab_map);
        ImageView tabNo = new ImageView(this);
        tabNo.setImageResource(R.drawable.tab_list);
        ImageView tabNotice = new ImageView(this);
        tabNotice.setImageResource(R.drawable.tab_notice);

        TabHost.TabSpec spec = tabHost.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("", getDrawable(R.drawable.tab_map)); //탭버튼 클릭 시 이미지 변환
        tabHost.addTab(spec); //탭호스트에 탭 추가

        spec = tabHost.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("", getDrawable(R.drawable.tab_list));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("", getDrawable(R.drawable.tab_notice));
        tabHost.addTab(spec);

        // tab1부분이 초기화면으로 설정
        tabHost.setCurrentTab(0);

        //탭 색상
/*
        TabWidget widget = tabHost.getTabWidget();
        for(int i = 0; i < widget.getChildCount(); i++) {
            View v = widget.getChildAt(i);

            // Look for the title view to ensure this is an indicator and not a divider.
            TextView tv = (TextView)v.findViewById(android.R.id.title);
            if(tv == null) {
                continue;
            }
            v.setBackgroundResource(R.drawable.tab_selector);
        }
        */

    }


    // 3. 초기화 - list 탭 부분
    public void initList() {
        //list 관련
        userList = (ListView) findViewById(R.id.list_user);
        userAdapter = new UserAdapter(this, R.layout.item_user, dataManager.users, dataManager); //2016-05-16 서지현 추가
        //userAdapter = new UserAdapter(this, R.layout.list_user_item, dataManager.users); // 2016-03-28 어뎁터 하나로 사용 - 테스트중 _ 서지현
        userList.setAdapter(userAdapter);
        //데이터가 변했을때 호출
        userAdapter.notifyDataSetChanged();
    }

    /**공지추가 **//////////////////////////
    public void getNoticeData() {
        //저장되어 있는 공지 가져오기
        dataManager.connectURL2(Protocol.URL_GET_ALL_NOTICE_DATA, "", "", "", dataManager.groupData.getGroup_name());
        if(noticeAdapter != null) {
            noticeAdapter.updateData(this.getApplicationContext());
        }
        else {
            //초기 셋팅시 initNotice()
        }
    }
    // 4. 초기화 - notice 탭 부분
    public void initNotice() {

        getNoticeData();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_notice);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL));
        noticeAdapter = new NotesAdapter(this, 9);
        recyclerView.setAdapter(noticeAdapter);

        if(dataManager.userType) { // 2016-05-26 서지현
            // 가이드 인 경우 FloatingActionButton visibility 수정
            // 즉 가이드만 공지 생성가능
            FloatingActionMenu createNoticeMenu = (FloatingActionMenu) findViewById(R.id.menu_create_notice);
           createNoticeMenu.setVisibility(View.VISIBLE);
        }

        /*
        noticeList = (ListView)findViewById(R.id.list_notice);
        noticeAdapter = new NoticeAdapter(this,R.layout.list_notice_item, dataManager.notices);
        noticeList.setAdapter(noticeAdapter);
        noticeAdapter.notifyDataSetChanged();

        //저장되어 있는 공지 가져오기
        dataManager.connectURL2(Protocol.URL_GET_ALL_NOTICE_DATA, "", "", "", dataManager.groupData.getGroup_name());

        //공지내용 불러오기 /리스너
        noticeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                view.setBackgroundResource(R.color.dark_gray);
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(view.getContext());

                ///4/30 추가: 공지내용보기 back key - 리스트뷰 배경색 변경
                alertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog,
                                         int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            view.setBackgroundResource(R.color.list_gray);
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

                ////4/30 추가: 공지내용보기 title 배경색 설정 - xml 생성*
                //title 만 설정
                LayoutInflater inflater = getLayoutInflater();
                View view1 = inflater.inflate(R.layout.dialog_notice_content, null);
                alertDlg.setCustomTitle(view1);

                //공지내용 가져오기
                NoticeData item = dataManager.notices.get(position);
                String content = item.getNotice_content();
                alertDlg.setMessage(content);

                alertDlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        view.setBackgroundResource(R.color.list_gray);
                    }
                });

                alertDlg.show();
            }
        });
        //공지 삭제 (길게 눌렀을 때 삭제 다이얼로그)
        noticeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                view.setBackgroundResource(R.color.dark_gray);
                ///5/12 추가 - 가이드만 삭제
                if(dataManager.userType){
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(view.getContext());
                    alertDlg.setTitle("공지를 삭제하시겠습니까?");
                    //서버에 보낼 공지시간, 방이름 가져오기
                    NoticeData item = dataManager.notices.get(position);
                    final String time = item.getNotice_time(); //서버에 보내
                    final String groupN = item.getNotice_groupN();

                    //4/29//예,아니요 위치 바꿈 //
                    alertDlg.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            view.setBackgroundResource(R.color.list_gray);
                            dialog.dismiss();
                        }
                    });
                    alertDlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //공지 list-item 삭제
                            dataManager.connectURL2(Protocol.URL_DELETE_NOTICE, "", "",
                                    time, groupN);
                            view.setBackgroundResource(R.color.list_gray);
                        }
                    });

                    //4/30 추가: 공지내용보기 back key - 리스트뷰 배경색 변경
                    alertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        public boolean onKey(DialogInterface dialog,
                                             int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                view.setBackgroundResource(R.color.list_gray);
                                dialog.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                    alertDlg.show();
                    return true;
                }
                else{ //가이드만 삭제할 수 있다 는 다이얼로그
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(view.getContext());
                    alertDlg.setTitle("삭제 권한이 없습니다.");
                    alertDlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            view.setBackgroundResource(R.color.list_gray);
                            dialog.dismiss();
                        }
                    });
                    alertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        public boolean onKey(DialogInterface dialog,
                                             int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                view.setBackgroundResource(R.color.list_gray);
                                dialog.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                    alertDlg.show();
                    return true;
                }
            }
        });
        */
    }

    //DM에서 호출
    public NotesAdapter getNoticeAdapter(){return noticeAdapter;}

    public void dialog_notice(){
        //2-1. 공지 리스너
        noticelistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notice.getNoticeTitle().equals("") || notice.getNoticeContent().equals("")){
                    Toast.makeText(getApplicationContext(), "제목 또는 내용을 작성하세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    notice.dismiss();
                    Toast.makeText(getApplicationContext(), "제목내용 add성공", Toast.LENGTH_SHORT).show();
                    //서버에 공지 제목,내용 ,'시간' 보내기
                    //dataManager.noticeData.updateNotice(notice.getNoticeTitle(), notice.getNoticeContent(), notice.getNoticeTime(), dataManager.groupData.getGroup_name());
                    dataManager.connectURL2(Protocol.URL_CREATE_NOTICE, notice.getNoticeTitle(), notice.getNoticeContent(),
                            notice.getNoticeTime(), dataManager.groupData.getGroup_name());
                }
            }
        };
        //가이드인지 파악
        if(dataManager.userType) {
            //공지 입력 다이얼로그
            notice = new NoticeDialog(MainActivity.this, 1, noticelistener);
            notice.show(); //다이얼로그 호출
        }
        else{ //작성할수 없다 공지 띄우고 끝
            NoticeDialog notice2 = new NoticeDialog(MainActivity.this, 0);
            notice2.show(); //다이얼로그 호출
        }
    }


    //2016-05-16 서지현 - 목적지 공지 추가
    public void createDestination() {
        //가이드인지 파악
        //if(dataManager.userType) {
            //공지 입력 다이얼로그
        Intent i = new Intent(this, DestinationActivity.class);
        startActivity(i);


        //}
        //else{ //작성할수 없다 공지 띄우고 끝
           // NoticeDialog notice2 = new NoticeDialog(MainActivity.this, 0);
            //notice2.show(); //다이얼로그 호출
       // }
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
                marker.showInfoWindow(); /// 2016-05-27
                mMapInfoWindowAdapter.getInfoContents(marker);
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
            case R.id.button_create_notice:
                dialog_notice();
                break;
            case R.id.button_create_destination_notice:
                createDestination();
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

        markingDestination();
    }
    public void initDestination() {
        getDestination();
    }
    public void getDestination() {
        dataManager.command(Protocol.URL_GET_DESTINATION_DATA, dataManager.groupData.getGroup_name(), "", "", "", "", "");
    }

    //2016-05-17 Destination Marking
    public void markingDestination() {
        //1. dataManager에서 Destination 객체 업데이트
        DestinationData destinationData = dataManager.destinationData;
        if( destinationData != null) {
            Double latitude = Double.parseDouble(destinationData.getLatitude());
            Double longitude = Double.parseDouble(destinationData.getLongitude());
            //dataManager.destinationData.updateData();
            destinationMarker = new MarkerOptions();
            destinationMarker.position(new LatLng(latitude, longitude)); //위도*경도
            destinationMarker.title("목적지"); //제목 미리보기
            destinationMarker.snippet("목적지");
            destinationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_d));
            mMap.addMarker(destinationMarker);
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

        //2.서버로 모든 데이터 업데이트 요청
        //2.1 group내 user데이터
        dataManager.command(Protocol.URL_GET_MY_GROUP_USERS_DATA, dataManager.userData.getUser_phone(), dataManager.userData.getUser_name(), dataManager.userData.getLatitude(), dataManager.userData.getLongitude(), dataManager.userData.getGroup_name());
        //2.2 group내 notice 데이터
        //dataManager.connectURL2(Protocol.URL_GET_ALL_NOTICE_DATA, "", "", "", dataManager.groupData.getGroup_name());
        //2.3 group내의 destination 데이터
        dataManager.command(Protocol.URL_GET_DESTINATION_DATA, dataManager.groupData.getGroup_name(), "", "", "", "", "");

        //데이터가 변했을때 호출
        userAdapter.notifyDataSetChanged();

        /**공지추가 **//////////////////////////
        //noticeAdapter.notifyDataSetChanged();
    }
    public void notifyDataChanged() {
        userAdapter.notifyDataSetChanged();
        noticeAdapter.notifyDataSetChanged();
        //2016-05-17 목적지 추가
    }


    // custom infowindow Adapter
    public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        public final View mapContentsView;
        Marker selectedMarker;

        MapInfoWindowAdapter() {
            mapContentsView = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
            //custom infowindow 크기 지정 //레이아웃부분에서 적용이 안됨.(왜지)
           // mapContentsView.setMinimumHeight(300);
            mapContentsView.setMinimumWidth(100);
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

                ///**********2016-2-29 테스트중 (프로필 fragment 관련)
                LinearLayout layout_profile = (LinearLayout) findViewById(R.id.layout_map_profile);
                LinearLayout map_layout_profile = (LinearLayout) findViewById(R.id.maptab_user_info);
                TextView name = (TextView) map_layout_profile.findViewById(R.id.text_infowindow_name);
                TextView phone = (TextView) map_layout_profile.findViewById(R.id.text_infowindow_phone);
                Button button_ar = (Button) map_layout_profile.findViewById(R.id.button_infowindow_ar);
                Button button_call = (Button) map_layout_profile.findViewById(R.id.button_infowindow_call);
                //*************

                final UserData focusUserData = mMarkersHashMap.get(marker);
                name.setText(focusUserData.getUser_name());
                phone.setText(focusUserData.getUser_phone());

                button_ar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //AR로 연결 - activity
                        //2016-05-09
                        LinearLayout map_layout_profile = (LinearLayout) findViewById(R.id.maptab_user_info);
                        String user_phone_to_track = ((TextView) map_layout_profile.findViewById(R.id.text_infowindow_phone)).getText().toString();
                        String user_name_to_track = ((TextView) map_layout_profile.findViewById(R.id.text_infowindow_name)).getText().toString();

                        //2016-05-15서지현
                        SharedPreferencesManager.savePreferences(getApplicationContext(), "group_name", dataManager.groupData.getGroup_name());
                        SharedPreferencesManager.savePreferences(getApplicationContext(), "user_phone_to_track", user_phone_to_track);
                        // ****끝
                        Toast.makeText(
                                getApplicationContext(),
                                user_name_to_track + "님의 위치 추적(AR)",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), MixView.class);
                        startActivity(intent);
                        //finish(); 2016-05-24 서지현 삭제
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

            //bottom sheet open 2016-05-24 서지현
            BottomSheetBehavior behavior = BottomSheetBehavior.from(findViewById(R.id.layout_map_profile));
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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


        // new - 네비게이션 드로어
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
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
