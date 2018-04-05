package erolasan.moodplayer.moodrecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import erolasan.moodplayer.R;
import erolasan.moodplayer.utils.Mood;
import erolasan.moodplayer.utils.SharedPref;

public class MoodResultActivity extends AppCompatActivity{
private TextView moodTxt;
private ImageView moodImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mood_result);

        final Mood mood = (Mood) getIntent().getSerializableExtra("mood");

        moodTxt = findViewById(R.id.moodTxt);
        moodImg = findViewById(R.id.moodImg);
        setMood(mood);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPref sp = new SharedPref();
                sp.putLastMood(mood);
                finish();

            }
        });

        findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoodResultActivity.this, CameraActivity.class));
                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {
        // block the back button, user can either tap ok or retry or kill the app
    }

    private void setMood(Mood mood) {
        switch (mood){
            case HAPPY:
                moodTxt.setText(mood.toString());
                moodImg.setImageResource(R.drawable.mood_happy);
                break;
            case SAD:
                moodTxt.setText(mood.toString());
                moodImg.setImageResource(R.drawable.mood_sad);
                break;
            case ANGRY:
                moodTxt.setText(mood.toString());
                moodImg.setImageResource(R.drawable.mood_angry);
                break;
            case NEUTRAL:
                moodTxt.setText(mood.toString());
                moodImg.setImageResource(R.drawable.mood_neutral);
        }
    }
}
