package com.kristaappel.jobspot.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.media.Image;
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

import java.util.ArrayList;


public class SavedJobsFragment extends ListFragment {

    private ArrayList<Job> savedJobs;
    private static final int ID_CONSTANT = 0x01010;


    public static SavedJobsFragment newInstance(){
        return new SavedJobsFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        savedJobs = FileUtil.readSavedJobs(context);
        setListAdapter(new SavedListAdapter() {
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        savedJobs = FileUtil.readSavedJobs(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText("No Saved Jobs");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //Create and display a JobInfoFragment for the selected job:
        JobInfoFragment jobInfoFragment = JobInfoFragment.newInstance(savedJobs.get(position));
        getFragmentManager().beginTransaction().replace(R.id.content, jobInfoFragment).commit();

    }


    private class SavedListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return savedJobs.size();
        }


        @Override
        public Object getItem(int position) {
            return savedJobs.get(position);
        }


        @Override
        public long getItemId(int position) {
            return ID_CONSTANT + position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.saved_list_item, parent, false);
            }

            // Get TextViews:
            TextView textTitle = (TextView) convertView.findViewById(R.id.textView_saved_title);
            TextView textCompany = (TextView) convertView.findViewById(R.id.textView_saved_company);

            // Set text:
            textTitle.setText(savedJobs.get(position).getJobTitle());
            textCompany.setText(savedJobs.get(position).getCompanyName());

            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.savedJobs_delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Unsave the job from Firebase:
                    Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        firebase.child("users").child(firebaseUser.getUid()).child("savedjobs").child(savedJobs.get(position).getJobID()).removeValue();
                    }
                    // Unsave the job from the device:
                    ArrayList<Job> jobsToRemove = new ArrayList<Job>();
                    for (Job savedJob : savedJobs){
                        if (savedJob.getJobID().equals(savedJobs.get(position).getJobID())){
                            jobsToRemove.add(savedJob);
                        }
                    }
                    savedJobs.removeAll(jobsToRemove);
                    FileUtil.writeSavedJob(getActivity(), savedJobs);
                    Toast.makeText(getActivity(), "Job has been removed.", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }


    }

}
