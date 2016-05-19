package seojihyun.odya.pineapple.protocol;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import seojihyun.odya.pineapple.activities.MainActivity;

/**
 * Created by SEOJIHYUN on 2016-05-05.
 */
public class GpsInfo extends Service implements LocationListener {

    private final MainActivity mainActivity;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lon; // 경도
    double alt; // 고도  *2016-03-05 테스트

    // GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_UPDATES = 10;

    // GPS 정보 업데이트 시간 10초
    private static final long MIN_TIME_UPDATES = 1000;

    protected LocationManager locationManager;


    public GpsInfo(Context context) {
        this.mainActivity = (MainActivity)context;
        getLocation();

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mainActivity
                    .getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.isGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_UPDATES,
                            MIN_DISTANCE_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            alt = location.getAltitude();
                        }

                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager
                                .requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_UPDATES,
                                        MIN_DISTANCE_UPDATES,
                                        this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                alt = location.getAltitude();
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * GPS 종료 2016-03-05 테스트 완료 (그룹을 나간 경우의 스레드 충돌을 방지!)
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GpsInfo.this);
        }
    }

    /**
     * 위도값
     * */
    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * 경도값
     * */
    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    /*
     * 고도값
     */
    public double getAltitude() {
        if (location != null) {
            alt = location.getLongitude();
        }
        return alt;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                mainActivity);

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog
                .setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mainActivity.startActivity(intent);
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

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //*********2016-03-05 테스트중
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        Toast.makeText(mainActivity, "onLocationChanged 호출 (10초 주기)" + alt, Toast.LENGTH_SHORT).show();
        mainActivity.setMyLocation(location.getLatitude(), location.getLongitude());

        //2016-03-28 테스트중_서지현
        //데이터가 변했을때 호출
        //mainActivity.updateAllData();

        //그룹을 나간 경우 gps 사용 x
        if(mainActivity.isDestroyed()) {
            this.stopUsingGPS();
        }
    }

    public void onStatusChanged(String provider, int status,
                                Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}
