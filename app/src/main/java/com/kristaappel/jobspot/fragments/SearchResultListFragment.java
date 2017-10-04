package com.kristaappel.jobspot.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.Job;
import com.kristaappel.jobspot.objects.LocationHelper;

import java.util.ArrayList;


public class SearchResultListFragment extends ListFragment {

    private static final int ID_CONSTANT = 0x01010;
    private ArrayList<Job> jobs;
    private static final String ARG_PARAM1 = "param1";
    private Firebase firebase;
    private FirebaseUser firebaseUser;


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

            // Get TextViews:
            TextView textTitle = (TextView) convertView.findViewById(R.id.textView_searchResult_title);
            TextView textCompany = (TextView) convertView.findViewById(R.id.textView_searchResult_company);
            TextView textDate = (TextView) convertView.findViewById(R.id.textView_searchResult_datePosted);
            TextView textDistance = (TextView) convertView.findViewById(R.id.searchResult_distance);

            // Set text:
            textTitle.setText(jobs.get(position).getJobTitle());
            textCompany.setText(jobs.get(position).getCompanyName());
            String jobDate = "Posted on: " + jobs.get(position).getDatePosted();
            textDate.setText(jobDate);
            String distanceString = jobs.get(position).getDistance(getActivity(), jobs.get(position)) + " miles away";
            textDistance.setText(distanceString);

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
                    firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    firebaseUser = mAuth.getCurrentUser();

                    // If the job is not saved, then save it:
                    if ((Integer)favoriteButton.getTag() == R.drawable.ic_star_unsaved){
                        favoriteButton.setImageResource(R.drawable.ic_star_saved);
                        favoriteButton.setTag(R.drawable.ic_star_saved);
                        // Save the job to the device:
                        ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
                        savedJobs.add(jobs.get(position));
                        FileUtil.writeSavedJob(getActivity(), savedJobs);
                        // Save the job to Firebase:
                        if (firebaseUser != null) {
                            firebase.child("users").child(firebaseUser.getUid()).child("savedjobs").child(jobs.get(position).getJobID()).setValue(jobs.get(position));
                        }
                        Toast.makeText(getActivity(), "Job has been saved.", Toast.LENGTH_SHORT).show();

                        // If the job is saved, then unsave it:
                    }else if ((Integer)favoriteButton.getTag() == R.drawable.ic_star_saved){
                        favoriteButton.setImageResource(R.drawable.ic_star_unsaved);
                        favoriteButton.setTag(R.drawable.ic_star_unsaved);
                        // Unsave the job from the device:
                        ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
                        ArrayList<Job> jobsToRemove = new ArrayList<>();
                        for (Job savedJob : savedJobs){
                            if (savedJob.getJobID().equals(jobs.get(position).getJobID())){
                                jobsToRemove.add(savedJob);
                            }
                        }
                        savedJobs.removeAll(jobsToRemove);
                        FileUtil.writeSavedJob(getActivity(), savedJobs);
                        // Unsave the job from Firebase:
                        if (firebaseUser != null) {
                            firebase.child("users").child(firebaseUser.getUid()).child("savedjobs").child(jobs.get(position).getJobID()).removeValue();
                        }
                        Toast.makeText(getActivity(), "Job has been removed.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return convertView;
        }


    }

}
