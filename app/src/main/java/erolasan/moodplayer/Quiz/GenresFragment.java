package erolasan.moodplayer.Quiz;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adroitandroid.chipcloud.ChipCloud;

import erolasan.moodplayer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class GenresFragment extends Fragment {


    public GenresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_genres, container, false);

        ChipCloud chipCloud = v.findViewById(R.id.chip_cloud);
        chipCloud.addChips( getActivity().getResources().getStringArray(R.array.genres));
        return v;
    }

}
