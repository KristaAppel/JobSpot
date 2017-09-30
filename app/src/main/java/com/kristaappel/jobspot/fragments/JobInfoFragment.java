package com.kristaappel.jobspot.fragments;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.Job;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobInfoFragment extends android.app.Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Job job;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public JobInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment JobInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        Log.i("JobInfoFragment", "Selected job:" + job.getJobTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_info, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebView webView = (WebView) getActivity().findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(job.getJobURL());

        ImageButton sharebutton = (ImageButton) view.findViewById(R.id.jobInfo_button_share);
        ImageButton saveButton = (ImageButton) view.findViewById(R.id.jobInfo_button_save);
        ImageButton appliedButton = (ImageButton) view.findViewById(R.id.jobInfo_button_applied);
        sharebutton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        appliedButton.setOnClickListener(this);

        // Find out if job is saved:
        ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
        for (Job savedJob : savedJobs){
            if (savedJob.getJobID().equals(job.getJobID())){
                // The job is saved, so display filled star:
                saveButton.setImageResource(R.drawable.ic_star_saved);
                saveButton.setTag(R.drawable.ic_star_saved);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jobInfo_button_share:
                //TODO: share
                Log.i("JobInfoFragment", "share the job");
                break;
            case R.id.jobInfo_button_save:
                Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                ImageButton saveButton = (ImageButton) v.findViewById(R.id.jobInfo_button_save);

                // If the job is not saved, then save it:
                if ((Integer)saveButton.getTag() == R.drawable.ic_star_unsaved){
                    saveButton.setImageResource(R.drawable.ic_star_saved);
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
                    saveButton.setImageResource(R.drawable.ic_star_unsaved);
                    saveButton.setTag(R.drawable.ic_star_unsaved);
                    // Unsave the job from the device:
                    ArrayList<Job> savedJobs = FileUtil.readSavedJobs(getActivity());
                    ArrayList<Job> jobsToRemove = new ArrayList<Job>();
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

                //TODO: save job to device & firebase, change icon to red star
                Log.i("JobInfoFragment", "save the job");
                break;
            case R.id.jobInfo_button_applied:
                //TODO: save to applied list, then hide icon or change its color?
                Log.i("JobInfoFragment", "mark the job as applied");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
