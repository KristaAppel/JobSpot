package com.kristaappel.jobspot.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.Job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class JobInfoFragment extends android.app.Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private Job job;

    public JobInfoFragment() {
        // Required empty public constructor
    }


    public static JobInfoFragment newInstance(Job selectedJob) {
        JobInfoFragment fragment = new JobInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, selectedJob);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            job = (Job) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_info, container, false);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebView webView = (WebView) getActivity().findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(job.getJobURL());

        Button sharebutton = (Button) view.findViewById(R.id.jobInfo_button_share);
        Button saveButton = (Button) view.findViewById(R.id.jobInfo_button_save);
        Button appliedButton = (Button) view.findViewById(R.id.jobInfo_button_applied);
        sharebutton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        appliedButton.setOnClickListener(this);

        saveButton.setTag(R.drawable.ic_star_unsaved);
        appliedButton.setTag(R.drawable.ic_check_unchecked);

        // Find out if job is saved:
        ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
        for (Job savedJob : savedJobs){
            if (savedJob.getJobID().equals(job.getJobID())){
                // The job is saved, so display filled star:
                saveButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_star_saved), null, null);
                saveButton.setTag(R.drawable.ic_star_saved);
            }
        }
        // Find out if a job was save as applied:
        ArrayList<Job> appliedJobs = FileUtil.readAppliedJobs(getActivity());
        for (Job appliedJob : appliedJobs){
            if (appliedJob.getJobID().equals(job.getJobID())){
                // The job is saved as applied, so display a red check:
                appliedButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_check_checked), null, null);
                appliedButton.setTag(R.drawable.ic_check_checked);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {
        Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        switch (v.getId()){
            case R.id.jobInfo_button_share:
                //TODO: share
                Log.i("JobInfoFragment", "share the job");
                break;
            case R.id.jobInfo_button_save:
                Button saveButton = (Button) v.findViewById(R.id.jobInfo_button_save);

                // If the job is not saved, then save it:
                if ((Integer)saveButton.getTag() == R.drawable.ic_star_unsaved){
                    saveButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_star_saved), null, null);
                    saveButton.setTag(R.drawable.ic_star_saved);
                    // Save the job to the device:
                    ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
                    savedJobs.add(job);
                    FileUtil.writeSavedJob(getActivity(), savedJobs);
                    // Save the job to Firebase:
                    if (firebaseUser != null) {
                        firebase.child("users").child(firebaseUser.getUid()).child("savedjobs").child(job.getJobID()).setValue(job);
                    }
                    Toast.makeText(getActivity(), "Job has been saved.", Toast.LENGTH_SHORT).show();

                    // If the job is saved, then unsave it:
                }else if ((Integer)saveButton.getTag() == R.drawable.ic_star_saved){
                    saveButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_star_unsaved), null, null);
                    saveButton.setTag(R.drawable.ic_star_unsaved);
                    // Unsave the job from the device:
                    ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
                    ArrayList<Job> jobsToRemove = new ArrayList<>();
                    for (Job savedJob : savedJobs){
                        if (savedJob.getJobID().equals(job.getJobID())){
                            jobsToRemove.add(savedJob);
                        }
                    }
                    savedJobs.removeAll(jobsToRemove);
                    FileUtil.writeSavedJob(getActivity(), savedJobs);
                    // Unsave the job from Firebase:
                    if (firebaseUser != null) {
                        firebase.child("users").child(firebaseUser.getUid()).child("savedjobs").child(job.getJobID()).removeValue();
                    }
                    Toast.makeText(getActivity(), "Job has been removed.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.jobInfo_button_applied:
                Button appliedButton = (Button) v.findViewById(R.id.jobInfo_button_applied);

                // If the job is not saved as applied, then save it:
                if ((Integer)appliedButton.getTag() == R.drawable.ic_check_unchecked){
                    appliedButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_check_checked), null, null);
                    appliedButton.setTag(R.drawable.ic_check_checked);
                    // Save the job to the device:
                    ArrayList appliedJobs = FileUtil.readAppliedJobs(getActivity());
                    appliedJobs.add(job);
                    FileUtil.writeAppliedJob(getActivity(), appliedJobs);
                    // Save the job to Firebase:
                    if (firebaseUser != null){
                        firebase.child("users").child(firebaseUser.getUid()).child("appliedjobs").child(job.getJobID()).setValue(job);
                        String applyTime = new SimpleDateFormat("MM-dd-yyyy HH:mm a", Locale.US).format(Calendar.getInstance().getTime());
                        firebase.child("users").child(firebaseUser.getUid()).child("appliedjobs").child(job.getJobID()).child("applydate").setValue(applyTime);
                    }
                    Toast.makeText(getActivity(), "Job added to Applied Jobs.", Toast.LENGTH_SHORT).show();

                    // If the job is saved to applied list, then unsave it:
                }else if ((Integer)appliedButton.getTag() == R.drawable.ic_check_checked){
                    appliedButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_check_unchecked), null, null);
                    appliedButton.setTag(R.drawable.ic_check_unchecked);
                    // Unsave from the device:
                    ArrayList<Job> appliedJobs = FileUtil.readAppliedJobs(getActivity());
                    ArrayList<Job> jobsToRemove = new ArrayList<>();
                    for (Job appliedJob : appliedJobs){
                        if (appliedJob.getJobID().equals(job.getJobID())){
                            jobsToRemove.add(appliedJob);
                        }
                    }
                    appliedJobs.removeAll(jobsToRemove);
                    FileUtil.writeSavedJob(getActivity(), appliedJobs);
                    // Unsave from Firebase:
                    if (firebaseUser != null) {
                        firebase.child("users").child(firebaseUser.getUid()).child("appliedjobs").child(job.getJobID()).removeValue();
                    }
                    Toast.makeText(getActivity(), "Job has been removed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
