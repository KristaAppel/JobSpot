package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

        EditText et_loc = (EditText) getActivity().findViewById(R.id.et_location);
        EditText et_kw = (EditText) getActivity().findViewById(R.id.et_keywords);

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
            et_loc.setText(SearchScreenFragment.locationText);
        }
        if (SearchScreenFragment.keywordsText != null){
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
    public void onPause() {

        // Save the location and keywords from the last search to the activity so they can
        // be displayed when the user comes back to this screen:
        EditText et_loc = (EditText) getActivity().findViewById(R.id.et_location);
        EditText et_kw = (EditText) getActivity().findViewById(R.id.et_keywords);
        if (et_loc != null && et_loc.getText() != null){
            BottomNavigationActivity.location = et_loc.getText().toString();
        }
        if (et_kw != null && et_kw.getText() != null){
            BottomNavigationActivity.keywords = et_kw.getText().toString();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Display the location and keywords from the last search:
        EditText et_loc = (EditText) getActivity().findViewById(R.id.et_location);
        EditText et_kw = (EditText) getActivity().findViewById(R.id.et_keywords);

        if (BottomNavigationActivity.location!=null){
            et_loc.setText(BottomNavigationActivity.location);
        }
        if (BottomNavigationActivity.keywords!=null){
            et_kw.setText(BottomNavigationActivity.keywords);
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
