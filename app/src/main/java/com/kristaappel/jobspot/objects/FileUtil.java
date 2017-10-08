package com.kristaappel.jobspot.objects;


import android.content.Context;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class FileUtil {

    private static final String fileTypeSaved = "saved";
    private static final String fileTypeApplied = "applied";
    private static final String fileTypeSearch = "search";

    private static String getFileName(String fileType){
        Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String savedJobsFile = null;
        String appliedJobsFile = null;
        String savedSearchesFile = null;
        if (firebaseUser != null) {
            savedJobsFile = "savedjobs" + firebaseUser.getUid() + ".txt";
            appliedJobsFile = "appliedjobs" + firebaseUser.getUid() + ".txt";
            savedSearchesFile = "savedsearches" + firebaseUser.getUid() + ".txt";
        }
        if (fileType.equals(fileTypeSaved)){
            return savedJobsFile;
        }else if (fileType.equals(fileTypeApplied)) {
            return appliedJobsFile;
        }else if (fileType.equals(fileTypeSearch)){
            return savedSearchesFile;
        }else {
            return null;
        }
    }

    public static void writeSavedJob(Context context, ArrayList<Job> savedJobs) {
        try {
            // Save the arraylist to local storage:
            String fileName = getFileName(fileTypeSaved);
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(savedJobs);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeAppliedJob(Context context, ArrayList<Job> appliedJobs) {
        try {
            // Save the arraylist to local storage:
            String fileName = getFileName(fileTypeApplied);
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(appliedJobs);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeSavedSearch(Context context, ArrayList<SavedSearch> savedSearches){
        try {
            // Save the arraylist to local storage:
            String fileName = getFileName(fileTypeSearch);
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(savedSearches);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Job> readSavedJobs(Context context) {
        final ArrayList<Job> savedJobs = new ArrayList<>();

            try {
                // Read from local storage:
                String fileName = getFileName(fileTypeSaved);
                FileInputStream fileInputStream = context.openFileInput(fileName);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Object readObject = objectInputStream.readObject();

                // Get objects; add them to arraylist to be returned:
                if (readObject instanceof ArrayList<?>) {
                    ArrayList<?> arrayList = (ArrayList<?>) readObject;
                    if (arrayList.size() > 0) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            Object arrayListItem = arrayList.get(i);
                            if (arrayListItem instanceof Job) {
                                Job savedJob = (Job) arrayListItem;
                                savedJobs.add(savedJob);
                            }
                        }
                    }
                }
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return savedJobs;
    }

    public static ArrayList<SavedSearch> readSavedSearches(Context context) {
        final ArrayList<SavedSearch> savedSearches = new ArrayList<>();

        try {
            // Read from local storage:
            String fileName = getFileName(fileTypeSearch);
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Object readObject = objectInputStream.readObject();

            // Get objects; add them to arraylist to be returned:
            if (readObject instanceof ArrayList<?>) {
                ArrayList<?> arrayList = (ArrayList<?>) readObject;
                if (arrayList.size() > 0) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        Object arrayListItem = arrayList.get(i);
                        if (arrayListItem instanceof SavedSearch) {
                            SavedSearch savedSearch = (SavedSearch) arrayListItem;
                            savedSearches.add(savedSearch);
                        }
                    }
                }
            }
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedSearches;
    }


    public static ArrayList<Job> readAppliedJobs(Context context) {
        ArrayList<Job> appliedJobs = new ArrayList<>();
        try {
            // Read from local storage:
            String fileName = getFileName(fileTypeApplied);
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Object readObject = objectInputStream.readObject();

            // Get objects; add them to arraylist to be returned:
            if (readObject instanceof ArrayList<?>) {
                ArrayList<?> arrayList = (ArrayList<?>) readObject;
                if (arrayList.size() > 0) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        Object arrayListItem = arrayList.get(i);
                        if (arrayListItem instanceof Job) {
                            Job appliedJob = (Job) arrayListItem;
                            appliedJobs.add(appliedJob);
                        }
                    }
                }
            }
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appliedJobs;
    }

}
