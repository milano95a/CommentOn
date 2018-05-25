package com.example.aj.commenton.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.aj.commenton.UI.CommentActivity;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

//    public static void hideKeyboard(Activity activity) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        //Find the currently focused view, so we can grab the correct window token from it.
//        View view = activity.getCurrentFocus();
//        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = new View(activity);
//        }
//
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

    private static final String LOG_TAG = Utils.class.getName();

    public static void hideKeyboardFromFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null;

    }

    public static boolean isEmailValid(@NonNull String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(@NonNull String password) {
        return password.length() <= 7;
    }

    public static void showMessage(View view, int message) {
        Snackbar.make(view,
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }

    public static void showMessage(View view, String message) {
        Snackbar.make(view,
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }

    public static String convertDateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'KK:mm:ss");
        return sdf.format(date);
    }

    public static String convertStringDate(String date){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'KK:mm:ss");

            Date commentDate = sdf.parse(date);
            Date nowDate = new Date();

            int commentMinute = commentDate.getMinutes();
            int nowMinute = nowDate.getMinutes();

            int commentHour = commentDate.getHours();
            int nowHour = commentDate.getHours();

            SimpleDateFormat dayFormatter = new SimpleDateFormat("dd.MM.yyyy");
            String commentDay = dayFormatter.format(commentDate);
            String nowDay = dayFormatter.format(nowDate);

            if(commentDay.equals(nowDay)){
                if(nowHour - commentHour < 1){
                    if(nowMinute - commentMinute < 3){
                        return "now";
                    }else{
                        return nowMinute - commentMinute + " min";
                    }
                }else{
                    SimpleDateFormat timeFormatter = new SimpleDateFormat("KK:mm");
                    return timeFormatter.format(commentDate);
                }
            }else{
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
                return dateFormatter.format(commentDate);
            }

//            SimpleDateFormat hourFormatter = new SimpleDateFormat("KK");
//            SimpleDateFormat montFormatter = new SimpleDateFormat("MM");
//            SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");


//            Date commentDate = sdf.parse(date);
//            long commentTime = commentDate.getTime();
//
//            String nowDateStr = sdf.format(new Date());
//            Date nowDate = sdf.parse(nowDateStr);
//            long nowTime = nowDate.getTime();
//
//            long diff = nowTime - commentTime;
//
//            if(commentHour < nowHour){
//                return nowHour - commentHour + " sec";
//            }else if(diff < HOUR){
//                return diff / MIN + " min";
//            }else if (commentDay.equals(nowDay)){
//            }else {
//            }

        }catch (Exception e){
            Log.wtf(LOG_TAG, e.getMessage());
        }

        return "";
    }

    public static void storeValue(Context context, String key, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String retrieveValue(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "getUser");
    }

}
