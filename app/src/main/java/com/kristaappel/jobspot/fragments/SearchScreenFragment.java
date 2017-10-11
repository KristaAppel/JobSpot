package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.Job;
import java.util.ArrayList;


public class SearchScreenFragment extends android.app.Fragment {


    private static final String ARG_PARAM1 = "param1";
    private ArrayList<Job> jobs;
    public static String locationText;
    public static String keywordsText;


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

    public static SearchScreenFragment newInstance(String _location, String _keywords){
        SearchScreenFragment fragment = new SearchScreenFragment();
        locationText = _location;
        keywordsText = _keywords;
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

        if (BottomNavigationActivity.mapIsShowing){
            // Create and display a MapFragment:
            MapFragment mapFrag;
            if (jobs != null) {
                mapFrag = MapFragment.newInstance(jobs);
            }else if (locationText != null && keywordsText != null){
                mapFrag = MapFragment.newInstance(locationText, keywordsText);
            }else{
                mapFrag = new MapFragment();
            }
            getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, mapFrag).commit();
        }else if (BottomNavigationActivity.listIsShowing){
            // Create and display a SearchResultListFragment:
            SearchResultListFragment searchResultListFragment;
            if (jobs != null){
                searchResultListFragment = SearchResultListFragment.newInstance(jobs);
            }else if (locationText != null && keywordsText != null) {
                searchResultListFragment = SearchResultListFragment.newInstance(locationText, keywordsText);
            }else{
                searchResultListFragment = new SearchResultListFragment();
            }
            getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, searchResultListFragment).commit();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText et_loc = (EditText) view.findViewById(R.id.et_location);
        EditText et_kw = (EditText) getActivity().findViewById(R.id.et_keywords);

        // Display the location and keywords from the search in the editTexts:
        if (locationText != null){
            et_loc.setText(locationText);
        }
        if (keywordsText != null){
            et_kw.setText(keywordsText);
        }
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
