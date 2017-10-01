package com.kristaappel.jobspot.objects;

import android.os.Parcel;
import android.os.Parcelable;
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


}







