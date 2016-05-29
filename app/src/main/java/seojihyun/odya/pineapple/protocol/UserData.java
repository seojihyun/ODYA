package seojihyun.odya.pineapple.protocol;

import java.io.Serializable;

/**
 * Created by SEOJIHYUN on 2016-02-20.
 */
public class UserData implements Serializable {
    private String user_phone="";
    private String user_name="";
    private String latitude="";
    private String longitude="";
    private String group_name="";

    private boolean partyType=false;

    UserData() {

    }
    UserData(String user_phone, String user_name, String latitude, String longitude, String group_name) {
        updateUserData(user_phone, user_name, latitude, longitude, group_name);
    }
    public String getUser_phone() { return user_phone;}
    public String getUser_name() { return user_name; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getGroup_name() { return group_name;}

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public void setLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    public void updateUserData(String... params) {
        user_phone = params[0];
        user_name = params[1];
        latitude = params[2];
        longitude = params[3];
        group_name = params[4];
    }

    // 일행 설정
    public void setParty() { partyType = true; }
    public void removeParty() { partyType = false; }
    public boolean isParty() { return partyType; }
}
