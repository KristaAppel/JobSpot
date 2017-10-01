package com.kristaappel.jobspot;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SortFilterFragment.OnSortFilterInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SortFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SortFilterFragment extends android.app.Fragment implements RadioGroup.OnCheckedChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RadioGroup radioGroupRadius;
    RadioGroup radioGroupPosted;
    RadioGroup radioGroupSortBy;
    String radius = "20";
    String posted = "30";
    String sortBy = "accquisitiondate";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnSortFilterInteractionListener mListener;

    public SortFilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SortFilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SortFilterFragment newInstance(String param1, String param2) {
        SortFilterFragment fragment = new SortFilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSortFilterInteraction(radius, posted, sortBy);
            }
        });
        radioGroupRadius = (RadioGroup) view.findViewById(R.id.radioGroup_radius);
        radioGroupPosted = (RadioGroup) view.findViewById(R.id.radioGroup_posted);
        radioGroupSortBy = (RadioGroup) view.findViewById(R.id.radioGroup_sortby);

        radioGroupRadius.setOnCheckedChangeListener(this);
        radioGroupPosted.setOnCheckedChangeListener(this);
        radioGroupSortBy.setOnCheckedChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSortFilterInteractionListener) {
            mListener = (OnSortFilterInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
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
