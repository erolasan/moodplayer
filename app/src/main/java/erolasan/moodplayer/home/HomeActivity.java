package erolasan.moodplayer.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import erolasan.moodplayer.R;
import erolasan.moodplayer.quiz.ArtistsFragment;
import erolasan.moodplayer.quiz.FinalFragment;
import erolasan.moodplayer.quiz.GenresFragment;
import erolasan.moodplayer.quiz.GreetingsFragment;
import erolasan.moodplayer.quiz.LoginFragment;
import erolasan.moodplayer.quiz.QuizActivity;
import erolasan.moodplayer.utils.CustomViewPager;
import erolasan.moodplayer.utils.PlaylistGenerator;
import erolasan.moodplayer.utils.SpotifyPlayerWrapper;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class HomeActivity extends AppCompatActivity implements
        HomeFragment.OnFragmentChangeRequest, PlayerFragment.OnFragmentChangeRequest {

    public static final String CLIENT_ID = "5fa8d5ce9baf426f823d979fd1a449b9";
    private static final String REDIRECT_URI = "moodplayer://callback";
    private static final int REQUEST_CODE = 5;
    private CustomViewPager mPager;
    private HomePagerAdapter mPagerAdapter;
    private HomeFragment homeFragment;
    private String accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        // Instantiate a ViewPager and a PagerAdapter.
        homeFragment = new HomeFragment();
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }


    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                accessToken = response.getAccessToken();
                homeFragment.generatePlaylist(accessToken);
            }
        }
    }

    @Override
    public void changeFragment() {
        Fragment current = mPagerAdapter.getItem(mPager.getCurrentItem());
        if (current instanceof HomeFragment) mPager.setCurrentItem(1);
        else mPager.setCurrentItem(0);
    }

    @Override
    public void generatePlaylist() {
        // Spotify setup
        if(accessToken == null) {
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                    AuthenticationResponse.Type.TOKEN,
                    REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }else{
            homeFragment.generatePlaylist(accessToken);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment current = mPagerAdapter.getItem(mPager.getCurrentItem());
        if (current instanceof PlayerFragment) mPager.setCurrentItem(0);
        else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    private class HomePagerAdapter extends FragmentStatePagerAdapter {
        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return homeFragment;
            else return new PlayerFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }


    }
}
