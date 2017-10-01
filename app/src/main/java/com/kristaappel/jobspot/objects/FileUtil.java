package com.kristaappel.jobspot.objects;


import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileUtil {

    public static void writeSavedJob(Context context, ArrayList<Job> savedJobs) {
        try {
            // Save the arraylist to local storage:
            FileOutputStream fileOutputStream = context.openFileOutput("savedjobs.txt", Context.MODE_PRIVATE);
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
            FileOutputStream fileOutputStream = context.openFileOutput("appliedjobs.txt", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(appliedJobs);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Job> readSavedJobs(Context context) {
        ArrayList<Job> savedJobs = new ArrayList<>();
        try {
            // Read from local storage:
            FileInputStream fileInputStream = context.openFileInput("savedjobs.txt");
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

    public static ArrayList<Job> readAppliedJobs(Context context) {
        ArrayList<Job> appliedJobs = new ArrayList<>();
        try {
            // Read from local storage:
            FileInputStream fileInputStream = context.openFileInput("appliedjobs.txt");
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
