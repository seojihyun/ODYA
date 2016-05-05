package seojihyun.odya.pineapple.protocol;

import java.io.Serializable;

/**
 * Created by SEOJIHYUN on 2016-02-20.
 */
public class GroupData implements Serializable {
    private String group_name="";//그룹 이름
    private String group_pwd=""; //그룹 비밀번호
    private String number=""; //인원수
    private String user_phone=""; //가이드 전화번호
    private String user_name=""; //가이드 이름
    public GroupData() {}
    public GroupData(String... params) {
        updateGroup(params);
    }
    public void updateGroup(String... params) {
        group_name = params[0];
        group_pwd = params[1];
        number = params[2];
        user_phone = params[3];
        user_name = params[4];
    }
    public String getGroup_name() {return group_name;}
    public boolean checkGroup_pwd(String group_pwd) {
        return (this.group_pwd.equals(group_pwd))? true: false;
    }
    public String getNumber() {return number;}
    public String getUser_phone() {return user_phone;}
    public String getUser_name() { return user_name;}
}

