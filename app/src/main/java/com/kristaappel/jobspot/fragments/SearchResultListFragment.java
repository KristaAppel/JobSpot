package com.kristaappel.jobspot.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.Job;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;


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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.searchresult_list_item, parent, false);
            }
            Log.i("SearchResultslistfrag", "title: " + jobs.get(position).getJobTitle() + " job id: " + jobs.get(position).getJobID());
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
            favoriteButton.setImageResource(R.drawable.ic_star_unsaved);
            favoriteButton.setTag(R.drawable.ic_star_unsaved);

            // Find out if job is saved:
            ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getContext());
            for (Job savedJob : savedJobs){
                if (savedJob.getJobID().equals(jobs.get(position).getJobID())){
                    // The job is saved, so display filled star:
                    favoriteButton.setImageResource(R.drawable.ic_star_saved);
                    favoriteButton.setTag(R.drawable.ic_star_saved);
                }
            }


            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("SearchResultListFrag", "tapped a star!");
                    if ((Integer)favoriteButton.getTag() == R.drawable.ic_star_unsaved){
                        favoriteButton.setImageResource(R.drawable.ic_star_saved);
                        favoriteButton.setTag(R.drawable.ic_star_saved);
                        //TODO: save the job to Firebase
                        // Save the job to the device:
                        ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
                        savedJobs.add(jobs.get(position));
                        FileUtil.writeSavedJob(getActivity(), savedJobs);
                        Toast.makeText(getActivity(), "Job has been saved.", Toast.LENGTH_SHORT).show();

                    }else if ((Integer)favoriteButton.getTag() == R.drawable.ic_star_saved){
                        favoriteButton.setImageResource(R.drawable.ic_star_unsaved);
                        favoriteButton.setTag(R.drawable.ic_star_unsaved);
                        //TODO: unsave the job from Firebase
                        // Unsave the job from the device:
                        ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
                        ArrayList<Job> jobsToRemove = new ArrayList<Job>();
                        for (Job savedJob : savedJobs){//////////////
                            if (savedJob.getJobID().equals(jobs.get(position).getJobID())){
                                jobsToRemove.add(savedJob);
                            }
                        }
                        savedJobs.removeAll(jobsToRemove);
                        Toast.makeText(getActivity(), "Job has been removed.", Toast.LENGTH_SHORT).show();
                        FileUtil.writeSavedJob(getActivity(), savedJobs);
                    }

                }
            });

            return convertView;
        }


    }

}
