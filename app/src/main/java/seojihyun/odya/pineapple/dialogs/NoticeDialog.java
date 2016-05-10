package seojihyun.odya.pineapple.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import seojihyun.odya.pineapple.R;

/**
 * Created by user11 on 2016-03-15.
 */
public class NoticeDialog extends Dialog{
    int yes; //guide 가 누른 경우 1
    Context context;

    //notice_x
    TextView text_warning;
    Button btnOk;

    //notice_o
    EditText editTitle, editContent;
    Button btnInput,btnCancel;
    View.OnClickListener noticeListener;

    public NoticeDialog(Context context, int yes) {
        super(context);
        this.context = context;
        this.yes = yes;
    }
    /** 추가 3/24**/
    public NoticeDialog(Context context, int yes, View.OnClickListener listener) {
        super(context);
        this.context = context;
        this.yes = yes;
        this.noticeListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        switch (yes) {//가이드인지 check
            case 1:
                alertNotice();        //가이드인 경우
                break;
            case 0:
                alertNoticeWarning(); //가이드가 아닌 경우
                break;
            default:
                break;
        }
    }

    //가이드가 아닌 경우
    private void alertNoticeWarning() {
        setContentView(R.layout.dialog_notice_x);

        text_warning = (TextView) findViewById(R.id.text_notice);
        btnOk = (Button) findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    //가이드인 경우
    private void alertNotice() {
        setContentView(R.layout.dialog_notice_o);

        editTitle = (EditText) findViewById(R.id.edit_title);
        editContent = (EditText) findViewById(R.id.edit_content);
        btnInput = (Button) findViewById(R.id.btn_input);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        //작성확인 버튼
        btnInput.setOnClickListener(noticeListener);
        //작성취소 버튼
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alertCheck(); 작성했던 내용 취소 됩니다.// -> 거기서 dismiss();
                dismiss();
            }
        });
    }

    //입력한 값 activity, 서버 로 가져가기
    public String getNoticeContent() {
        String content = editContent.getText().toString();
        return content;
    }
    public String getNoticeTitle() {
        String title = editTitle.getText().toString();
        return title;
    }
    public String getNoticeTime(){ //시간 구하기
        String format = new String("MM월 dd일 HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);
        String time = sdf.format(new Date());
        return time;
    }

}

