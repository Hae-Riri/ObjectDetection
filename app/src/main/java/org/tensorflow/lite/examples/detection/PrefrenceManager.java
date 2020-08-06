package org.tensorflow.lite.examples.detection;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefrenceManager {
    public static final String PREFRENCES_NAME="rebuild_preference";

    private static final String DEFAULT_VALUE_STRING="";
    private static final boolean DEFAULT_VALUE_BOOLEAN=false;
    private static final int DEFUALT_VALUE_INT=-1;
    private static final long DEFUALT_VALUE_LONG=-1L;
    private static final float DEFUALT_VALUE_FLOAT=-1F;

    private static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(PREFRENCES_NAME,Context.MODE_PRIVATE);
    }

    public static void setBoolean(Context context,String key, boolean value){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public static void setString(Context context,String key, String value){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static boolean getBoolean(Context context, String key){
        SharedPreferences prefs = getPreferences(context);
        boolean value = prefs.getBoolean(key,DEFAULT_VALUE_BOOLEAN);
        return value;
    }
    public static String getString(Context context, String key){
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key,DEFAULT_VALUE_STRING);
        return value;
    }
    public static void removeKey(Context context,String key){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }

}
