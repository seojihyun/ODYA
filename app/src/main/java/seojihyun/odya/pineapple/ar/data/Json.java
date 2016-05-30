package seojihyun.odya.pineapple.ar.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import seojihyun.odya.pineapple.ar.view.Marker;
import seojihyun.odya.pineapple.ar.view.MixView;
import seojihyun.odya.pineapple.ar.view.POIMarker;

// JSON 파일을 다루는 클래스
public class Json extends DataHandler {

    public static final int MAX_JSON_OBJECTS=1000;	// JSON 객체의 최대 수

    // 각종 데이터를 로드
    public List<Marker> load(JSONObject root, DataSource.DATAFORMAT dataformat) {
        // 데이터를 읽는데 사용할 JSON 객체와 데이터행렬, 마커들
        JSONObject jo = null;
        JSONArray dataArray = null;
        List<Marker> markers=new ArrayList<Marker>();

        try {
            // 각 스키마에 따른 데이터를 읽어온다
            // 트위터
            if(root.has("results"))
                dataArray = root.getJSONArray("results");
                // 위키피디아 & 파인애플
            else if (root.has("geonames"))
                dataArray = root.getJSONArray("geonames");
                // 구글 버즈
            else if (root.has("data") && root.getJSONObject("data").has("items"))
                dataArray = root.getJSONObject("data").getJSONArray("items");

            // 데이터행렬에 데이터들이 있다면
            if (dataArray != null) {
                // 일단 로그 생성. 데이터 포맷을 기록한다
                Log.i(MixView.TAG, "processing "+dataformat+" JSON Data Array");
                // 최대 객체 수와 실제 데이터 길이를 비교해 최소치를 탑으로 지정
                int top = Math.min(MAX_JSON_OBJECTS, dataArray.length());

                // 각 데이터들에 대한 처리
                for (int i = 0; i < top; i++) {
                    // 처리할 JSON 객체를 할당
                    jo = dataArray.getJSONObject(i);
                    Marker ma = null;

                    // 데이터 포맷에 따른 처리
                    switch(dataformat) {
                        //case MIXARE: ma = processMixareJSONObject(jo); break;
                        //case BUZZ: ma = processBuzzJSONObject(jo); break;
                        //case TWITTER: ma = processTwitterJSONObject(jo); break;
                        case WIKIPEDIA: ma = processWikipediaJSONObject(jo); break;
                        case PINEAPPLE: ma = processPineappleJSONObject(jo); break;
                        // default: ma = processMixareJSONObject(jo); break;
                    }
                    // 마커 추가
                    if(ma!=null)
                        markers.add(ma);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // 모든 마커가 추가된 리스트를 리턴
        return markers;
    }
    // 파인애플 데이터 처리
    public Marker processPineappleJSONObject(JSONObject jo) throws JSONException {

        Marker ma = null;	// 임시객체

        // 형식에 맞는지 검사. 타이틀과 위도, 경도, 고도 태그를 찾는다
        if (jo.has("title") && jo.has("lat") && jo.has("lng") && jo.has("elevation") ) {

            Log.v(MixView.TAG, "processing Pineapple JSON object");	// 로그 출력

            // 할당된 값들로 마커 생성
            ma = new POIMarker(
                    jo.getString("title"),
                    jo.getString("user_phone"),
                    jo.getDouble("lat"),
                    jo.getDouble("lng"),
                    jo.getDouble("elevation"),
                    "http://google.co.kr/",		// 위키의 url 을 입력
                    DataSource.DATASOURCE.PINEAPPLE);
        }
        return ma;	// 마커 리턴
    }

    // 위키피디아 데이터 처리
    public Marker processWikipediaJSONObject(JSONObject jo) throws JSONException {

        Marker ma = null;	// 임시객체

        // 형식에 맞는지 검사. 타이틀과 위도, 경도, 고도 태그를 찾는다
        if (jo.has("title") && jo.has("lat") && jo.has("lng") && jo.has("elevation") ) {

            Log.v(MixView.TAG, "processing Wikipedia JSON object");	// 로그 출력

            // 할당된 값들로 마커 생성
            ma = new POIMarker(
                    jo.getString("title"),
                    jo.getString("user_phone"),
                    jo.getDouble("lat"),
                    jo.getDouble("lng"),
                    jo.getDouble("elevation"),
                    "http://",		// 위키의 url 을 입력
                    DataSource.DATASOURCE.WIKIPEDIA);
        }
        return ma;	// 마커 리턴
    }

    // html 엔트리의 해쉬맵
    private static HashMap<String, String> htmlEntities;
    static {
        htmlEntities = new HashMap<String, String>();
        htmlEntities.put("&lt;", "<");
        htmlEntities.put("&gt;", ">");
        htmlEntities.put("&amp;", "&");
        htmlEntities.put("&quot;", "\"");
        htmlEntities.put("&agrave;", "à");
        htmlEntities.put("&Agrave;", "À");
        htmlEntities.put("&acirc;", "â");
        htmlEntities.put("&auml;", "ä");
        htmlEntities.put("&Auml;", "Ä");
        htmlEntities.put("&Acirc;", "Â");
        htmlEntities.put("&aring;", "å");
        htmlEntities.put("&Aring;", "Å");
        htmlEntities.put("&aelig;", "æ");
        htmlEntities.put("&AElig;", "Æ");
        htmlEntities.put("&ccedil;", "ç");
        htmlEntities.put("&Ccedil;", "Ç");
        htmlEntities.put("&eacute;", "é");
        htmlEntities.put("&Eacute;", "É");
        htmlEntities.put("&egrave;", "è");
        htmlEntities.put("&Egrave;", "È");
        htmlEntities.put("&ecirc;", "ê");
        htmlEntities.put("&Ecirc;", "Ê");
        htmlEntities.put("&euml;", "ë");
        htmlEntities.put("&Euml;", "Ë");
        htmlEntities.put("&iuml;", "ï");
        htmlEntities.put("&Iuml;", "Ï");
        htmlEntities.put("&ocirc;", "ô");
        htmlEntities.put("&Ocirc;", "Ô");
        htmlEntities.put("&ouml;", "ö");
        htmlEntities.put("&Ouml;", "Ö");
        htmlEntities.put("&oslash;", "ø");
        htmlEntities.put("&Oslash;", "Ø");
        htmlEntities.put("&szlig;", "ß");
        htmlEntities.put("&ugrave;", "ù");
        htmlEntities.put("&Ugrave;", "Ù");
        htmlEntities.put("&ucirc;", "û");
        htmlEntities.put("&Ucirc;", "Û");
        htmlEntities.put("&uuml;", "ü");
        htmlEntities.put("&Uuml;", "Ü");
        htmlEntities.put("&nbsp;", " ");
        htmlEntities.put("&copy;", "\u00a9");
        htmlEntities.put("&reg;", "\u00ae");
        htmlEntities.put("&euro;", "\u20a0");
    }

    // HTML 아스키 값들을 다시 복원. 변환할 소스와 시작점을 인자로 받는다
    public String unescapeHTML(String source, int start) {
        int i, j;	// 임시 변수

        // &와 ;의 위치로 값들을 읽는다
        i = source.indexOf("&", start);
        if (i > -1) {
            j = source.indexOf(";", i);
            if (j > i) {
                // 검색된 위치에서 값을 읽어옴
                String entityToLookFor = source.substring(i, j + 1);
                String value = (String) htmlEntities.get(entityToLookFor);

                // 값이 있을 시 복원작업 시작. 재귀호출 이용
                if (value != null) {
                    source = new StringBuffer().append(source.substring(0, i))
                            .append(value).append(source.substring(j + 1))
                            .toString();
                    return unescapeHTML(source, i + 1); // recursive call
                }
            }
        }
        return source;	// 복원된 소스 리턴
    }
}