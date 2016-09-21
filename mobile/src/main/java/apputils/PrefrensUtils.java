package apputils;

import android.content.Context;
import android.content.SharedPreferences;

import subcodevs.communicator.HomeScreen;

/**
 * Created by nupadhay on 9/15/2016.
 */
public class PrefrensUtils {
    private static SharedPreferences sharedpreferences;
    private  static String KEY_DEVICE_TOKEN="device_token";
    private  static String KEY_USER_ID="user_id";
    private  static String KEY_USER_NAME="user_name";
    private static String  KEY_USER_PUSH_TOKEN="user_push_token";
    private static String KEY_MD_DEVICE_TOKEN="md_token";
    private static String KEY_PASSWORD="password";


    public static void setDeviceToken(Context context, String token) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_DEVICE_TOKEN, token);
        editor.apply();
    }

    public static String getDeviceToken(Context context) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_DEVICE_TOKEN, "");
    }

    public static void setPushToken(Context context, String token) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_USER_PUSH_TOKEN, token);
        editor.apply();
    }

    public static String getPushToken(Context context) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_USER_PUSH_TOKEN, "");
    }


    public static void setUserID(Context context, String id) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_USER_ID, id);
        editor.apply();
    }

    public static String getUserID(Context context) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_USER_ID, "");
    }

    public static void setUserName(Context context, String name) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public static String getUserName(Context context) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_USER_NAME, "");
    }


    public static void setMDDeviceToken(Context context, String mdDeviceToken) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_MD_DEVICE_TOKEN, mdDeviceToken);
        editor.apply();
    }


    public static String getMDDevideToken(Context context) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_MD_DEVICE_TOKEN, "");
    }

    public static void setPassword(Context context, String name) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(KEY_PASSWORD, name);
        editor.apply();
    }

    public static String getPassword(Context context) {
        sharedpreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedpreferences.getString(KEY_PASSWORD, "");
    }

}
