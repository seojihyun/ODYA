package seojihyun.odya.pineapple.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import seojihyun.odya.pineapple.R;

/**
 * Created by SEOJIHYUN on 2016-05-15.
 * **Auto Login Service**
 */
public class LoginSharedPreferences {
    // 1. 로그인
    public static void setLogin(Activity ctx, String userPhone, String userName) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putString(ctx.getString(R.string.user_phone), userPhone);
        editor.putString(ctx.getString(R.string.user_name), userName);
        editor.commit();
    }
    //  로그아웃
    public static void logout(Activity ctx) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putString(ctx.getString(R.string.user_phone), "");
        editor.putString(ctx.getString(R.string.user_name), "");
        editor.commit();
    }

    //  로그인 여부
    public static boolean isLogin(Activity ctx) {
        return (ctx.getSharedPreferences("info", Context.MODE_PRIVATE).getString(ctx.getString(R.string.user_name), "") != "" && ctx.getSharedPreferences("info", Context.MODE_PRIVATE).getString(ctx.getString(R.string.user_phone), "") != "");
    }


    // 2. 그룹 입장
    public static void enterGroup(Activity ctx, String groupName) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putString(ctx.getString(R.string.group_name), groupName);
        editor.commit();
    }

    // 그룹 퇴장
    public static void exitGroup(Activity ctx) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putString(ctx.getString(R.string.group_name), "");
        editor.commit();
    }
    // 그룹 입장 여부
    public static boolean isGroup(Activity ctx) { //그룹 입장 여부
        return (ctx.getSharedPreferences("info", Context.MODE_PRIVATE).getString(ctx.getString(R.string.group_name), "") != "" );
    }







    // 값 불러오기
    public static String getPreferences(Context ctx, String key){
        SharedPreferences pref = ctx.getSharedPreferences("info", Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    // 값 저장하기
    public static void savePreferences(Activity ctx, String key, String value){
        SharedPreferences pref = ctx.getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // 값(Key Data) 삭제하기
    public static void removePreferences(Activity ctx, String key){
        SharedPreferences pref = ctx.getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    public static void removeAllPreferences(Activity ctx){
        SharedPreferences pref = ctx.getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
