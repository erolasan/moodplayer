package erolasan.moodplayer.moodrecognition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import java.util.List;
import erolasan.moodplayer.R;
import erolasan.moodplayer.utils.Mood;


public class CameraActivity extends AppCompatActivity implements Detector.ImageListener {
    private Detector detector;
    private Camera mCamera;
    private CameraPreview mPreview;
    private ProgressDialog dialog;
    private float happy, sad, angry, winkTongue, kiss, tongue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        dialog = new ProgressDialog(this, R.style.ProgressDialogTheme);
        dialog.setMessage("Just a sec...");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.capture).setEnabled(false);
        findViewById(R.id.capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CameraActivity.this, MoodResultActivity.class);
                i.putExtra("mood", getMood());
                startActivity(i);
                detector.stop();
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        detector = configureDetector(mPreview);
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                detector.start();
            }
        });

    }


    private Detector configureDetector(CameraPreview mPreview) {

        Detector detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT,
                mPreview, 1, Detector.FaceDetectorMode.LARGE_FACES);
        detector.setImageListener(this);
        detector.setDetectAllEmojis(true);
        detector.setDetectAllEmotions(true);
        return detector;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(1); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    public void onImageResults(List<Face> faces, Frame frame, float v) {
        if(dialog.isShowing()) dialog.dismiss();

        if (faces == null)
            return; //frame was not processed

        if (faces.size() == 0) {
            findViewById(R.id.face_text).setVisibility(View.VISIBLE);
            findViewById(R.id.capture).setEnabled(false);
            return; //no face found
        } else {
            findViewById(R.id.face_text).setVisibility(View.GONE);
            findViewById(R.id.capture).setEnabled(true);
        }


        //For each face found
        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);

            this.happy = face.emotions.getJoy() + face.emojis.getLaughing() + face.emojis.getSmiley();
            this.sad = face.emotions.getSadness() + face.emojis.getDisappointed() + face.emojis.getFlushed();
            this.angry = face.emotions.getAnger() + face.emojis.getRage() + face.emojis.getScream();
            this.tongue = face.emojis.getStuckOutTongue();
            this.winkTongue = face.emojis.getStuckOutTongueWinkingEye();
            this.kiss = face.emojis.getKissing();

        }
    }

    private Mood getMood() {

        int upperThreshold = 35;
        int lowerThreshold = 3;
        // special case
        if (tongue > upperThreshold || winkTongue > upperThreshold || kiss > upperThreshold)
            return Mood.HAPPY;

        // if all other below 5 then NEUTRAL
        if (happy < lowerThreshold && sad < lowerThreshold && angry < lowerThreshold)
            return Mood.NEUTRAL;

        if (happy > sad && happy > angry) return Mood.HAPPY;
        else if (sad > happy && sad > angry ) return Mood.SAD;
        else return Mood.ANGRY;
    }
}
