package com.example.mekawy.sunrise;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Setting_Activity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{
    //you can use wizard to create setting activity to be compatable with all versions


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bind_Pref_Summary_Value(findPreference(getString(R.string.pref_location_key)));
        bind_Pref_Summary_Value(findPreference(getString(R.string.pref_units_key)));
    }

    public void bind_Pref_Summary_Value(Preference pref){
        pref.setOnPreferenceChangeListener(this);
        onPreferenceChange(pref,
                PreferenceManager.getDefaultSharedPreferences(pref.getContext())
                        .getString(pref.getKey(),"Err"));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        String value=o.toString();
        Log.i("LOGG",value);
        preference.setSummary(value);
        return true;
    }
}
