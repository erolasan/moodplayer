package erolasan.moodplayer.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import erolasan.moodplayer.R;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.PlaylistsPager;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Erol Asan on 3/17/2018.
 */
public class PlaylistGenerator {

    final private static int MAX_SONGS = 20;
    final private static int MIN_SONGS_FOR_2_OR_LESS_GENRES = 10;
    final private static int MIN_SONGS_FOR_3_OR_MORE_GENRES = 15;

    public static void generatePlaylist(Context ctx, final SpotifyService spotify, final PlaylistGeneratedCallback callback) {
        final Stack<String> genres = getMatchingGenres(ctx);

        // if genres = 0, fallback to strategy 2
        if (genres.empty()) {
            generatePlaylistWithoutGenres(spotify, new PlaylistGeneratedCallback() {
                @Override
                public void playlistGenerated(List<Track> playlist) {
                    if (playlist.size() >= MAX_SONGS) {
                        playlist = playlist.subList(0, MAX_SONGS);
                        callback.playlistGenerated(playlist);
                    }else{
                        if (playlist.size() < MIN_SONGS_FOR_2_OR_LESS_GENRES) {
                            generateAdditionalSongs(playlist, MIN_SONGS_FOR_2_OR_LESS_GENRES - playlist.size(), spotify, new PlaylistGeneratedCallback() {
                                @Override
                                public void playlistGenerated(List<Track> playlist) {
                                    callback.playlistGenerated(playlist);
                                }
                            });
                        }
                    }
                }
            });
            return;
        }

        // recursively generate a playlist for each genre - combine them in one playlist and return in callback
        generatePlaylistFromGenres(genres, new ArrayList<Track>(), spotify, new PlaylistGeneratedCallback() {
            @Override
            public void playlistGenerated(List<Track> playlist) {
                // shuffle tracks from different genres
                Collections.shuffle(playlist);

                //aim for playlists with 10-20 songs
                if (playlist.size() >= MAX_SONGS) {
                    playlist = playlist.subList(0, MAX_SONGS);
                    callback.playlistGenerated(playlist);
                } else {
                    if (genres.size() > 2) {
                        if (playlist.size() < MIN_SONGS_FOR_3_OR_MORE_GENRES)
                            generateAdditionalSongs(playlist, MIN_SONGS_FOR_3_OR_MORE_GENRES - playlist.size(), spotify, new PlaylistGeneratedCallback() {
                                @Override
                                public void playlistGenerated(List<Track> playlist) {
                                    callback.playlistGenerated(playlist);
                                }
                            });
                        else callback.playlistGenerated(playlist);
                    } else {
                        if (playlist.size() < MIN_SONGS_FOR_2_OR_LESS_GENRES)
                            generateAdditionalSongs(playlist, MIN_SONGS_FOR_2_OR_LESS_GENRES - playlist.size(), spotify, new PlaylistGeneratedCallback() {
                                @Override
                                public void playlistGenerated(List<Track> playlist) {
                                    callback.playlistGenerated(playlist);
                                }
                            });
                        else callback.playlistGenerated(playlist);
                    }
                }
            }
        });
    }

