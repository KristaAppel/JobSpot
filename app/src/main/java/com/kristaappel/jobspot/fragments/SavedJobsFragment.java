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


public class SavedJobsFragment extends ListFragment {

    private ArrayList<Job> savedJobs;
    private static final int ID_CONSTANT = 0x01010;


    public static SavedJobsFragment newInstance(){
        return new SavedJobsFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        savedJobs = FileUtil.readSavedJobs(context);
        setListAdapter(new SavedListAdapter() {
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        savedJobs = FileUtil.readSavedJobs(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText("No Saved Jobs");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //TODO: Go to the job info screen for the chosen job:
//        Intent powerWordScreenIntent = new Intent(getActivity(), PowerWordActivity.class);
//        powerWordScreenIntent.putExtra(EXTRA_IRLA_LEVEL_INDEX, position);
//        startActivity(powerWordScreenIntent);

//        //Create and display a JobInfoFragment for the selected job:
//        JobInfoFragment jobInfoFragment = JobInfoFragment.newInstance(savedJobs.get(position));
//        getFragmentManager().beginTransaction().replace(R.id.searchScreen_bottomContainer, jobInfoFragment).commit();

    }


    private class SavedListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return savedJobs.size();
        }


        @Override
        public Object getItem(int position) {
            return savedJobs.get(position);
        }


        @Override
        public long getItemId(int position) {
            return ID_CONSTANT + position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.saved_list_item, parent, false);
            }

            TextView textTitle = (TextView) convertView.findViewById(R.id.textView_saved_title);
            TextView textCompany = (TextView) convertView.findViewById(R.id.textView_saved_company);

            // Set text:
            textTitle.setText(savedJobs.get(position).getJobTitle());
            textCompany.setText(savedJobs.get(position).getCompanyName());

            return convertView;
        }


    }

}
