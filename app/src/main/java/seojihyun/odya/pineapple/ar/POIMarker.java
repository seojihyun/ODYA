package seojihyun.odya.pineapple.ar;

import android.location.Location;

/**
 * Created by SEOJIHYUN on 2016-04-04.
 */
// POI(Point of Interest)마커 클래스. 마커로부터 확장
public class POIMarker extends Marker {

    public static final int MAX_OBJECTS=30;	// 최대 객체 수

    // 생성자. 타이틀과 위도, 경도, 고도, 그리고 URL과 데이터 소스를 인자로 받는다
    public POIMarker(String title, String user_phone,double latitude, double longitude,
                     double altitude, String URL, DataSource.DATASOURCE datasource) {
        super(title, user_phone, latitude, longitude, altitude, URL, datasource);
        // TODO Auto-generated constructor stub
    }

    // 마커 위치 갱신
    @Override
    public void update(Location curGPSFix) {
        super.update(curGPSFix);
    }

    // 최대 객체 수 리턴
    @Override
    public int getMaxObjects() {
        return MAX_OBJECTS;
    }

}

