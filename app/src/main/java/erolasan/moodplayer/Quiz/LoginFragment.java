package erolasan.moodplayer.Quiz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import erolasan.moodplayer.R;
import erolasan.moodplayer.Utils.SharedPref;


public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText name;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        name = (EditText) v.findViewById(R.id.name);
        v.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pattern ps = Pattern.compile("^[a-zA-Z]+$");
                Matcher ms = ps.matcher(name.getText().toString());
                boolean bs = ms.matches();
                if (!bs) {
                    Toast.makeText(getActivity(), "Please enter a valid name!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPref sharedPref = new SharedPref();
                    sharedPref.putName(name.getText().toString());
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
