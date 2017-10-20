package com.kristaappel.jobspot.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kristaappel.jobspot.R;
import com.kristaappel.jobspot.objects.FileUtil;
import com.kristaappel.jobspot.objects.NetworkMonitor;
import com.kristaappel.jobspot.objects.SavedSearch;

import java.util.ArrayList;
import java.util.HashMap;


public class SavedSearchListFragment extends ListFragment {

    private ArrayList<SavedSearch> savedSearches;
    private static final int ID_CONSTANT = 0x01010;
    private SavedSearchAdapter listAdapter;
    private OnSavedSearchInteractionListener mListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        savedSearches = FileUtil.readSavedSearches(context);
        listAdapter = new SavedSearchAdapter();
        setListAdapter(listAdapter);

        if (context instanceof OnSavedSearchInteractionListener){
            mListener = (OnSavedSearchInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NetworkMonitor.deviceIsConnected(getActivity())){
            // We're online.  Get list from Firebase:
            Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                Firebase firebaseSavedRef = firebase.child("users").child(firebaseUser.getUid()).child("savedsearches");
                firebaseSavedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        savedSearches.clear();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            Log.i("SavedSearchFrag", "snapshot: " + userSnapshot.getValue());

                            HashMap snapshotSearch = (HashMap) userSnapshot.getValue();
                            String keywords = snapshotSearch.get("keywords").toString();
                            String location = snapshotSearch.get("location").toString();
                            String days = snapshotSearch.get("days").toString();
                            String radius = snapshotSearch.get("radius").toString();
                            String dateTime = snapshotSearch.get("dateTime").toString();

                            SavedSearch aSearch = new SavedSearch(keywords, radius, location, days, dateTime);

                            boolean foundMatch = false;
                            for (int i=0; i<savedSearches.size(); i++){
                                if (savedSearches.get(i).getKeywords().equals(aSearch.getKeywords()) && savedSearches.get(i).getLocation().equals(aSearch.getLocation())){
                                    foundMatch = true;
                                }
                            }
                            if (!foundMatch){
                                savedSearches.add(aSearch);
                                listAdapter.notifyDataSetChanged();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        }else{
            savedSearches = FileUtil.readSavedSearches(getActivity());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText("No Saved Searches");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Use the chosen saved search to run a job search:
        mListener.onsavedSearchInteraction(savedSearches.get(position));
    }

    private class SavedSearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return savedSearches.size();
        }


        @Override
        public Object getItem(int position) {
            return savedSearches.get(position);
        }


        @Override
        public long getItemId(int position) {
            return ID_CONSTANT + position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.saved_list_item, parent, false);
            }

            // Get TextViews:
            TextView textTitle = (TextView) convertView.findViewById(R.id.textView_saved_title);
            TextView textSubtitle = (TextView) convertView.findViewById(R.id.textView_saved_company);

            // Set text:
            textTitle.setText(savedSearches.get(position).getKeywords());
            String searchString = "Within " + savedSearches.get(position).getRadius() + " miles of " + savedSearches.get(position).getLocation() + ", past " + savedSearches.get(position).getDays() + " days";
            textSubtitle.setText(searchString);

            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.savedJobs_delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Unsave the search from Firebase:
                    Firebase firebase = new Firebase("https://jobspot-a0171.firebaseio.com/");
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        firebase.child("users").child(firebaseUser.getUid()).child("savedsearches").child(savedSearches.get(position).getDateTime()).removeValue();
                    }
                    // Unsave the search from the device:
                    ArrayList<SavedSearch> searchesToRemove = new ArrayList<>();
                    for (SavedSearch savedSearch : savedSearches){
                        if (savedSearch.getDateTime().equals(savedSearches.get(position).getDateTime())){
                            searchesToRemove.add(savedSearch);
                        }
                    }
                    savedSearches.removeAll(searchesToRemove);
                    FileUtil.writeSavedSearch(getActivity(), savedSearches);
                    Toast.makeText(getActivity(), "Search has been removed.", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }


    }

    public interface OnSavedSearchInteractionListener{
        // Send the saved search info back to run the search:
        void onsavedSearchInteraction(SavedSearch savedSearch);
    }

}
