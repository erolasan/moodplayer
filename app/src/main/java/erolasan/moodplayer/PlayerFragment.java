package erolasan.moodplayer;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment
        implements SpotifyPlayer.NotificationCallback{

    private TextView trackName, artistName;
    private Button prev, next, play;
    private OnFragmentChangeRequest mListener;

    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player, container, false);

        trackName = v.findViewById(R.id.trackName);
        artistName = v.findViewById(R.id.artistName);
        HomeActivity.mPlayer.addNotificationCallback(this);

        v.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.mPlayer.skipToNext(null);
            }
        });

        v.findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.mPlayer.skipToPrevious(null);
            }
        });

        v.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragment(1);
            }
        });

        return v;
    }

    public void updatePlayer(Metadata.Track track){
        trackName.setText(track.name);
        artistName.setText(track.artistName);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyPlay:
            case kSpPlaybackNotifyNext:
            case kSpPlaybackNotifyPrev:
                updatePlayer(HomeActivity.mPlayer.getMetadata().currentTrack);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {

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
        void changeFragment(int id);
    }
}
