package com.kristaappel.jobspot.objects;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.kristaappel.jobspot.BottomNavigationActivity;
import com.kristaappel.jobspot.fragments.MapFragment;

import java.io.Serializable;

public class Job implements Serializable, Parcelable{

    private String jobID;
    private String jobTitle;
    private String companyName;
    private String datePosted;
    private String jobURL;
    private String jobCityState;
    private double jobLat;
    private double jobLng;


    public Job(String jobid, String jobtitle, String companyname, String dateposted, String joburl, String jobcitystate, double joblat, double joblng){
        jobID = jobid;
        jobTitle = jobtitle;
        companyName = companyname;
        datePosted = dateposted;
        jobURL = joburl;
        jobCityState = jobcitystate;
        jobLat = joblat;
        jobLng = joblng;
    }


    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public void setJobURL(String jobURL) {
        this.jobURL = jobURL;
    }

    public void setJobCityState(String jobCityState) {
        this.jobCityState = jobCityState;
    }

    public void setJobLat(double jobLat) {
        this.jobLat = jobLat;
    }

    public void setJobLng(double jobLng) {
        this.jobLng = jobLng;
    }

    private Job(Parcel in) {
        jobID = in.readString();
        jobTitle = in.readString();
        companyName = in.readString();
        datePosted = in.readString();
        jobURL = in.readString();
        jobCityState = in.readString();
        jobLat = in.readDouble();
        jobLng = in.readDouble();
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

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

    public double getJobLat() {
        return jobLat;
    }

    public double getJobLng(){
        return jobLng;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Job){
            Job otherPerson = (Job) obj;
            return this.getJobID().equals(otherPerson.getJobID());
        }
        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobID);
        dest.writeString(jobTitle);
        dest.writeString(companyName);
        dest.writeString(datePosted);
        dest.writeString(jobURL);
        dest.writeString(jobCityState);
        dest.writeDouble(jobLat);
        dest.writeDouble(jobLng);
    }

    public Double getDistance(Context context, Job job){
        Location currentLocation = LocationHelper.getCurrentLocation(context, new MapFragment());
        Location jobLocation = new Location("");
        jobLocation.setLatitude(job.getJobLat());
        jobLocation.setLongitude(job.getJobLng());

        float distanceInMeters = currentLocation.distanceTo(jobLocation);

        return Math.round((distanceInMeters * 0.000621371) * 10)/10.0;
    }

}







