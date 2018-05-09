package erolasan.moodplayer.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import erolasan.moodplayer.home.HomeActivity;
import erolasan.moodplayer.R;
import erolasan.moodplayer.utils.CustomViewPager;
import erolasan.moodplayer.utils.SharedPref;

public class QuizActivity extends AppCompatActivity
        implements OnFragmentInteractionListener {

    private CustomViewPager mPager;
    private QuizPagerAdapter mPagerAdapter;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_quiz);

        sharedPref = new SharedPref();
        if (sharedPref.getQuizCompleted()) goToMainAppScreen(); // skip the quiz if already done

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPagerAdapter = new QuizPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


    }

    @Override
    public void onFragmentInteraction() {
        Fragment current = mPagerAdapter.getItem(mPager.getCurrentItem());
        if (current instanceof LoginFragment) {
            mPager.setCurrentItem(1);
        } else if (current instanceof GreetingsFragment) {
            mPager.setCurrentItem(2);
        } else if (current instanceof GenresFragment) {
            mPager.setCurrentItem(3);
        } else if (current instanceof ArtistsFragment) {
            mPager.setCurrentItem(4);
        } else {
            sharedPref.putQuizCompleted(true);
            goToMainAppScreen();
        }
    }

    private class QuizPagerAdapter extends FragmentStatePagerAdapter {
        public QuizPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LoginFragment();
                case 1:
                    return new GreetingsFragment();
                case 2:
                    return new GenresFragment();
                case 3:
                    return new ArtistsFragment();
                case 4:
                    return new FinalFragment();
                default:
                    return new LoginFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }


    }

    private void goToMainAppScreen(){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