    private static void generateAdditionalSongs(final List<Track> playlist, final int numberOfSongs, final SpotifyService spotify, final PlaylistGeneratedCallback callback) {
        //spotify:track:id
        String trackID = playlist.get(playlist.size() / 2).id;

        // get random track from the middle of the playlist
        spotify.getTrack(trackID, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                // get related artist from track
                spotify.getRelatedArtists(track.artists.get(0).id, new Callback<Artists>() {
                    @Override
                    public void success(Artists artists, Response response) {
                        // get that artists top tracks
                        spotify.getArtistTopTrack(artists.artists.get(0).id, "GB", new Callback<Tracks>() {
                            @Override
                            public void success(Tracks tracks, Response response) {
                                // add a track to the playlist
                                playlist.add(tracks.tracks.get(0));

                                // if all required songs are added return; else generate another song
                                if (numberOfSongs == 1) callback.playlistGenerated(playlist);
                                else
                                    generateAdditionalSongs(playlist, numberOfSongs - 1, spotify, new PlaylistGeneratedCallback() {
                                        @Override
                                        public void playlistGenerated(List<Track> playlist) {
                                            callback.playlistGenerated(playlist);
                                        }
                                    });
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e("PlaylistGenerator", error.toString());
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("PlaylistGenerator", error.toString());
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("PlaylistGenerator", error.toString());
            }
        });
    }

    private static void generatePlaylistFromGenres(final Stack<String> genres, final List<Track> playlist, final SpotifyService spotify, final PlaylistGeneratedCallback callback) {
        final SharedPref sharedPref = new SharedPref();
        final Mood mood = Mood.valueOf(sharedPref.getLastMood());

        // add options
        HashMap<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 40);
        options.put(SpotifyService.MARKET, "GB");
        options.put("min_popularity", 50);

        // filter audio features depending on mood
        if (mood == Mood.SAD) {
            options.put("max_danceability", 0.5);
            options.put("max_energy", 0.6);
            options.put("max_liveness", 0.2);
        } else if(mood == Mood.HAPPY || mood == Mood.ANGRY) {
            options.put("min_danceability", 0.4);
            options.put("min_energy", 0.6);
        }

        // pop the genre
        options.put("seed_genres", genres.pop().toLowerCase());

        spotify.getRecommendations(options, new Callback<Recommendations>() {
            @Override
            public void success(Recommendations recommendations, Response response) {
                for (Track t : recommendations.tracks) {
                    // check for disliked artists
                    boolean add = true;
                    for (ArtistSimple a : t.artists) {
                        if (sharedPref.getArtists(false).contains(a.name)) add = false;
                    }

                    if (add) playlist.add(t);
                }


                // if no more genres return; else generate again
                if (genres.empty()) callback.playlistGenerated(playlist);
                else
                    generatePlaylistFromGenres(genres, playlist, spotify, new PlaylistGeneratedCallback() {
                        @Override
                        public void playlistGenerated(List<Track> playlist) {
                            callback.playlistGenerated(playlist);
                        }
                    });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("PlaylistGenerator", error.toString());
            }
        });
    }

    private static void generatePlaylistWithoutGenres(final SpotifyService spotify, final PlaylistGeneratedCallback callback) {
        SharedPref sharedPref = new SharedPref();
        spotify.searchPlaylists(sharedPref.getLastMood().toLowerCase(), new Callback<PlaylistsPager>() {
            @Override
            public void success(PlaylistsPager playlistsPager, Response response) {
                // get a random playlist from mood keyword
                Random random = new Random();
                PlaylistSimple playlist = playlistsPager.playlists.items.get(random.nextInt(playlistsPager.playlists.items.size()));
                spotify.getPlaylist(playlist.owner.id, playlist.id, new Callback<Playlist>() {
                    @Override
                    public void success(Playlist playlist, Response response) {
                        List<Track> trackList = new ArrayList<>();
                        for (PlaylistTrack t : playlist.tracks.items) {
                            trackList.add(t.track);
                        }
                        callback.playlistGenerated(trackList);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("PlaylistGenerator", error.toString());
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("PlaylistGenerator", error.toString());
            }
        });
    }

    private static Stack<String> getMatchingGenres(Context ctx) {
        SharedPref sharedPref = new SharedPref();
        Mood mood = Mood.valueOf(sharedPref.getLastMood());
        Set<String> userGenres = sharedPref.getGenres();
        List<String> genres;

        if (mood == Mood.HAPPY)
            genres = Arrays.asList(ctx.getResources().getStringArray(R.array.happy_genres));
        else if (mood == Mood.SAD)
            genres = Arrays.asList(ctx.getResources().getStringArray(R.array.sad_genres));
        else if (mood == Mood.ANGRY)
            genres = Arrays.asList(ctx.getResources().getStringArray(R.array.angry_genres));
        else
            genres = Arrays.asList(ctx.getResources().getStringArray(R.array.neutral_genres));

        Stack<String> finalGenres = new Stack<>();
        for (String g : userGenres) {
            if (genres.contains(g)) finalGenres.add(g);
        }

        return finalGenres;
    }

    public interface PlaylistGeneratedCallback {
        void playlistGenerated(List<Track> playlist);
    }
}
