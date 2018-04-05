package erolasan.moodplayer;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;

import erolasan.moodplayer.moodrecognition.CameraActivity;
import erolasan.moodplayer.utils.AppBarStateChangeListener;
import erolasan.moodplayer.utils.Mood;
import erolasan.moodplayer.utils.SharedPref;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements Player.NotificationCallback {

    private Button recognize;
    private ImageView moodImg;
    private TextView moodTxt, moodTitle;
    private TextView trackName;
    private View overlay;
    private OnFragmentChangeRequest mListener;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        recognize = v.findViewById(R.id.recognize);

        moodImg = v.findViewById(R.id.moodImg);
        moodTxt = v.findViewById(R.id.moodTxt);
        moodTitle = v.findViewById(R.id.moodTitle);
        overlay = v.findViewById(R.id.overlay);
        trackName = v.findViewById(R.id.trackName);
        trackName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragment(0);
            }
        });

        AppBarLayout toolbarLayout = v.findViewById(R.id.app_bar);
        toolbarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                switch (state) {
                    case COLLAPSED:
                        moodImg.animate().scaleXBy(-0.4f);
                        moodImg.animate().scaleYBy(-0.4f);
                        moodImg.animate().translationY(65);
                        break;
                    case EXPANDED:
                        moodImg.animate().scaleX(1);
                        moodImg.animate().scaleY(1);
                        moodImg.animate().translationY(0);
                        recognize.animate().alpha(1);
                        moodTxt.animate().alpha(1);
                        moodTitle.animate().alpha(1);
                        break;
                    case IDLE:
                        recognize.animate().alpha(0);
                        moodTxt.animate().alpha(0);
                        moodTitle.animate().alpha(0);
                }
            }
        });

        v.findViewById(R.id.circlebg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppBarLayout) v.findViewById(R.id.app_bar)).setExpanded(true, true);
            }
        });

        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CameraActivity.class));
            }
        });

        return v;
    }

    public void setPlayerListener(){
        HomeActivity.mPlayer.addNotificationCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPref sharedPref = new SharedPref();
        if(sharedPref.getLastMood().equals("EMPTY_MOOD")){
            overlay.setVisibility(View.VISIBLE);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.ProgressDialogTheme);
            dialog.setTitle("Missing mood")
                    .setMessage("Before we start, let's recognize your mood first.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getActivity(), CameraActivity.class));
                        }
                    })
                    .show();
        }
        else {if(overlay.getVisibility() == View.VISIBLE) overlay.setVisibility(View.GONE);
            setMood(Mood.valueOf(sharedPref.getLastMood()));}
    }

    private void setMood(Mood mood) {
        switch (mood) {
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChangeRequest) {
            mListener = (OnFragmentChangeRequest) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyPlay:
            case kSpPlaybackNotifyNext:
            case kSpPlaybackNotifyPrev:
                trackName.setText(HomeActivity.mPlayer.getMetadata().currentTrack.name);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {

    }


    public interface OnFragmentChangeRequest {
        void changeFragment(int id);
    }
}
