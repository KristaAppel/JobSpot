package com.kristaappel.jobspot.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kristaappel.jobspot.R;


public class SearchResultListFragment extends ListFragment {

    private static final int ID_CONSTANT = 0x01010;
    private String[] jobtitles = {"Android Developer", "Mobile Developer", "Junior iOS Developer", "Cashier"};
    private String[] companies = {"Chase", "TechData", "Sparxoo", "Target"};

    public static SearchResultListFragment newInstance(){
        return new SearchResultListFragment();
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
            return jobtitles.length;
        }


        @Override
        public Object getItem(int position) {
            return jobtitles[position];
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
            textTitle.setText(jobtitles[position]);
            textCompany.setText(companies[position]);

            return convertView;
        }


    }

}
