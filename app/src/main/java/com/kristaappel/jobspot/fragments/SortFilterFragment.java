package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.SavedSearch;

import java.io.File;


public class SortFilterFragment extends android.app.Fragment implements RadioGroup.OnCheckedChangeListener {

    private String radius = "20";
    private String posted = "30";
    private String sortBy = "accquisitiondate";
    private OnSortFilterInteractionListener mListener;
    SharedPreferences sharedPreferences;

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

        SavedSearch recentSearch = FileUtil.readMostRecentSearch(getActivity());
        if (recentSearch != null) {
            radius = recentSearch.getRadius();
            posted = recentSearch.getDays();
        }

        RadioButton radiusButton1 = (RadioButton) getActivity().findViewById(R.id.radioButton_radius_1);
        radiusButton1.setChecked(radius.equals("10"));
        RadioButton radiusButton2 = (RadioButton) getActivity().findViewById(R.id.radioButton_radius_2);
        radiusButton2.setChecked(radius.equals("20"));
        RadioButton radiusButton3 = (RadioButton) getActivity().findViewById(R.id.radioButton_radius_3);
        radiusButton3.setChecked(radius.equals("30"));
        RadioButton radiusButton4 = (RadioButton) getActivity().findViewById(R.id.radioButton_radius_4);
        radiusButton4.setChecked(radius.equals("40"));

        RadioButton postedButton1 = (RadioButton) getActivity().findViewById(R.id.radioButton_posted_1);
        postedButton1.setChecked(posted.equals("7"));
        RadioButton postedButton2 = (RadioButton) getActivity().findViewById(R.id.radioButton_posted_2);
        postedButton2.setChecked(posted.equals("14"));
        RadioButton postedButton3 = (RadioButton) getActivity().findViewById(R.id.radioButton_posted_3);
        postedButton3.setChecked(posted.equals("30"));

        sharedPreferences = getActivity().getSharedPreferences("com.kristaappel.jobspot.preferences", Context.MODE_PRIVATE);
        String lastSortOption = sharedPreferences.getString("sortBy", "accquisitiondate");
        Log.i("lastSortOption", lastSortOption);
        RadioButton sortByButton1 = (RadioButton) getActivity().findViewById(R.id.radioButton_sortby_1);
        sortByButton1.setChecked(lastSortOption.equals("accquisitiondate"));
        RadioButton sortByButton2 = (RadioButton) getActivity().findViewById(R.id.radioButton_sortby_2);
        sortByButton2.setChecked(lastSortOption.equals("location"));
        RadioButton sortByButton3 = (RadioButton) getActivity().findViewById(R.id.radioButton_sortby_3);
        sortByButton3.setChecked(lastSortOption.equals("relevance"));
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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (checkedId){
                    case R.id.radioButton_sortby_1:
                        sortBy = "accquisitiondate";
                        break;
                    case R.id.radioButton_sortby_2:
                        sortBy = "location";
                        break;
                    case R.id.radioButton_sortby_3:
                        sortBy = "relevance";
                        break;
                }
                editor.putString("sortBy", sortBy);
                editor.apply();
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
