package com.jmonte.seriesquotes;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MyLocationOverlay;
import android.graphics.drawable.Drawable;

import java.util.List;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;

import android.view.WindowManager;

import com.jmonte.seriesquotes.widgets.CurrentItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MapController;
import android.content.DialogInterface;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.TimePicker;

import android.view.View.OnClickListener;
import android.view.View;

import android.os.Message;
import android.os.Handler;
import java.util.Calendar;

import android.content.Intent;
import android.app.PendingIntent;
import android.app.AlarmManager;

public class AlarmActivity extends MapActivity {
	
	List<Overlay> mapOverlays;
	Drawable drawable;
	CurrentItemizedOverlay itemizedOverlay;
	MapView mapView;
	MapController mc;
	private double lat, lon;

	private int mHour;
    private int mMinute;
    private Button mPickTime;
    private Button mCategory;
    private Button mCreateAlarm;
    static final int TIME_DIALOG_ID = 0;
	static final int CATEGORY_DIALOG_ID = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.blue);
		itemizedOverlay = new CurrentItemizedOverlay(drawable);
		mc = mapView.getController();       
        mc.setZoom(16);
		mapView.setBuiltInZoomControls(true);  
        mapView.setClickable(true);
        
        GeoPoint point = mapView.getMapCenter();
        lat = point.getLatitudeE6()/1000000;
        lon = point.getLongitudeE6()/1000000;
        
        OverlayItem overlayitem = new OverlayItem(point,"", "");
        itemizedOverlay.addOverlay(overlayitem);        
        
        //mapOverlays = mapView.getOverlays();
        mapOverlays.add(itemizedOverlay);
       
        mPickTime = (Button) findViewById(R.id.changeTime);
        // add a click listener to the button
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        updateDisplay();
        
        mCategory = (Button) findViewById(R.id.addCategory);
        // add a click listener to the button
        mCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(CATEGORY_DIALOG_ID);
            }
        });
        
        mCreateAlarm = (Button) findViewById(R.id.createAlarm);
        // add a click listener to the button
        mCreateAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	addAlarm();
            }
        });
        
	}

	public void addAlarm() {
    	Calendar cal = Calendar.getInstance();
	   	 // add 5 minutes to the calendar object
	   	 cal.add(Calendar.SECOND, 10);
	   	 Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
	   	 intent.putExtra("alarm_message", "O'Doyle Rules!");
	   	 
	   	 // In reality, you would want to have a static variable for the request code instead of 192837
	   	 PendingIntent sender = PendingIntent.getBroadcast(this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	
	   	 // Get the AlarmManager service
	   	 AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
	   	 am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

	}
	
	
	protected CharSequence[] _options = { "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune" };
	protected boolean[] _selections =  new boolean[ _options.length ];

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case TIME_DIALOG_ID:
	        return new TimePickerDialog(this,
	                mTimeSetListener, mHour, mMinute, false);
	    case CATEGORY_DIALOG_ID:
	    	return new AlertDialog.Builder( this )
 	       		.setTitle( "Categories" )
 	       		.setMultiChoiceItems( _options, _selections, new DialogSelectionClickHandler() )
 	       		.setPositiveButton( "OK", new DialogButtonClickHandler() )
 	       			.create();

	    }
	    return null;
	}

	public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
	{
		public void onClick( DialogInterface dialog, int clicked, boolean selected )
		{
			_selections[clicked] = selected; 
			System.out.println ( _options[ clicked ] + " selected: " + selected );
		}
	}
	
	public class DialogButtonClickHandler implements DialogInterface.OnClickListener
	{
		public void onClick( DialogInterface dialog, int clicked ) {
			switch( clicked ) {
				case DialogInterface.BUTTON_POSITIVE:
					updateDisplayCategory();
					break;
			}
		}
	}
	
	protected void updateDisplayCategory() {
		int numChecked = 0;
		for( int i = 0; i < _options.length; i++ ){
			if(_selections[i]) {
				numChecked++;
			}
		}
		if(numChecked == 0 ) {
			mCategory.setText("Add Categories");
		} else if (numChecked == 1) {
			mCategory.setText(numChecked + " Category");
		} else {
			mCategory.setText(numChecked + " Categories");
		}
	}
	
	protected void printSelectedPlanets()
	{
	        for( int i = 0; i < _options.length; i++ ){
		          System.out.println( _options[ i ] + " selected: " + _selections[i] );
		}
	}

	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	            mHour = hourOfDay;
	            mMinute = minute;
	            updateDisplay();
	        }
	    };	

    private void updateDisplay() {
        mPickTime.setText(
            new StringBuilder()
                    .append(pad(mHour)).append(":")
                    .append(pad(mMinute))+ "( Alarm )");
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
	    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
