package seojihyun.odya.pineapple.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import seojihyun.odya.pineapple.R;

/**
 * Created by SEOJIHYUN on 2016-02-21.
 */
public class CreateGroupDialog extends Dialog {

    private TextView mTitleView;
    private EditText groupName;
    private EditText password;
    private Button mLeftButton;
    private Button mRightButton;
    private String mTitle;
    private String mContent;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_create_group);

        mTitleView = (TextView) findViewById(R.id.title_create_group);
        groupName = (EditText) findViewById(R.id.edit_create_group_name);
        password = (EditText) findViewById(R.id.edit_create_password);
        mLeftButton = (Button) findViewById(R.id.button_create_group_right);
        mRightButton = (Button) findViewById(R.id.button_create_group_left);

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
        //mContentView.setText(mContent);

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CreateGroupDialog(Context context, String title,
                             View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mLeftClickListener = singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public CreateGroupDialog(Context context, String title, View.OnClickListener leftListener,
                             View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    //입력한 비밀번호값 activity로 가져가기
    public String getPwd() {
        String pwd = password.getText().toString();
        return pwd;
    }
    public String getGroupName() {
        String name = groupName.getText().toString();
        return name;
    }
}