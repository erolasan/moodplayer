package erolasan.moodplayer;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationHandler;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import erolasan.moodplayer.moodrecognition.CameraActivity;
import erolasan.moodplayer.utils.AppBarStateChangeListener;
import erolasan.moodplayer.utils.Mood;
import erolasan.moodplayer.utils.SharedPref;

public class HomeActivity extends AppCompatActivity
        implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback,
        HomeFragment.OnFragmentChangeRequest, PlayerFragment.OnFragmentChangeRequest {

    private static final String CLIENT_ID = "5fa8d5ce9baf426f823d979fd1a449b9";
    private static final String REDIRECT_URI = "moodplayer://callback";
    private static final int REQUEST_CODE = 5;
    public static Player mPlayer;
    private FragmentManager fm;
    private HomeFragment hf;
    private PlayerFragment pf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        // Spotify setup
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


        // Fragments setup
        fm = getSupportFragmentManager();
        hf = new HomeFragment();
        pf = new PlayerFragment();
        fm.beginTransaction().replace(R.id.fragment_container, hf).commit();


    }


    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void changeFragment(int id) {
        if(id == 0){
            // change to player view
            fm.beginTransaction().replace(R.id.fragment_container, pf).commit();
        }else{
            // change to home view
            fm.beginTransaction().replace(R.id.fragment_container, hf).commit();
        }
    }

    //----------------------------------------------------------------------------------------------
    // SPOTIFY SECTION


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(HomeActivity.this);
                        mPlayer.addNotificationCallback(HomeActivity.this);
                        hf.setPlayerListener();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onLoggedIn() {
        mPlayer.playUri(null, "spotify:user:anonerror:playlist:4tJvw3Ae7fui5mUoEOFTnA", 0, 0);
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Error error) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
    }

    @Override
    public void onPlaybackError(Error error) {

    }

    //----------------------------------------------------------------------------------------------
}
