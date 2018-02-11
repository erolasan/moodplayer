package erolasan.moodplayer;

import android.app.Application;
import android.content.Context;

/**
 * Created by medie on 2/11/2018.
 */

public class App extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}
