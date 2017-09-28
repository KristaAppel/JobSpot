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
import com.kristaappel.jobspot.objects.Job;

import java.util.ArrayList;


public class SearchResultListFragment extends ListFragment {

    private static final int ID_CONSTANT = 0x01010;
    private ArrayList<Job> jobs;
    private static final String ARG_PARAM1 = "param1";
    private String[] jobtitles = {"Android Developer", "Mobile Developer", "Junior iOS Developer", "Cashier"};
    private String[] companies = {"Chase", "TechData", "Sparxoo", "Target"};

    public SearchResultListFragment() {
        // empty public constructor
    }

    public static SearchResultListFragment newInstance(ArrayList<Job> joblist){
        SearchResultListFragment fragment = new SearchResultListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, joblist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobs = getArguments().getParcelableArrayList(ARG_PARAM1);
        }else{
            jobs = new ArrayList<>();
            // setEmptyText("no jobs yet");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setListAdapter(new AppliedListAdapter() {
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //TODO: Go to the job info screen for the chosen job:
//        Intent powerWordScreenIntent = new Intent(getActivity(), PowerWordActivity.class);
//        powerWordScreenIntent.putExtra(EXTRA_IRLA_LEVEL_INDEX, position);
//        startActivity(powerWordScreenIntent);
    }


    private class AppliedListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return jobs.size();
        }


        @Override
        public Object getItem(int position) {
            return jobs.get(position);
        }


        @Override
        public long getItemId(int position) {
            return ID_CONSTANT + position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.searchresult_list_item, parent, false);
            }

            TextView textTitle = (TextView) convertView.findViewById(R.id.textView_applied_title);
            TextView textCompany = (TextView) convertView.findViewById(R.id.textView_applied_company);



            // Set text:
            textTitle.setText(jobs.get(position).getJobTitle());
            textCompany.setText(jobs.get(position).getCompanyName());

            return convertView;
        }


    }

}
