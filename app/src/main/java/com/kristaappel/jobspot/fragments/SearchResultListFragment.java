package com.kristaappel.jobspot.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kristaappel.jobspot.JobInfoActivity;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.Job;

import java.io.Serializable;
import java.util.ArrayList;


public class SearchResultListFragment extends ListFragment {

    private static final int ID_CONSTANT = 0x01010;
    private ArrayList<Job> jobs;
    private static final String ARG_PARAM1 = "param1";


    public SearchResultListFragment() {
        // empty public constructor
    }

    public static SearchResultListFragment newInstance(ArrayList<Job> joblist){
        SearchResultListFragment fragment = new SearchResultListFragment();
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
        }else{
            jobs = new ArrayList<>();
            // setEmptyText("no jobs yet");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setListAdapter(new AppliedListAdapter() {
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText("No Jobs to Display");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

         //Create and display a JobInfoFragment for the selected job:
        JobInfoFragment jobInfoFragment = JobInfoFragment.newInstance(jobs.get(position));
        getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, jobInfoFragment).commit();


//        // Create and display a JobInfoActivity for the selected job:
//        Intent jobInfoIntent = new Intent(getActivity(), JobInfoActivity.class);
//        Job job = jobs.get(position);
//        Bundle jobBundle = new Bundle();
//        jobBundle.putSerializable("extra_job", job);
//        jobInfoIntent.putExtra("job_bundle", jobBundle);
//        //jobInfoIntent.putExtra("extra_job", (Serializable) job);
//        startActivity(jobInfoIntent);
    }


    private class AppliedListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return jobs.size();
        }


        @Override
        public Object getItem(int position) {
            return jobs.get(position);
        }


        @Override
        public long getItemId(int position) {
            return ID_CONSTANT + position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.searchresult_list_item, parent, false);
            }

            // Get TextViews:
            TextView textTitle = (TextView) convertView.findViewById(R.id.textView_searchResult_title);
            TextView textCompany = (TextView) convertView.findViewById(R.id.textView_searchResult_company);
            TextView textDate = (TextView) convertView.findViewById(R.id.textView_searchResult_datePosted);

            // Set text:
            textTitle.setText(jobs.get(position).getJobTitle());
            textCompany.setText(jobs.get(position).getCompanyName());
            String jobDate = "Posted on: " + jobs.get(position).getDatePosted();
            textDate.setText(jobDate);

            // Get ImageButton and set appropriate image:
            final ImageButton favoriteButton = (ImageButton) convertView.findViewById(R.id.searchResult_favorite_button);
            //TODO: if the job is not saved:
            favoriteButton.setImageResource(R.drawable.ic_star_unsaved);
            favoriteButton.setTag(R.drawable.ic_star_unsaved);
            //TODO: else if job is saved, favoriteButton.setImageResource(R.drawable.ic_star_saved); favoriteButton.setTag(R.drawable.ic_star_saved);

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("SearchResultListFrag", "tapped a star!");
                    if ((Integer)favoriteButton.getTag() == R.drawable.ic_star_unsaved){
                        Log.i("SearchResultListFrag", "it's not saved");
                        favoriteButton.setImageResource(R.drawable.ic_star_saved);
                        favoriteButton.setTag(R.drawable.ic_star_saved);
                        //TODO: save the job to the device & Firebase
                    }else if ((Integer)favoriteButton.getTag() == R.drawable.ic_star_saved){
                        Log.i("SearchResultListFrag", "it's saved");
                        favoriteButton.setImageResource(R.drawable.ic_star_unsaved);
                        favoriteButton.setTag(R.drawable.ic_star_unsaved);
                        //TODO: unsave the job from the device and Firebase
                    }

                }
            });

            return convertView;
        }


    }

}
