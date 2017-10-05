package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.R;



public class SortFilterFragment extends android.app.Fragment implements RadioGroup.OnCheckedChangeListener {

    private String radius = "20";
    private String posted = "30";
    private String sortBy = "accquisitiondate";
    private OnSortFilterInteractionListener mListener;

    public SortFilterFragment() {
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
        return inflater.inflate(R.layout.fragment_sort_filter, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button okButton = (Button) view.findViewById(R.id.sortFilter_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSortFilterInteraction(radius, posted, sortBy);
            }
        });
        RadioGroup radioGroupRadius = (RadioGroup) view.findViewById(R.id.radioGroup_radius);
        RadioGroup radioGroupPosted = (RadioGroup) view.findViewById(R.id.radioGroup_posted);
        RadioGroup radioGroupSortBy = (RadioGroup) view.findViewById(R.id.radioGroup_sortby);

        radioGroupRadius.setOnCheckedChangeListener(this);
        radioGroupPosted.setOnCheckedChangeListener(this);
        radioGroupSortBy.setOnCheckedChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSortFilterInteractionListener) {
            mListener = (OnSortFilterInteractionListener) context;
            BottomNavigationActivity.sortBy = "accquisitiondate";
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.onSortFilterInteraction(radius, posted, sortBy);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (group.getId()){
            // Get search radius:
            case R.id.radioGroup_radius:
                switch (checkedId){
                    case R.id.radioButton_radius_1:
                        radius = "10";
                        break;
                    case R.id.radioButton_radius_2:
                        radius = "20";
                        break;
                    case R.id.radioButton_radius_3:
                        radius = "30";
                        break;
                    case R.id.radioButton_radius_4:
                        radius = "40";
                        break;
                }
                break;
            // Get date range:
            case R.id.radioGroup_posted:
                switch (checkedId){
                    case R.id.radioButton_posted_1:
                        posted = "7";
                        break;
                    case R.id.radioButton_posted_2:
                        posted = "14";
                        break;
                    case R.id.radioButton_posted_3:
                        posted = "30";
                        break;
                }
                break;
            // Get sort type:
            case R.id.radioGroup_sortby:
                switch (checkedId){
                    case R.id.radioButton_sortby_1:
                        sortBy = "accqusitiondate";
                        break;
                    case R.id.radioButton_sortby_2:
                        sortBy = "location";
                        break;
                    case R.id.radioButton_sortby_3:
                        sortBy = "relevance";
                        break;
                }
                break;
        }
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
    public interface OnSortFilterInteractionListener {
        // Send the radio button input back:
        void onSortFilterInteraction(String radius, String posted, String sortBy);
    }
}
