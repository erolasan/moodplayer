package erolasan.moodplayer.quiz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import erolasan.moodplayer.R;
import erolasan.moodplayer.utils.SharedPref;


public class ArtistsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private List<String> genres = new ArrayList<>();
    private Map<String, String[]> artistsMap = new HashMap<>();
    private Stack<String> gameList = new Stack<>();
    private boolean _hasLoadedOnce = false;
    private CardStackView cardStackView;
    private ArtistAdapter adapter;
    private Set<String> artistsLiked = new HashSet<>();
    private Set<String> artistsDisliked = new HashSet<>();

    public ArtistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artists, container, false);
        cardStackView = v.findViewById(R.id.cardStack);
        adapter = new ArtistAdapter(getActivity());
        cardStackView.setAdapter(adapter);

        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(true);


        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isVisibleToUser && !_hasLoadedOnce) {
                SharedPref sharedPref = new SharedPref();
                extractMainGenres(sharedPref.getGenres());
                if (genres.isEmpty()) {     //if no main genre type has been chosen skip this fragment
                    if (mListener != null) {
                        mListener.onFragmentInteraction();
                    }
                } else {
                    generateGameList();
                    setup();
                }

                _hasLoadedOnce = true;
            }
        }
    }


    private void setup() {

        for (String s : gameList) {
            adapter.add(new QuizArtist(s));
            adapter.notifyDataSetChanged();
        }

        cardStackView.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {

            }

            @Override
            public void onCardSwiped(SwipeDirection direction) {
                String name = adapter.getItem(cardStackView.getTopIndex() - 1).getName();
                if (direction == SwipeDirection.Right) {
                    artistsLiked.add(name);
                } else if (direction == SwipeDirection.Left) {
                    artistsDisliked.add(name);
                }
                if (cardStackView.getTopIndex() == adapter.getCount()){
                    SharedPref sharedPref = new SharedPref();
                    sharedPref.putArtists(artistsLiked, true);
                    sharedPref.putArtists(artistsDisliked, false);
                    if (mListener != null)
                        mListener.onFragmentInteraction();
                }


            }

            @Override
            public void onCardReversed() {

            }

            @Override
            public void onCardMovedToOrigin() {

            }

            @Override
            public void onCardClicked(int index) {

            }
        });
    }

    private void generateGameList() {

        for (String s : genres) {
            if (genres.size() >= 3) {
                gameList.add(artistsMap.get(s)[0]);
                gameList.add(artistsMap.get(s)[1]);
            } else if (genres.size() == 2) {
                gameList.add(artistsMap.get(s)[0]);
                gameList.add(artistsMap.get(s)[1]);
                gameList.add(artistsMap.get(s)[2]);
                gameList.add(artistsMap.get(s)[3]);
            } else if (genres.size() == 1)
                Collections.addAll(gameList, artistsMap.get(s));

        }
    }

    private void extractMainGenres(Set<String> allGenres) {
        for (String s : allGenres) {
            switch (s) {
                case "Hip-Hop":
                    artistsMap.put(s, getActivity().getResources().getStringArray(R.array.artists_hiphop));
                    genres.add(s);
                    break;
                case "Rock":
                    artistsMap.put(s, getActivity().getResources().getStringArray(R.array.artists_rock));
                    genres.add(s);
                    break;
                case "Metal":
                    artistsMap.put(s, getActivity().getResources().getStringArray(R.array.artists_metal));
                    genres.add(s);
                    break;
                case "EDM":
                    artistsMap.put(s, getActivity().getResources().getStringArray(R.array.artists_edm));
                    genres.add(s);
                    break;
                case "Jazz":
                    artistsMap.put(s, getActivity().getResources().getStringArray(R.array.artists_jazz));
                    genres.add(s);
                    break;
                case "Classical":
                    artistsMap.put(s, getActivity().getResources().getStringArray(R.array.artists_classical));
                    genres.add(s);
                    break;
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
}
