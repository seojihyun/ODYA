package seojihyun.odya.pineapple.ar.view;

import seojihyun.odya.pineapple.ar.render.Matrix;
import seojihyun.odya.pineapple.ar.render.MixVector;

/**
 * Created by SEOJIHYUN on 2016-04-04.
 */
// 현재의 상태에 관한 클래스
public class MixState {

    // 각 상태에 대한 상수값 설정
    public static int NOT_STARTED = 0;
    public static int PROCESSING = 1;
    public static int READY = 2;
    public static int DONE = 3;

    int nextLStatus = MixState.NOT_STARTED;	// 다음 상태
    String downloadId;	// 다운로드할 ID

    private float curBearing;	// 현재의 방위각
    private float curPitch;		// 현재의 장치각(?)

    private boolean detailsView;	// 디테일 뷰가 표시 중인지 여부

    // 이벤트 처리
    public boolean handleEvent(MixContext ctx, String onPress) {
        // 눌려진 스트링 값이 null 이 아니고, 웹페이지로 연결될 경우
        if (onPress != null && onPress.startsWith("webpage")) {
            try {
                // 내용을 파싱하고 디테일 뷰에 웹페이지를 띄운다
                String webpage = MixUtils.parseAction(onPress);
                this.detailsView = true;
                ctx.loadMixViewWebPage(webpage);
            } catch (Exception ex) {
            }
        }
        return true;
    }


    //서지현_test1 추가
    // 이벤트 처리
    public boolean handleEvent(MixContext ctx, String onPress, Marker marker) {
        try {
            // 내용을 파싱하고 디테일 뷰에 웹페이지를 띄운다
            //String webpage = MixUtils.parseAction(onPress);
            this.detailsView = true;
            ctx.loadMixViewWebPage(marker); //서지현_test1
        } catch (Exception ex) {}
        return true;
    }
    //*************서지현_test1 끝

    // 현재의 방위각을 리턴
    public float getCurBearing() {
        return curBearing;
    }

    // 현재의 장치각을 리턴
    public float getCurPitch() {
        return curPitch;
    }

    // 디테일 뷰의 표시 여부를 리턴
    public boolean isDetailsView() {
        return detailsView;
    }

    // 디테일 뷰의 표시 여부를 설정
    public void setDetailsView(boolean detailsView) {
        this.detailsView = detailsView;
    }

    // 장치각과 방위각을 계산
    public void calcPitchBearing(Matrix rotationM) {
        MixVector looking = new MixVector();
        rotationM.transpose();
        looking.set(1, 0, 0);
        looking.prod(rotationM);
        this.curBearing = (int) (MixUtils.getAngle(0, 0, looking.x, looking.z)  + 360 ) % 360 ;

        rotationM.transpose();
        looking.set(0, 1, 0);
        looking.prod(rotationM);
        this.curPitch = -MixUtils.getAngle(0, 0, looking.y, looking.z);
    }
}

