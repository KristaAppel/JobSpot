package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.Job;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchScreenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchScreenFragment extends android.app.Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private ArrayList<Job> jobs;
    private OnFragmentInteractionListener mListener;


    public SearchScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment SearchScreenFragment.
     */
    public static SearchScreenFragment newInstance(ArrayList<Job> joblist) {
        SearchScreenFragment fragment = new SearchScreenFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, joblist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobs = getArguments().getParcelableArrayList(ARG_PARAM1);
        }

        // Create and display a SearchBoxFragment:
        SearchBoxFragment searchBoxFrag = new SearchBoxFragment();
        getFragmentManager().beginTransaction().replace(R.id.searchScreen_topContainer, searchBoxFrag).commit();

        // Create and display a MapFragment:
        MapFragment mapFrag = null;
        if (jobs != null){
            mapFrag = MapFragment.newInstance(jobs);
        }else{
            mapFrag = new MapFragment();
        }

        getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, mapFrag).commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_screen, container, false);


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnSearchBoxFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(View v);
    }
}
