package com.jmonte.seriesquotes;

import android.app.TabActivity;
import android.os.Bundle;
import android.content.res.Resources;
import android.widget.TabHost;
import android.content.Intent;

public class MainActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, QuoteActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("artists").setIndicator("Quotes",
                          null)
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, CategoryList.class);
        spec = tabHost.newTabSpec("albums").setIndicator("Categories",
                          null)
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(2);

    }
}