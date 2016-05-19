package seojihyun.odya.pineapple;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SEOJIHYUN on 2016-05-15.
 */
public class SharedPreferencesManager {

    // 값 불러오기
    public static String getPreferences(Context ctx, String key){
        SharedPreferences pref = ctx.getSharedPreferences("info", Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    // 값 저장하기
    public static void savePreferences(Context ctx, String key, String value){
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
