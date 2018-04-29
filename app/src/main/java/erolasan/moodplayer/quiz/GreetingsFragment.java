package erolasan.moodplayer.quiz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import erolasan.moodplayer.R;
import erolasan.moodplayer.utils.SharedPref;


public class GreetingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TextView helloUser;
    private SharedPref sharedPref;
    private boolean _hasLoadedOnce = false;

    public GreetingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_greetings, container, false);
        sharedPref = new SharedPref();
        helloUser = (TextView) v.findViewById(R.id.hello_user);

        v.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFragmentInteraction();
                }
            }
        });
        return v;
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(true);


        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isVisibleToUser && !_hasLoadedOnce) {
                helloUser.setText("Hello " + sharedPref.getName() + ",");
                _hasLoadedOnce = true;
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
