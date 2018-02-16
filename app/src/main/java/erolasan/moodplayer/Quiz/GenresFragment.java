package erolasan.moodplayer.Quiz;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;

import java.util.HashSet;
import java.util.Set;

import erolasan.moodplayer.R;
import erolasan.moodplayer.Utils.SharedPref;


/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends Fragment {

    private GreetingsFragment.OnFragmentInteractionListener mListener;
    private Set<String> genres = new HashSet<>();

    public GenresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_genres, container, false);

        final ChipCloud chipCloud = v.findViewById(R.id.chip_cloud);
        final String[] chips = getActivity().getResources().getStringArray(R.array.genres);
        chipCloud.addChips(chips);

        chipCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int i) {
                genres.add(chips[i]);

            }

            @Override
            public void chipDeselected(int i) {
                genres.remove(chips[i]);
            }
        });

        v.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (genres.isEmpty()) Toast.makeText(getActivity(), "Please select at least one genre!", Toast.LENGTH_SHORT).show();
                else {
                    SharedPref sharedPref = new SharedPref();
                    sharedPref.putGenres(genres);
                    if (mListener != null) {
                        mListener.onFragmentInteraction();
                    }
                }

            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GreetingsFragment.OnFragmentInteractionListener) {
            mListener = (GreetingsFragment.OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
