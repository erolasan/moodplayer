package erolasan.moodplayer.utils;

import android.util.Log;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import kaaes.spotify.webapi.android.models.Track;

public class SpotifyPlayerWrapper implements Player.NotificationCallback {
    private Player mPlayer;
    private List<Track> playlist;
    private int currentTrack;
    private boolean repeat, shuffle;
    private CustomNotificationCallback notificationCallback;

    public SpotifyPlayerWrapper(Player player) {
        mPlayer = player;
        mPlayer.addNotificationCallback(this);
        repeat = true;
    }

    public void play(int index) {
        currentTrack = index;
        mPlayer.playUri(null, playlist.get(index).uri, 0, 0);

    }

    public void playNext() {
        if ((playlist.size() - 1) == currentTrack) {
            if (repeat) {
                play(0);
            }
        } else {
            if(shuffle){
                Random rnd = new Random();
                play(rnd.nextInt(playlist.size()));
            }else{
                play(currentTrack + 1);
            }

        }

    }

    public void playPrev() {
        if (currentTrack == 0) {
            if (repeat) {
                play(playlist.size() - 1);
            }
        } else {
            play(currentTrack - 1);
        }
    }

    public void pause() {
        mPlayer.pause(null);
    }

    public void resume() {
        mPlayer.resume(null);
    }

    public void setPlaylist(List<Track> playlist) {
        this.playlist = playlist;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
        if (repeat)
            notifyPlayer(PlayerEvent.kSpPlaybackNotifyRepeatOn);
        else
            notifyPlayer(PlayerEvent.kSpPlaybackNotifyRepeatOff);
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        if (shuffle)
            notifyPlayer(PlayerEvent.kSpPlaybackNotifyShuffleOn);
        else
            notifyPlayer(PlayerEvent.kSpPlaybackNotifyShuffleOff);
    }

    public boolean getShuffle() {
        return shuffle;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public int getCurrentTrack() {
        return currentTrack;
    }

    public PlaybackState getPlaybackState() {
        return mPlayer.getPlaybackState();
    }

    public Metadata getMetadata() {
        return mPlayer.getMetadata();
    }

    public void addConnectionStateCallback(ConnectionStateCallback callback) {
        mPlayer.addConnectionStateCallback(callback);
    }

    public void setNotificationCallback(CustomNotificationCallback callback) {
        notificationCallback = callback;
    }

    private void notifyPlayer(PlayerEvent event) {
        if (notificationCallback != null) {
            notificationCallback.onPlaybackEvent(event);
        }
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        switch (playerEvent){
            case kSpPlaybackNotifyPlay:
                Log.e("player", "Play");
                break;
            case kSpPlaybackNotifyTrackChanged:
                Log.e("player", "trackChanged");
                notifyPlayer(PlayerEvent.kSpPlaybackNotifyTrackChanged);
                break;
            case kSpPlaybackNotifyPause:
                Log.e("player", "pause");
                break;
            case kSpPlaybackNotifyTrackDelivered:
                playNext();
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {

    }

    public interface CustomNotificationCallback {
        void onPlaybackEvent(PlayerEvent var1);
    }
}
