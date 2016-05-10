package seojihyun.odya.pineapple.protocol;

/**
 * Created by SEOJIHYUN on 2016-02-20.
 */
public class Protocol {
    public static final String URL = "http://211.58.69.16:8080/pineapple/";

    /* GCM 관련 */

    /*     Server -> Client   */

    //php로부터 전달받는 key값 관련 프로토콜
    public static final String KEY_MESSAGE = "message"; // (1) 쿼리진행 종류
    public static final String KEY_RESULT = "result"; //(2) 쿼리 성공여부
    public static final String KEY_ARRAY="array"; //(3-1) 쿼리 성공시 전달받는 User들의 정보 - 배열
    //static final String KEY_USER_DATA = "user_data" //(3-2) 쿼리성공시 전달받는 user하나의 정보
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_PHONE = "user_phone";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_GROUP_NAME = "group_name";
    public static final String KEY_GROUP_PWD = "group_pwd";
    public static final String KEY_NUMBER = "number";


    // KEY_MESSAGE 의 value 관련 프로토콜
    public static final String MESSAGE_ERROR = "0";
    public static final String MESSAGE_CHECK_USER = "1";
    public static final String MESSAGE_SIGN_UP = "2";
    public static final String MESSAGE_LOGIN = "3";
    public static final String MESSAGE_GROUP_ROOM_LIST = "5"; //현재 존재하는 그룹리스트 출력
    public static final String MESSAGE_CREATE_GROUP = "8";
    public static final String MESSAGE_ENTER_GROUP="9";//비번 입력 성공후 그룹입장(group테이블의 Group_num과 user테이블의 group_name수정, UserData와 groupData객체 수정)
    public static final String MESSAGE_MAP_SETLOCATION = "6";
    public static final String MESSAGE_MAP_GETALLUSERLOCATION = "7";
    public static final String MESSAGE_GET_ALL_GROUP_DATA="10";
    public static final String MESSAGE_CHECK_GROUP_PWD="11"; //*********테스트중
    public static final String MESSAGE_GET_MY_GROUP_USERS_DATA="12";
    public static final String MESSAGE_EXIT_GROUP="13";


    // KEY_RESULT 의 value 관련 프로토콜
    public static final String RESULT_SUCCESS="success";
    public static final String RESULT_FAILURE="failure";
    //else 면 ARRAY 값으로 리턴할것임



    /*     Client -> Server   */

    // 각 액티비티에서 dataManager로 command 처리 관련 프로토콜 (서버와의 연결전 작업 진행)**** 구상중

    // URL connect 와 관련된 프로토콜
    public static final String URL_CHECK_USER = Protocol.URL + "checkuser.php";
    public static final String URL_SIGN_UP = Protocol.URL + "signup.php";
    public static final String URL_LOGIN = Protocol.URL + "login.php";
    public static final String URL_SET_LOCATION = Protocol.URL + "setlocation.php";
    public static final String URL_GET_ALL_USER_LOCATION = Protocol.URL + "getalluserlocation.php"; //**테스트용
    public static final String URL_ENTER_Group = Protocol.URL + "entergroup.php"; //비밀번호 확인 & user테이블 group테이블 업데이트
    public static final String URL_GET_ALL_GROUP_DATA = Protocol.URL + "getallgroupdata.php";
    public static final String URL_CHECK_GROUP_PWD = Protocol.URL + "checkgrouppwd.php";
    public static final String URL_CREATE_GROUP = Protocol.URL + "creategroup.php";
    public static final String URL_GET_MY_GROUP_USERS_DATA = Protocol.URL + "getmygroupusersdata.php";
    public static final String URL_EXIT_GROUP=Protocol.URL + "exitgroup.php";

    //2016-05-09 AR 로 연결
    public static final String URL_AR_GET_USER_LOCATION = Protocol.URL + "ar_getuserLocation.php";
    public static final String URL_AR_GET_ALL_USER_LOCATION = Protocol.URL + "ar_getalluserlocation.php";

    /* Client측에서 해결하는 Task*/
    //static final String URL_CHECK_GROUP_PWD="CHECK_GROUP_PWD";

}

