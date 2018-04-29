package erolasan.moodplayer.home;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import erolasan.moodplayer.R;
import erolasan.moodplayer.moodrecognition.CameraActivity;
import erolasan.moodplayer.utils.AppBarStateChangeListener;
import erolasan.moodplayer.utils.Mood;
import erolasan.moodplayer.utils.PlaylistGenerator;
import erolasan.moodplayer.utils.SharedPref;
import erolasan.moodplayer.utils.SpotifyPlayerWrapper;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ConnectionStateCallback, PlaylistAdapter.OnItemClicked {

    private Button generatePlaylist;
    private ImageView moodImg;
    private TextView moodTxt, moodTitle, placeholderText;
    private View overlay;
    private ProgressBar loadingProgress;
    private OnFragmentChangeRequest mListener;
    private SpotifyApi api;
    public static SpotifyPlayerWrapper mPlayer;
    private RecyclerView mRecyclerView;
    List<Track> tracks;
    public static PlaylistAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        generatePlaylist = v.findViewById(R.id.generatePlaylist);

        moodImg = v.findViewById(R.id.moodImg);
        moodTxt = v.findViewById(R.id.moodTxt);
        moodTitle = v.findViewById(R.id.moodTitle);
        overlay = v.findViewById(R.id.overlay);
        loadingProgress = v.findViewById(R.id.loadingProgress);
        placeholderText = v.findViewById(R.id.placeholderText);

        AppBarLayout toolbarLayout = v.findViewById(R.id.app_bar);
        toolbarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                switch (state) {
                    case COLLAPSED:
                        moodImg.animate().scaleXBy(-0.4f);
                        moodImg.animate().scaleYBy(-0.4f);
                        moodImg.animate().translationY(65);
                        generatePlaylist.setVisibility(View.GONE);
                        break;
                    case EXPANDED:
                        moodImg.animate().scaleX(1);
                        moodImg.animate().scaleY(1);
                        moodImg.animate().translationY(0);
                        generatePlaylist.setVisibility(View.VISIBLE);
                        generatePlaylist.animate().alpha(1);
                        moodTxt.animate().alpha(1);
                        moodTitle.animate().alpha(1);
                        break;
                    case IDLE:
                        generatePlaylist.animate().alpha(0);
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

        moodImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CameraActivity.class));
            }
        });

        generatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tracks == null || tracks.isEmpty()) {
                    mListener.generatePlaylist();
                    overlay.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.VISIBLE);
                }
                else {
                    overlay.setVisibility(View.VISIBLE);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.ProgressDialogTheme);
                    dialog.setTitle("Playlist already exists")
                            .setMessage("Are you sure you want to generate a new playlist?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    loadingProgress.setVisibility(View.VISIBLE);
                                    mListener.generatePlaylist();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    overlay.setVisibility(View.GONE);
                                }
                            })
                            .show();
                }
            }
        });

        //------------------------------------------------------------------------------------------
        // Playlist stuff
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), HORIZONTAL);
        mRecyclerView.addItemDecoration(itemDecor);

        return v;
    }

    public void setPlayerListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPref sharedPref = new SharedPref();
        if (sharedPref.getLastMood().equals("EMPTY_MOOD")) {
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
        } else {
            if (overlay.getVisibility() == View.VISIBLE) overlay.setVisibility(View.GONE);
            setMood(Mood.valueOf(sharedPref.getLastMood()));
        }
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

    public void generatePlaylist(String accessToken) {
        // setup spotify api and player
        if(mPlayer == null) {
            api = new SpotifyApi();
            api.setAccessToken(accessToken);
            Config playerConfig = new Config(getActivity(), accessToken, HomeActivity.CLIENT_ID);
            Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    mPlayer = new SpotifyPlayerWrapper(spotifyPlayer);
                    mPlayer.addConnectionStateCallback(HomeFragment.this);
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }else{
            final SpotifyService spotify = api.getService();
            PlaylistGenerator.generatePlaylist(getActivity(), spotify, new PlaylistGenerator.PlaylistGeneratedCallback() {
                @Override
                public void playlistGenerated(List<Track> playlist) {
                    tracks = playlist;
                    mAdapter.setmDataset(tracks);
                    mAdapter.notifyDataSetChanged();
                    mPlayer.setPlaylist(playlist);
                    mPlayer.play(0);
                    overlay.setVisibility(View.GONE);
                    loadingProgress.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onLoggedIn() {
        final SpotifyService spotify = api.getService();
        PlaylistGenerator.generatePlaylist(getActivity(), spotify, new PlaylistGenerator.PlaylistGeneratedCallback() {
            @Override
            public void playlistGenerated(List<Track> playlist) {
                tracks = playlist;
                mAdapter = new PlaylistAdapter(playlist, HomeFragment.this);
                mRecyclerView.setAdapter(mAdapter);
                mPlayer.setPlaylist(playlist);
                mPlayer.play(0);
                overlay.setVisibility(View.GONE);
                loadingProgress.setVisibility(View.GONE);
                placeholderText.setVisibility(View.GONE);
            }
        });
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(true);


        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isVisibleToUser) {
               PlaylistAdapter.selectedPosition = mPlayer.getCurrentTrack();
               mAdapter.notifyDataSetChanged();
               mRecyclerView.scrollToPosition(mPlayer.getCurrentTrack());
            }
        }
    }

    @Override
    public void itemClicked(int postition) {
        if(mPlayer.getCurrentTrack() != postition) mPlayer.play(postition);
        mAdapter.notifyDataSetChanged();
        mListener.changeFragment();
    }

    public interface OnFragmentChangeRequest {
        void changeFragment();

        void generatePlaylist();
    }
}
