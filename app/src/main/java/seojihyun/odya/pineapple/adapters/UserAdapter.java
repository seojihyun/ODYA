package seojihyun.odya.pineapple.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.Vector;

import seojihyun.odya.pineapple.R;
import seojihyun.odya.pineapple.activities.MainActivity;
import seojihyun.odya.pineapple.protocol.DataManager;
import seojihyun.odya.pineapple.protocol.UserData;

/**
 * Created by SEOJIHYUN on 2016-02-20.
 */
public class UserAdapter extends BaseSwipeAdapter {
    /*
    @param context : 컨텍스트
    @param layoutId : 보여줄 레이아웃
    @param list : 보여줄 데이터를 갖고있는 배열
    */

    private Context context;
    private int layoutId;
    private Vector<UserData> list;
    private LayoutInflater inflater; //레이아웃xml파일을 자바 객체로 변환하기 위한 객체
    private DataManager dataManager;

    public UserAdapter(Context context, int layoutId, Vector<UserData> list) {
        this.context = context;
        this.layoutId = layoutId;
        this.list = list;
        //LayoutInflater 객체 얻어오기(레이아웃 xml파일을 자바객체로 변환하기 위해서)
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public UserAdapter(Context context, int layoutId, Vector<UserData> list, DataManager dataManager) {
        this.context = context;
        this.layoutId = layoutId;
        this.list = list;
        this.dataManager = dataManager;
        //LayoutInflater 객체 얻어오기(레이아웃 xml파일을 자바객체로 변환하기 위해서)
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //항목의 갯수 반환
    @Override
    public int getCount() {
        return list.size();
    }

    //position에 해당하는 항목 반환환
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    //해당항목 id반환
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup viewGroup) {
        final View convertView = LayoutInflater.from(context).inflate(R.layout.item_user, null);
        final SwipeLayout swipeLayout = (SwipeLayout)convertView.findViewById(getSwipeLayoutResourceId(position));

        TextView text_user_phone = (TextView)swipeLayout.findViewById(R.id.text_user_phone);
        TextView text_user_name = (TextView)swipeLayout.findViewById(R.id.text_user_name);
        final ImageView button = (ImageView) swipeLayout.findViewById(R.id.button_user_profile);
        //텍스트뷰에 group_name 넣기
        final UserData item = list.get(position);
        text_user_phone.setText(item.getUser_phone());
        text_user_name.setText(item.getUser_name());


        //서지현 추가 - 가이드와 일반 사용자간의 아이콘 차이
        if(dataManager != null) {
            //서지현 가이드와 일반 사용자의 구분을 위해!
            if (item.getUser_phone().equals(dataManager.groupData.getUser_phone())) {
                //가이드인 경우
                ImageView img = (ImageView) convertView.findViewById(R.id.list_user_icon);
                img.setImageResource(R.drawable.pin1);
            } else {
                //일반 사용자인 경우
                ImageView img = (ImageView) convertView.findViewById(R.id.list_user_icon);
                img.setImageResource(R.drawable.pin2);
            }
        }


        // 클릭 리스너 부착
        // 1. 일행 추가
        // 2. 일행 삭제
        // 3. 위치 추적
        convertView.findViewById(R.id.addParty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click addParty", Toast.LENGTH_SHORT).show();
                // 프로필에 별 추가
                button.setVisibility(View.VISIBLE);
                //  액션 추가 필요
                // 1. UserData 의 partyType 설정 = true
                item.setParty();

                // 2. 네비게이션 드로어 헤더안에 리스트 추가
                ((MainActivity) context).insertPartyMember(item);

                // swipelayout 닫기
                swipeLayout.close();
            }
        });

        convertView.findViewById(R.id.removeParty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click removeParty", Toast.LENGTH_SHORT).show();
                // 프로필에 별 삭제
                button.setVisibility(View.INVISIBLE);
                //  액션 추가 필요
                // 1. UserData 의 partyType 설정 = false
                item.removeParty();

                // 2. 네비게이션 드로어 헤더안에 리스트 삭제
                ((MainActivity) context).removePartyMember(item);

                //swipelayout 닫기
                swipeLayout.close();
            }
        });

        convertView.findViewById(R.id.focusMarker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click focusMarker", Toast.LENGTH_SHORT).show();
                String txt = "이름 : " + item.getUser_name() + "\n전화번호 : " + item.getUser_phone();
                //토스트로 출력하기
                Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
                // 방 입장 메소드 호출 ******테스트중
                //((MainActivity)context).makeEnterGroupDialog(item);

                //2016-03-31 테스트중 서지현
                ((MainActivity)context).focusMarker(item);
            }
        });



     ///이미지뷰에 그룹 이미지 넣기
        //* ImageView img=(ImageView) convertView.findViewById(...)
       // * img.setImageResource(item.getIcon());


        // 버튼 눌렀을때 선택된 제품명 출력하기
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = "이름 : " + item.getUser_name() + "\n전화번호 : " + item.getUser_phone();
                //토스트로 출력하기
                Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
                // 방 입장 메소드 호출 ******테스트중
                //((MainActivity)context).makeEnterGroupDialog(item);

                //2016-03-31 테스트중 서지현
                ((MainActivity)context).focusMarker(item);
            }
        });

        return convertView;
    }

    @Override
    public void fillValues(int i, View view) {

    }

}
