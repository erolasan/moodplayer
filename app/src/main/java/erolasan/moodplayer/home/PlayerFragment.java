package erolasan.moodplayer.home;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlayerEvent;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import erolasan.moodplayer.R;
import erolasan.moodplayer.utils.SharedPref;
import erolasan.moodplayer.utils.SpotifyPlayerWrapper;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment
        implements SpotifyPlayerWrapper.CustomNotificationCallback {

    private TextView trackName, artistName, trackLength, trackProgressText;
    private ProgressBar trackProgressBar;
    private OnFragmentChangeRequest mListener;
    private ImageButton prev, next, playPause, shuffle, repeat;
    private SpotifyPlayerWrapper mPlayer;
    private ImageView trackPic;
    private ScheduledExecutorService timeUpdateService = Executors.newSingleThreadScheduledExecutor();
    private View v;
    private boolean _hasLoadedOnce = false;


    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_player, container, false);

        trackName = v.findViewById(R.id.trackName);
        artistName = v.findViewById(R.id.artistName);
        trackLength = v.findViewById(R.id.trackLength);
        trackProgressText = v.findViewById(R.id.trackProgressText);
        trackProgressBar = v.findViewById(R.id.trackProgressBar);
        trackPic = v.findViewById(R.id.trackPic);
        playPause = v.findViewById(R.id.playPause);
        prev = v.findViewById(R.id.prev);
        next = v.findViewById(R.id.next);
        shuffle = v.findViewById(R.id.shuffle);
        repeat = v.findViewById(R.id.repeat);

        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(true);

        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isVisibleToUser && !_hasLoadedOnce) {

                mPlayer = HomeFragment.mPlayer;
                mPlayer.setNotificationCallback(this);

                // Buttons/Interactions
                playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayer.getPlaybackState().isPlaying) {
                            mPlayer.pause();
                            playPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                        } else {
                            playPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                            mPlayer.resume();
                        }
                    }
                });


                prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlayer.playPrev();
                    }
                });

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlayer.playNext();
                    }
                });

                shuffle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayer.getShuffle())
                            mPlayer.setShuffle(false);
                        else
                            mPlayer.setShuffle(true);

                    }
                });

                repeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayer.getRepeat())
                            mPlayer.setRepeat(false);
                        else
                            mPlayer.setRepeat(true);
                    }
                });

                v.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.changeFragment();
                    }
                });

                // initial setup
                updatePlayer();

                // start scrubber service and update track progress
                timeUpdateService.scheduleWithFixedDelay(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (mPlayer != null) {
                                    final long position = mPlayer.getPlaybackState().positionMs;
                                    trackProgressBar.setProgress((int) position);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String length = String.format(Locale.UK, "%d:%02d",
                                                    TimeUnit.MILLISECONDS.toMinutes(position),
                                                    TimeUnit.MILLISECONDS.toSeconds(position) -
                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))
                                            );
                                            trackProgressText.setText(length);
                                        }
                                    });
                                }

                            }
                        }, 0, 100, TimeUnit.MILLISECONDS);

                _hasLoadedOnce = true;
            }
        }
    }



    private void updatePlayer() {

        if (mPlayer != null) {
            Metadata.Track track = mPlayer.getMetadata().currentTrack;

            // set play/pause button state
            if (mPlayer.getPlaybackState().isPlaying)
                playPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            else playPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);

            // set replay button state (on by default)
            if(mPlayer.getRepeat())repeat.setImageTintList(ColorStateList.valueOf(Color.parseColor("#EC9461")));
            else repeat.setImageTintList(ColorStateList.valueOf(Color.WHITE));

            trackName.setText(track.name);
            artistName.setText(track.artistName);

            Picasso.with(getActivity()).load(track.albumCoverWebUrl).memoryPolicy(MemoryPolicy.NO_CACHE).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    trackPic.setImageBitmap(bitmap);
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch swatch = palette.getDarkMutedSwatch();
                            if (swatch == null) v.setBackgroundColor(Color.parseColor("#2B2C5A"));
                            else v.setBackgroundColor(swatch.getRgb());
                        }
                    });
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e("error", errorDrawable.toString());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });


            trackProgressBar.setMax((int) track.durationMs);
            String length = String.format(Locale.UK, "%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(track.durationMs),
                    TimeUnit.MILLISECONDS.toSeconds(track.durationMs) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(track.durationMs))
            );
            trackLength.setText(length);
        }

    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyTrackChanged:
                updatePlayer();
                break;
            case kSpPlaybackNotifyShuffleOn:
                shuffle.setImageTintList(ColorStateList.valueOf(Color.parseColor("#EC9461")));
                break;
            case kSpPlaybackNotifyShuffleOff:
                shuffle.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                break;
            case kSpPlaybackNotifyRepeatOn:
                repeat.setImageTintList(ColorStateList.valueOf(Color.parseColor("#EC9461")));
                break;
            case kSpPlaybackNotifyRepeatOff:
                repeat.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                break;
            default:
                break;
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

    public interface OnFragmentChangeRequest {
        void changeFragment();
    }
}
