package com.example.mekawy.sunrise;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;


import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String FORECAST_SHARE_HASHTAG = " #SunriseApp";
    private String Sharable_Forecast;
    private String LOG=DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
    setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment,menu);
        MenuItem mItem=menu.findItem(R.id.share);
         ShareActionProvider mShare
                =(ShareActionProvider) MenuItemCompat.getActionProvider(mItem);
        if(mShare!=null)mShare.setShareIntent(createShareForecastIntent());
        else Log.i(LOG,"NO ACTION.SEND Founded");
    }

    private Intent createShareForecastIntent(){
        Intent share_intent=new Intent(Intent.ACTION_SEND);
        share_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_TEXT,Sharable_Forecast);
        return share_intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_detail, container, false);
        Intent text_intent=getActivity().getIntent();
        if(text_intent!=null && text_intent.hasExtra(Intent.EXTRA_TEXT)){
            TextView forecast_tv=(TextView) root.findViewById(R.id.detail_text);
            Sharable_Forecast=text_intent.getStringExtra(Intent.EXTRA_TEXT)+FORECAST_SHARE_HASHTAG;
            forecast_tv.setText(Sharable_Forecast);
        }
        return root;
    }
}
