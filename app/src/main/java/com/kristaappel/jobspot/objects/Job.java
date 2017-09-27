package com.kristaappel.jobspot.objects;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Job implements Serializable {

    private String jobID;
    private String jobTitle;
    private String companyName;
    private String datePosted;
    private String jobURL;
    private String jobCityState;
    private LatLng jobLatLng;


    public Job(String jobid, String jobtitle, String companyname, String dateposted, String joburl, String jobcitystate, LatLng joblatlng){
        jobID = jobid;
        jobTitle = jobtitle;
        companyName = companyname;
        datePosted = dateposted;
        jobURL = joburl;
        jobCityState = jobcitystate;
        jobLatLng = joblatlng;
    }


    public String getJobID() {
        return jobID;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public String getJobURL() {
        return jobURL;
    }

    public String getJobCityState() {
        return jobCityState;
    }

    public LatLng getJobLatLng() {
        return jobLatLng;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Job){
            Job otherPerson = (Job) obj;
            return this.getJobID().equals(otherPerson.getJobID());
        }
        return false;
    }


}







