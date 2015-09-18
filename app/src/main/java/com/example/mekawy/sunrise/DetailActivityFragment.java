package com.example.mekawy.sunrise;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String FORECAST_SHARE_HASHTAG = " #SunriseApp";

    public DetailActivityFragment() {

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
            forecast_tv.setText(text_intent.getStringExtra(Intent.EXTRA_TEXT)+FORECAST_SHARE_HASHTAG);
        }
        return root;
    }
}
