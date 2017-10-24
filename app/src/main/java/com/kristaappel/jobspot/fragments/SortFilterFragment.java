package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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


public class SortFilterFragment extends android.app.Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private String radius;
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
        SavedSearch recentSearch = FileUtil.readMostRecentSearch(getActivity());
        if (recentSearch != null) {
            radius = recentSearch.getRadius();
            posted = recentSearch.getDays();
            Log.i("radius", radius);
        } else {
            Log.i("radius", "recent search is null");
            radius = "20";
            posted = "30";
        }
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
        okButton.setOnClickListener(this);

        RadioGroup radioGroupRadius = (RadioGroup) view.findViewById(R.id.radioGroup_radius);
        RadioGroup radioGroupPosted = (RadioGroup) view.findViewById(R.id.radioGroup_posted);
        RadioGroup radioGroupSortBy = (RadioGroup) view.findViewById(R.id.radioGroup_sortby);

        radioGroupRadius.setOnCheckedChangeListener(this);
        radioGroupPosted.setOnCheckedChangeListener(this);
        radioGroupSortBy.setOnCheckedChangeListener(this);

        sharedPreferences = getActivity().getSharedPreferences("com.kristaappel.jobspot.preferences", Context.MODE_PRIVATE);

        Log.i("radius2", radius);

        // Check the most recently used radius:
        String recentRadius = sharedPreferences.getString("radius", "20");
        RadioButton radiusButton1 = (RadioButton) getActivity().findViewById(R.id.radioButton_radius_1);
        radiusButton1.setChecked(recentRadius.equals("10"));
        RadioButton radiusButton2 = (RadioButton) getActivity().findViewById(R.id.radioButton_radius_2);
        radiusButton2.setChecked(recentRadius.equals("20"));
        RadioButton radiusButton3 = (RadioButton) getActivity().findViewById(R.id.radioButton_radius_3);
        radiusButton3.setChecked(recentRadius.equals("30"));
        RadioButton radiusButton4 = (RadioButton) getActivity().findViewById(R.id.radioButton_radius_4);
        radiusButton4.setChecked(recentRadius.equals("40"));

        // Check the most recently used posted date:
        String recentPosted = sharedPreferences.getString("posted", "30");
        RadioButton postedButton1 = (RadioButton) getActivity().findViewById(R.id.radioButton_posted_1);
        postedButton1.setChecked(recentPosted.equals("7"));
        RadioButton postedButton2 = (RadioButton) getActivity().findViewById(R.id.radioButton_posted_2);
        postedButton2.setChecked(recentPosted.equals("14"));
        RadioButton postedButton3 = (RadioButton) getActivity().findViewById(R.id.radioButton_posted_3);
        postedButton3.setChecked(recentPosted.equals("30"));

        // Check the most recently used sort option:
        String recentSortOption = sharedPreferences.getString("sortBy", "accquisitiondate");
        Log.i("recentSortOption", recentSortOption);
        RadioButton sortByButton1 = (RadioButton) getActivity().findViewById(R.id.radioButton_sortby_1);
        sortByButton1.setChecked(recentSortOption.equals("accquisitiondate"));
        RadioButton sortByButton2 = (RadioButton) getActivity().findViewById(R.id.radioButton_sortby_2);
        sortByButton2.setChecked(recentSortOption.equals("location"));
        RadioButton sortByButton3 = (RadioButton) getActivity().findViewById(R.id.radioButton_sortby_3);
        sortByButton3.setChecked(recentSortOption.equals("relevance"));
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (group.getId()) {
            // Get search radius:
            case R.id.radioGroup_radius:
                switch (checkedId) {
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
                editor.putString("radius", radius);
                break;
            // Get date range:
            case R.id.radioGroup_posted:
                switch (checkedId) {
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
                editor.putString("posted", posted);
                break;
            // Get sort type:
            case R.id.radioGroup_sortby:
                switch (checkedId) {
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
                break;
        }
        BottomNavigationActivity.radius = radius;
        BottomNavigationActivity.posted = posted;
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        Log.i("radius", radius + " " + posted + " " + sortBy);
        mListener.onSortFilterInteraction(radius, posted, sortBy);
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
