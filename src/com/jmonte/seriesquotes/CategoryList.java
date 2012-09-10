package com.jmonte.seriesquotes;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jmonte.seriesquotes.adapters.DBAdapter;
import com.jmonte.seriesquotes.widgets.ExtendedCheckBox;
import com.jmonte.seriesquotes.widgets.ExtendedCheckBoxListAdapter;



public class CategoryList extends ListActivity {
	

	
	ArrayList<Integer> categoryList = new ArrayList<Integer>();
	ArrayList<Integer> myCategories = new ArrayList<Integer>();
	List<Map<String,String>> list = null;
	ArrayList<Boolean> categoryMyList = new ArrayList<Boolean>();

	private ExtendedCheckBoxListAdapter mListAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateList(DBAdapter.KEY_NAME);
        this.setContentView(R.layout.categories);
    }
    
    public void populateList(String orderBy) {
    	list = new ArrayList<Map<String,String>>();
    	categoryMyList = new ArrayList<Boolean>();
		DBAdapter db = new DBAdapter(this);
        db.open();
		Cursor c = db.getAllCategories(orderBy);
		 mListAdapter = new ExtendedCheckBoxListAdapter(this);
		myCategories = db.getMyCategories();
		if (c.moveToFirst()) { 
	         do {
	        	Map<String, String> map = new HashMap<String,String>();
     			map.put("categoryItem",c.getString(1) );
     			if(myCategories.contains(c.getInt(0))) {
     				mListAdapter.addItem( new ExtendedCheckBox(c.getString(1),true));
     			} else {
     				mListAdapter.addItem( new ExtendedCheckBox(c.getString(1),false));
     			}
     			list.add(map);
	         } while (c.moveToNext());
	        }
        setListAdapter(mListAdapter);
        db.close();
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
    	Toast.makeText(getApplicationContext(),"Test",Toast.LENGTH_LONG);
    	return;
	}
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.categories,menu);
        return true;
    }
    
    public void sortName() {
    	populateList(DBAdapter.KEY_NAME);
    }
    
    public void sortChecked() {
    	populateList(DBAdapter.KEY_MY_CATEGORY+" DESC");
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = null;
    	switch(item.getItemId()) {
    		case R.id.sortMenu:
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("Sort By");
    			builder.setIcon(R.drawable.options);
    			final String[] items = new String[] {"Name" , "Checked"};
    			builder.setItems(items, new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog, int item) {
    			    	switch (item) {
    			    		case 0:
    			    			sortName();
    			    			break;
    			    		case 1:
    			    			sortChecked();
    			    			break;
    			    	} 
    			        
    			    }
    			});
    			AlertDialog alert = builder.create();
    			alert.show();
    			break;
    		case R.id.moodMenu:
    			AlertDialog.Builder mood = new AlertDialog.Builder(this);
    			mood.setTitle("Please:");
    			mood.setIcon(R.drawable.happy);
    			final String[] moodItems = new String[] {
    										"Inspire me!" , 
    										"Make me Laugh!",
    										"Make me smarter",
    										"Calm me, Im Angry"};
    			mood.setItems(moodItems, new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog, int item) {
    			    	moodRegulate(item);
    			    }
    			});
    			AlertDialog moodAlert = mood.create();
    			moodAlert.show();
    			break;
			case R.id.settingsMenu:
				
				break;
    	}
        return false;
    }
    
    public void moodRegulate(int item) {
    	String[] moodName = null;
    	switch (item) {
    		case 0:
    			moodName = new String[]{
    					"Age",
    					"Business",
    					"Art"
    			};
    			break;
    		case 1:
    			moodName = new String[] {
    					"Pick Up Lines"
    			};
    			break;
    		case 2:
    			moodName = new String[] {
    					"Points to Ponder",
    					"Strange Facts"
    			};
    			break;
    		case 3:
    			moodName = new String[] {
    					"Anger",
    			};
    			break;
    	} 
    	DBAdapter db = new DBAdapter(this.getApplicationContext());
    	db.open();
    	db.insertMyCategories(moodName);
    	this.finish();
    	//populateList(DBAdapter.KEY_NAME);
    	
    }
    
}

