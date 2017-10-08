package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.R;


public class SearchBoxFragment extends android.app.Fragment implements View.OnClickListener{

    private OnSearchBoxFragmentInteractionListener mListener;

    public SearchBoxFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_box, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set click listeners:
        Button mapButton = (Button) view.findViewById(R.id.mapFragToggle1);
        mapButton.setOnClickListener(this);

        Button listButton = (Button) view.findViewById(R.id.mapFragToggle2);
        listButton.setOnClickListener(this);

        ImageButton myLocationButton = (ImageButton) view.findViewById(R.id.locationButton);
        myLocationButton.setOnClickListener(this);

        ImageButton filtersButton = (ImageButton) view.findViewById(R.id.filterButton);
        filtersButton.setOnClickListener(this);

        ImageButton recentButton = (ImageButton) view.findViewById(R.id.recentButton);
        recentButton.setOnClickListener(this);

        ImageButton searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        // Display the location and keywords from the search in the editTexts:
        if (SearchScreenFragment.locationText != null){
            EditText et_loc = (EditText) getActivity().findViewById(R.id.et_location);
            et_loc.setText(SearchScreenFragment.locationText);
        }
        if (SearchScreenFragment.keywordsText != null){
            EditText et_kw = (EditText) getActivity().findViewById(R.id.et_keywords);
            et_kw.setText(SearchScreenFragment.keywordsText);
        }

        if (BottomNavigationActivity.mapIsShowing){
            // Set buttons to appropriate colors:
            mapButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mapButton.setTextColor(getResources().getColor(R.color.colorWhite));
            listButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
            listButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else if (BottomNavigationActivity.listIsShowing){
            // Set buttons to appropriate colors:
            mapButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
            mapButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            listButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            listButton.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchBoxFragmentInteractionListener) {
            mListener = (OnSearchBoxFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnSearchBoxFragmentInteractionListener {
        void onSearchBoxFragmentInteraction(int id);
    }

    @Override
    public void onClick(View v) {
        mListener.onSearchBoxFragmentInteraction(v.getId());
    }

}
