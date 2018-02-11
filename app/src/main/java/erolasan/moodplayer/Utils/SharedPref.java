package erolasan.moodplayer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import erolasan.moodplayer.App;


/**
 * Created by Erol Asan on 2/10/2018.
 */

public class SharedPref {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String NAME_KEY = "user_name";

    public SharedPref() {
        sharedPref = App.getAppContext().getSharedPreferences(
                "user_pref", Context.MODE_PRIVATE);
    }

    public void putName(String name) {
        if (editor == null) editor = sharedPref.edit();
        editor.putString(NAME_KEY, name);
        editor.apply();
    }

    public String getName(){
        return sharedPref.getString(NAME_KEY, "EMPTY_NAME");
    }
}
