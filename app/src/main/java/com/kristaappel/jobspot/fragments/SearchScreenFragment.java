package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.Job;
import java.util.ArrayList;


public class SearchScreenFragment extends android.app.Fragment {


    private static final String ARG_PARAM1 = "param1";
    private ArrayList<Job> jobs;


    public SearchScreenFragment() {
        // Required empty public constructor
    }

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
        MapFragment mapFrag;
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
