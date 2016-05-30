package seojihyun.odya.pineapple.ar.data;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import seojihyun.odya.pineapple.R;

// 데이터 소스를 실질적으로 다루는 클래스
public class DataSource {

    // 데이터 소스와 데이터 포맷은 비슷해 보이지만 전혀 다르다.
    // 데이터 소스는 데이터가 어디서 왔는지, 데이터 포맷은 어떤 형식으로 포맷되었는지를 가르킨다.
    // 이에 대한 이해는 똑같은 데이터 포맷으로 여러가지의 데이터 소스를 실험하는데에 필수적이다


    // 데이터 소스와 데이터 포맷의 열거형 변수
    public enum DATASOURCE { WIKIPEDIA, BUZZ, TWITTER, OSM, OWNURL, PINEAPPLE};
    public enum DATAFORMAT { WIKIPEDIA, BUZZ, TWITTER, OSM, MIXARE, PINEAPPLE};

    /** 기본 URL */
    // 파인애플
    private static final String PINEAPPLE_BASE_URL ="http://211.58.69.16:8080/pineapple/ar_track_test.php"; //2016-05-30
    // private static final String PINEAPPLE_BASE_URL ="http://211.58.69.16:8080/pineapple/artest.php";
    // 위키피디아
    //private static final String WIKI_BASE_URL = "http://ws.geonames.org/findNearbyWikipediaJSON";
    private static final String WIKI_BASE_URL = "http://211.58.69.16:8080/pineapple/ar_track_test.php";
    //private static final String WIKI_BASE_URL =	"file:///sdcard/wiki.json";

    // 트위터
    private static final String TWITTER_BASE_URL = "http://search.twitter.com/search.json";

    // 구글 버즈
    private static final String BUZZ_BASE_URL = "https://www.googleapis.com/buzz/v1/activities/search?alt=json&max-results=20";

    // OSM(OpenStreetMap)
    // OpenStreetMap API는 http://wiki.openstreetmap.org/wiki/Xapi 를 참고
    // 예를 들어, 철도만 사용할 경우:
    //private static final String OSM_BASE_URL =	"http://www.informationfreeway.org/api/0.6/node[railway=station]";
    //private static final String OSM_BASE_URL =	"http://xapi.openstreetmap.org/api/0.6/node[railway=station]";
    private static final String OSM_BASE_URL =		"http://osmxapi.hypercube.telascience.org/api/0.6/node[railway=station]";
    // 모든 객체들은 이름이 명명되어 있다
    //String OSM_URL = "http://xapi.openstreetmap.org/api/0.6/node[name=*]";

    // 주의할것! 방대한 양의 데이터(MB단위 이상)을 산출할 때에는, 작은 반경이나 특정한 쿼리만을 사용해야한다
    /** URL 부분 끝 */


    // 아이콘들. 트위터와 버즈
    public static Bitmap twitterIcon;
    public static Bitmap buzzIcon;

    // 기본 생성자
    public DataSource() {

    }

    // 리소스로부터 각 아이콘 생성
    public static void createIcons(Resources res) {
        twitterIcon= BitmapFactory.decodeResource(res, R.drawable.twitter);
        buzzIcon=BitmapFactory.decodeResource(res, R.drawable.buzz);
    }

    // 아이콘 비트맵의 게터
    public static Bitmap getBitmap(DATASOURCE ds) {
        Bitmap bitmap=null;
        switch (ds) {
            case TWITTER: bitmap=twitterIcon; break;
            case BUZZ: bitmap=buzzIcon; break;
        }
        return bitmap;
    }

    // 데이터 소스로부터 데이터 포맷을 추출
    public static DATAFORMAT dataFormatFromDataSource(DATASOURCE ds) {
        DATAFORMAT ret;
        // 소스 형식에 따라 포맷을 할당한다
        switch (ds) {
            case PINEAPPLE: ret=DATAFORMAT.PINEAPPLE; break;
            case WIKIPEDIA: ret=DATAFORMAT.WIKIPEDIA; break;
            case BUZZ: ret=DATAFORMAT.BUZZ; break;
            case TWITTER: ret=DATAFORMAT.TWITTER; break;
            case OSM: ret=DATAFORMAT.OSM; break;
            case OWNURL: ret=DATAFORMAT.MIXARE; break;
            default: ret=DATAFORMAT.MIXARE; break;
        }
        return ret;	// 포맷 리턴
    }

    // 각 정보들로 완성된 URL 리퀘스트를 생성 2016-05-15  파라미터 추가 : group_name, user_phone_to_track
    public static String createRequestURL(DATASOURCE source, double lat, double lon, double alt, float radius, String locale, String group_name, String user_phone_to_track, String track_type) {
        String ret="";	// 결과 스트링

        // 소스에 따라 주소 할당. 우선 상수로 설정된 값들을 할당한다
        switch(source) {

            case PINEAPPLE:
                ret = PINEAPPLE_BASE_URL;
                break;

            case WIKIPEDIA:
                ret = WIKI_BASE_URL;
                break;

            case BUZZ:
                ret = BUZZ_BASE_URL;
                break;

            case TWITTER:
                ret = TWITTER_BASE_URL;
                break;

            case OSM:
                ret = OSM_BASE_URL;
                break;


        }

        // 파일로부터 읽는 것이 아니라면
        if (!ret.startsWith("file://")) {

            // 각 소스에 따른 URL 리퀘스트를 완성한다
            switch(source) {
                // 파인애플
                case PINEAPPLE:
                    ret+=
                            "?lat=" + lat +
                                    "&lng=" + lon +
                                    "&radius=5" +
                                    "&maxRows=50" +
                                    "&lang=" + locale +
                                    "&username=seojihyunn"+
                                    "&track_type=" + track_type + //2016-05-29 서지현 추가
                                    "&user_phone_to_track=" + user_phone_to_track +
                                    "&group_name=" + group_name ;
                    break;
                // 위키피디아
                case WIKIPEDIA:
                    ret+=
                            "?lat=" + lat +
                                    "&lng=" + lon +
                                    "&radius=5" +
                                    "&maxRows=50" +
                                    "&lang=" + locale +
                                    "&username=seojihyunn";
                    break;
            }

        }

        return ret;
    }

    // 각 소스에 따른 색을 리턴
    public static int getColor(DATASOURCE datasource) {
        int ret;
        switch(datasource) {
            case BUZZ:		ret = Color.rgb(4, 228, 20); break;
            case TWITTER:	ret = Color.rgb(50, 204, 255); break;
            case OSM:		ret = Color.rgb(255, 168, 0); break;
            case WIKIPEDIA:	ret = Color.RED; break;
            case PINEAPPLE: ret = Color.YELLOW; break;
            default:		ret = Color.WHITE; break;
        }
        return ret;
    }

}
