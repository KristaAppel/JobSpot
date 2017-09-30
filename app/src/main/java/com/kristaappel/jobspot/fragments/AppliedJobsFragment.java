package com.kristaappel.jobspot.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.Job;

import java.util.ArrayList;


public class AppliedJobsFragment extends ListFragment {

    private ArrayList<Job> appliedJobs;
    private static final int ID_CONSTANT = 0x01010;


    public static AppliedJobsFragment newInstance(){
        return new AppliedJobsFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appliedJobs = FileUtil.readAppliedJobs(context);
        setListAdapter(new AppliedListAdapter() {
        });
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.applied_list_item, parent, false);
            }

            TextView textTitle = (TextView) convertView.findViewById(R.id.textView_applied_title);
            TextView textCompany = (TextView) convertView.findViewById(R.id.textView_applied_company);



            // Set text:
            textTitle.setText(appliedJobs.get(position).getJobTitle());
            textCompany.setText(appliedJobs.get(position).getCompanyName());

            return convertView;
        }


    }

}
