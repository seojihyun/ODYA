package seojihyun.odya.pineapple.protocol;

/**
 * Created by SEOJIHYUN on 2016-05-17.
 */
public class DestinationData {
    private String group_name;
    private String latitude;
    private String longitude;
    private String address;
    private String content;
    private String time_set;

    public DestinationData() {

    }
    public DestinationData(String group_name, String latitude, String longitude, String address, String content, String time_set) {
        this.group_name = group_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.content = content;
        this.time_set = time_set;
    }
    public void updateData(String group_name, String latitude, String longitude, String address, String content, String time_set) {
        this.group_name = group_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.content = content;
        this.time_set = time_set;
    }
    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime_set() {
        return time_set;
    }

    public void setTime_set(String time_set) {
        this.time_set = time_set;
    }
}
