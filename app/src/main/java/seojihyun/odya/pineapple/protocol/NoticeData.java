package seojihyun.odya.pineapple.protocol;

import java.io.Serializable;

/**
 * Created by user11 on 2016-03-19.
 */
public class NoticeData implements Serializable {
    private String notice_title="";
    private String notice_content="";
    private String notice_time="";
    private String group_name="";
    public NoticeData() {}
    public NoticeData(String... params) {
        updateNotice(params);
    }
    public void updateNotice(String... params) {
            notice_title = params[0];
            notice_content = params[1];
            notice_time = params[2];
            group_name = params[3];
    }

    //tab list화면에 보여질것들 보내기 (제목,시간)
    public String getNotice_title() {return notice_title;}
    public String getNotice_time() {return notice_time;}
    public String getNotice_content() {return notice_content;}
    public String getNotice_groupN() {return group_name;}

    //set / NoticeDialog에서 받아와 //안쓸듯?
    public void setNotice_title(String notice_title){this.notice_title = notice_title;}
    public void setNotice_content(String notice_content){this.notice_content = notice_content;}
    public void setNotice_time(String notice_time){this.notice_time = notice_time;}
}
