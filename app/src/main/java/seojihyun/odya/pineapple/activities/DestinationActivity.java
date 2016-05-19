package seojihyun.odya.pineapple.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import seojihyun.odya.pineapple.R;
import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.protocol.Protocol;

public class DestinationActivity extends AppCompatActivity {

    DataManager dataManager;

    private TabHost tabHost;
    TimePicker timePicker;
    String timeSet;
    private GoogleMap map;
    Marker currentMarker; // 현재 클릭한 목적지
    double latitude, longitude;
    String content;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        dataManager = (DataManager)getApplicationContext();


        /* 0. 목적지 제출 버튼 리스너 부착*/
        Button submitButton = (Button) findViewById(R.id.submit_destination);
        if (submitButton != null) {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitDestination();
                }
            });
        }
        /* 1. Tab 초기화 */
        initTabHost();
        /* 2. Map Tab 초기화 */
        initMap();
        /* 3. TimePicker 관련 변수 초기화 */
        initTimePicker();
        /* 4. Content 관련 초기화 */
        initContent();

    }
    /* 1. Tab 초기화 */
    public void initTabHost() {

        tabHost = (TabHost) findViewById(R.id.tabHost2);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("tag1");
        spec.setContent(R.id.d_tab1);
        spec.setIndicator("1.Destination"); //탭버튼에 들어갈 이름
        tabHost.addTab(spec); //탭호스트에 탭 추가

        spec = tabHost.newTabSpec("tag2");
        spec.setContent(R.id.d_tab2);
        spec.setIndicator("2.Time");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("tag3");
        spec.setContent(R.id.d_tab3);
        spec.setIndicator("3.Content");
        tabHost.addTab(spec);

        // tab1부분이 초기화면으로 설정
        tabHost.setCurrentTab(0);
    }
    /* 2. Map Tab 초기화 */
    public void initMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.destination_map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setUpMap() {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 1. map 초기화
                map.clear();
                // 2. 클릭한 위도, 경도 얻기
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                // 3. 마커로 표시
                MarkerOptions optFirst = new MarkerOptions();
                optFirst.position(latLng); //위도*경도
                optFirst.title("목적지"); //제목 미리보기
                optFirst.snippet("목적지");
                currentMarker = map.addMarker(optFirst);
            }
        });
        // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void initTimePicker() {
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timeSet = String.format("%d시 %d분", timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            }
        });
    }

    public void initContent() {
         editText = (EditText) findViewById(R.id.destination_content);
    }

    public void nextStep(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button2:
                tabHost.setCurrentTab(1);
                break;
            case R.id.button3:
                tabHost.setCurrentTab(2);
                break;
        }
    }

    public void submitDestination() {
        content = editText.getText().toString();
        timeSet = String.format("%d시 %d분", timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        if( currentMarker!=null && content!=null && timeSet!=null) {
            // 서버로 제출 & 푸시 알림 설계
            String lat = Double.toString(latitude);
            String lon = Double.toString(longitude);
            /*
            *  서버로 Create Destination
            * */
            dataManager.command(Protocol.URL_CREATE_DESTINATION, dataManager.groupData.getGroup_name(), lat, lon, "address", content, timeSet);
            Toast.makeText(this, "시간 : " + timeSet + "\n위도 경도 : " + latitude+longitude + "\n공지 내용 : " + content, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else {
            Toast.makeText(this, "모든 항목을 작성해주세요", Toast.LENGTH_SHORT).show();
        }
    }

}
