package seojihyun.odya.pineapple.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.Vector;

import seojihyun.odya.pineapple.R;
import seojihyun.odya.pineapple.protocol.NoticeData;

/**
 * Created by user11 on 2016-03-19.
 */
public class NoticeAdapter extends BaseAdapter {
     /*
    @param context : 컨텍스트
    @param layoutId : 보여줄 레이아웃
    */

    private Context context;
    private int layoutId;
    private Vector<NoticeData> list;
    private LayoutInflater inflater; //레이아웃xml파일을 자바 객체로 변환하기 위한 객체

    public NoticeAdapter(Context context, int layoutId, Vector<NoticeData> list) {
        this.context = context;
        this.layoutId = layoutId;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) { //항목 뷰가 한번도 보여진적이 없는 경우
            //레이아웃(list_notice_item.xml)을 자바 객체로 변환하기
            convertView=inflater.inflate(layoutId, parent, false);
        }
        ///// convertView에 어떻게 보여질지 설정 /////
        TextView text_notice_title = (TextView)convertView.findViewById(R.id.text_notice_title);
        TextView text_notice_time = (TextView)convertView.findViewById(R.id.text_notice_time);
        final NoticeData item = list.get(position);
        text_notice_title.setText(item.getNotice_title());
        text_notice_time.setText(item.getNotice_time());


        return convertView;
    }
}
