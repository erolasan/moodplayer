package erolasan.moodplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import erolasan.moodplayer.App;


/**
 * Created by Erol Asan on 2/10/2018.
 */

public class SharedPref {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static final String NAME_KEY = "user_name";
    private static final String GENRES_KEY = "user_genres";
    private static final String ARTISTS_LIKED_KEY = "user_artists_liked";
    private static final String ARTISTS_DISLIKED_KEY = "user_artists_disliked";
    private static final String QUIZ_COMPLETED_KEY = "quiz_completed";
    private static final String LAST_MOOD_KEY = "last_mood";


    public SharedPref() {
        sharedPref = App.getAppContext().getSharedPreferences(
                "user_pref", Context.MODE_PRIVATE);
    }

    public void putName(String name) {
        if (editor == null) editor = sharedPref.edit();
        editor.putString(NAME_KEY, name);
        editor.apply();
    }

    public String getName() {
        return sharedPref.getString(NAME_KEY, "EMPTY_NAME");
    }

    public void putGenres(Set<String> genres) {
        if (editor == null) editor = sharedPref.edit();
        editor.putStringSet(GENRES_KEY, genres);
        editor.apply();

    }

    public Set<String> getGenres() {
        return sharedPref.getStringSet(GENRES_KEY, null);
    }

    public void putArtists(Set<String> artists, boolean liked) {
        if (editor == null) editor = sharedPref.edit();
        if (liked)
            editor.putStringSet(ARTISTS_LIKED_KEY, artists);
        else
            editor.putStringSet(ARTISTS_DISLIKED_KEY, artists);
        editor.apply();
    }

    public Set<String> getArtists(boolean liked) {
        if (liked)
            return sharedPref.getStringSet(ARTISTS_LIKED_KEY, new HashSet<String>());
        else
            return sharedPref.getStringSet(ARTISTS_DISLIKED_KEY, new HashSet<String>());

    }

    public void putQuizCompleted(boolean quizCompleted){
        if (editor == null) editor = sharedPref.edit();
        editor.putBoolean(QUIZ_COMPLETED_KEY, quizCompleted);
        editor.apply();
    }

    public boolean getQuizCompleted(){
        return sharedPref.getBoolean(QUIZ_COMPLETED_KEY, false);
    }

    public void putLastMood(Mood mood){
        if (editor == null) editor = sharedPref.edit();
        editor.putString(LAST_MOOD_KEY, mood.toString());
        editor.apply();
    }

    public String getLastMood(){
        return sharedPref.getString(LAST_MOOD_KEY, "EMPTY_MOOD");
    }
}
