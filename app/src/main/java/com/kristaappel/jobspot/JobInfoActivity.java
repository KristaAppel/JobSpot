package com.kristaappel.jobspot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kristaappel.jobspot.objects.Job;

public class JobInfoActivity extends AppCompatActivity {

    Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_info);

        job = (Job) getIntent().getSerializableExtra("extra_job");
        if (job != null){
            Log.i("JobInfoActivity", "the selected job is: " + job.getJobTitle());
        }
    }
}
