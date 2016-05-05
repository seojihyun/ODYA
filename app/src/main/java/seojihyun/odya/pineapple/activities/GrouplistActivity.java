package seojihyun.odya.pineapple.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import seojihyun.odya.pineapple.dialogs.CreateGroupDialog;
import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.dialogs.EnterGroupDialog;
import seojihyun.odya.pineapple.adapters.GroupAdapter;
import seojihyun.odya.pineapple.protocol.GroupData;
import seojihyun.odya.pineapple.protocol.Protocol;
import seojihyun.odya.pineapple.R;
import seojihyun.odya.pineapple.protocol.UserData;

public class GrouplistActivity extends AppCompatActivity {

    GroupAdapter groupAdapter;
    DataManager dataManager;
    UserData userData;
    ListView groupList;
    EnterGroupDialog enterGroupDialog;
    CreateGroupDialog createGroupDialog;
    private View.OnClickListener leftListener, rightListener, createGroupRightListener, createGroupLeftListener;
    GroupData selectedGroup; //입장하려는 그룹
    ImageView button_create_group, button_search_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouplist);

         /* 각 버튼 리스너 부착 */
        button_create_group = (ImageView) findViewById(R.id.grouplist_button_create_group);
        button_search_group = (ImageView) findViewById(R.id.grouplist_button_search_group);

        //dataManager 객체 얻기
        dataManager=(DataManager)getApplicationContext();
        dataManager.setActivity(this);
        //인텐트 값 전달 받기 - UserData
        Intent intent = getIntent();
        userData = (UserData) intent.getSerializableExtra("UserData");

        /* groupList  */
        groupList = (ListView)findViewById(R.id.list_group);

        groupAdapter = new GroupAdapter(this, R.layout.item_group, dataManager.groups);
        groupList.setAdapter(groupAdapter);

        //그룹입장클릭시 다이얼로그 리스너 부착
        setOnClickListenerForDialog();



    }

    //방 입장
    /*
    * 1. group adapter 에서 호출됨
    * 2. 비밀번호 입력 다이얼로그 생성
    * 3. 비밀번호 일치 여부 확인
    * 4. 입장 가능
    * */
    public void makeEnterGroupDialog(GroupData selectedGroup) {
        this.selectedGroup = selectedGroup;
        enterGroupDialog = new EnterGroupDialog(this,
                "Group 입장", // 제목
                "Password를 입력해주세요", // 내용
                leftListener, // 왼쪽 버튼 이벤트
                rightListener); // 오른쪽 버튼 이벤트
        enterGroupDialog.show();
    }
    /*
    * 1. 방 생성 다이얼로그
    * 2. 방이름, 비밀번호 입력
    * 3. 방이름 존재 여부 확인
    * 4. 그룹 생성
    * 5  서버로 insert 방이름, 비밀번호 , 가이드전화번호, 가이드이름 생성
    * 6. 방 입장*/
    public void makeCreateGroupDialog(View v) { //group_list.xml에 button_create_group 의 onClick속성
        createGroupDialog = new CreateGroupDialog(this,
                "Group 생성", // 제목
                createGroupLeftListener, // 왼쪽 버튼 이벤트
                createGroupRightListener); // 오른쪽 버튼 이벤트
        createGroupDialog.show();
    }

    public void setOnClickListenerForDialog() {
        /* 그룹 입장 관련 다이얼로그 버튼리스너 */
        leftListener = new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "왼쪽버튼 클릭", Toast.LENGTH_SHORT).show();

                //1. 비밀번호 check
                //2. userData와 groupData update
                //3. 서버로 user테이블의 group_name 정보 변경 요청
                if (selectedGroup.checkGroup_pwd(enterGroupDialog.getPwd())) {
                    //2
                    dataManager.userData.setGroup_name(selectedGroup.getGroup_name());
                    //userData.setGroup_name(selectedGroup.getGroup_name()); 2016-05-05
                    //3 ***
                    dataManager.connectURL(Protocol.URL_ENTER_Group, dataManager.userData.getUser_phone(),
                            dataManager.userData.getUser_name(), dataManager.userData.getLatitude(), dataManager.userData.getLongitude(), selectedGroup.getGroup_name());

                } else { //로그인 실패
                    Toast.makeText(getApplicationContext(), "입장 실패", Toast.LENGTH_SHORT).show();
                }
                enterGroupDialog.dismiss();
            }
        };
        rightListener = new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "오른쪽버튼 클릭", Toast.LENGTH_SHORT).show();
                enterGroupDialog.dismiss();
            }
        };
        /* 그룹 생성 관련 다이얼로그 버튼 리스너 */
        // 그룹 생성 기능
    /*
    * 1. 그룹이름 존재 여부 확인
    * 2. userData와 groupData와 groups 정보 update
    * 3. 서버로 group테이블 insert 와 user테이블의 group_name 정보 변경 요청
    * 4. changeActivity(MainActivity)로 이동
    * */
        createGroupLeftListener = new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "생성 버튼 클릭", Toast.LENGTH_SHORT).show();

                // 1 그룹 이름 존재 여부 확인
                for(int i=0; i<dataManager.groups.size(); i++) {
                    if(dataManager.groups.get(i).getGroup_name().equals(createGroupDialog.getGroupName())) {
                        Toast.makeText(getApplicationContext(), "그룹 이름 이미 존재", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 2 userData와 groupData update ********수정 필요함
                // dataManager측 데이터 수정
                dataManager.userData.setGroup_name(createGroupDialog.getGroupName());
                dataManager.groupData.updateGroup(createGroupDialog.getGroupName(), createGroupDialog.getPwd(), "1", userData.getUser_phone(), userData.getUser_name());
                dataManager.groups.add(new GroupData(createGroupDialog.getGroupName(), createGroupDialog.getPwd(), "1", userData.getUser_phone(), userData.getUser_name()));

                // GroupListActivitiy측 데이터 수정
                userData.setGroup_name(createGroupDialog.getGroupName());

                // 3, 4 서버로 group테이블 insert 와 user테이블의 group_name 정보 변경 요청 & changeActivity
////////////////////////////////////////////////////
                String groupN = createGroupDialog.getGroupName();
                String pwd = createGroupDialog.getPwd();



                dataManager.connectURL(Protocol.URL_CREATE_GROUP, userData.getUser_phone(), userData.getUser_name(), userData.getLatitude(), userData.getLongitude(), groupN, pwd, "1");

                createGroupDialog.dismiss();
            }
        };
        createGroupRightListener = new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "취소 버튼 클릭", Toast.LENGTH_SHORT).show();
                createGroupDialog.dismiss();
            }
        };
        /* 그룹 찾기 관련 다이얼로그 버튼 리스너*/
    }
}
