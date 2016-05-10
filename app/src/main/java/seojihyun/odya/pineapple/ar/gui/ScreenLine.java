package seojihyun.odya.pineapple.ar.gui;

/**
 * Created by SEOJIHYUN on 2016-04-04.
 */
// 스크린 라인의 클래스
public class ScreenLine {
    public float x, y;	// x, y 위치값

    // 기본 생성자. x, y 값은 각각 0으로 세팅
    public ScreenLine() {
        set(0, 0);
    }

    // 생성자. 지정한 값으로 x, y 값 설정
    public ScreenLine(float x, float y) {
        set(x, y);
    }

    // 세터. 지정한 값으로 x, y 값 설정
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // 인자로 받은 각도로 회전시킨다
    public void rotate(double t) {
        // 삼각함수를 이용한 계산으로 정확한 값을 지정
        float xp = (float) Math.cos(t) * x - (float) Math.sin(t) * y;
        float yp = (float) Math.sin(t) * x + (float) Math.cos(t) * y;

        x = xp;
        y = yp;
    }

    // 각 x, y 값에 특정 수치를 더함
    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }
}
