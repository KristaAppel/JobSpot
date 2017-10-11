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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.Job;
import com.kristaappel.jobspot.objects.NetworkMonitor;

import java.util.ArrayList;
import java.util.HashMap;


public class AppliedJobsFragment extends ListFragment {

    private ArrayList<Job> appliedJobs;
    private static final int ID_CONSTANT = 0x01010;
    private AppliedListAdapter listAdapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appliedJobs = FileUtil.readAppliedJobs(context);
        listAdapter = new AppliedListAdapter();
        setListAdapter(listAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NetworkMonitor.deviceIsConnected(getActivity())){
            // We're online.  Get list from Firebase:
            Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                Firebase firebaseSavedRef = firebase.child("users").child(firebaseUser.getUid()).child("appliedjobs");
                firebaseSavedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        appliedJobs.clear();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            Log.i("FileUtil", "snapshot: " + userSnapshot.getValue());

                            HashMap snapshotJob = (HashMap) userSnapshot.getValue();
                            String jobID = snapshotJob.get("jobID").toString();
                            String jobTitle = snapshotJob.get("jobTitle").toString();
                            String companyName = snapshotJob.get("companyName").toString();
                            String datePosted = snapshotJob.get("datePosted").toString();
                            String jobURL = snapshotJob.get("jobURL").toString();
                            String jobCityState = snapshotJob.get("jobCityState").toString();
                            Double jobLat = (Double) snapshotJob.get("jobLat");
                            Double jobLng = (Double) snapshotJob.get("jobLng");

                            Job appliedJob = new Job(jobID, jobTitle, companyName, datePosted, jobURL, jobCityState, jobLat, jobLng);

                            appliedJobs.add(appliedJob);
                            listAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        }else{
            appliedJobs = FileUtil.readSavedJobs(getActivity());
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText("You have not applied for any jobs yet.");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //Create and display a JobInfoFragment for the selected job:
        JobInfoFragment jobInfoFragment = JobInfoFragment.newInstance(appliedJobs.get(position));
        getFragmentManager().beginTransaction().replace(R.id.content, jobInfoFragment).commit();
    }


    private class AppliedListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appliedJobs.size();
        }


        @Override
        public Object getItem(int position) {
            return appliedJobs.get(position);
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

            // Get text views:
            TextView textTitle = (TextView) convertView.findViewById(R.id.textView_saved_title);
            TextView textCompany = (TextView) convertView.findViewById(R.id.textView_saved_company);

            // Set text:
            textTitle.setText(appliedJobs.get(position).getJobTitle());
            textCompany.setText(appliedJobs.get(position).getCompanyName());

            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.savedJobs_delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Unsave the job from Firebase:
                    Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        firebase.child("users").child(firebaseUser.getUid()).child("appliedjobs").child(appliedJobs.get(position).getJobID()).removeValue();
                    }
                    // Unsave the job from the device:
                    ArrayList<Job> jobsToRemove = new ArrayList<>();
                    for (Job appliedJob : appliedJobs){
                        if (appliedJob.getJobID().equals(appliedJobs.get(position).getJobID())){
                            jobsToRemove.add(appliedJob);
                        }
                    }
                    appliedJobs.removeAll(jobsToRemove);
                    FileUtil.writeAppliedJob(getActivity(), appliedJobs);
                    Toast.makeText(getActivity(), "Job has been removed.", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }


    }

}
