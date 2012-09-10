package com.jmonte.seriesquotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;
import android.location.Location;

import android.location.LocationListener;

import android.location.LocationManager;
public class AlarmReceiver extends BroadcastReceiver {

	 private Context context;
	 
	 @Override
	 public void onReceive(Context context, Intent intent) {
	   try {
		 this.context = context;
	     Bundle bundle = intent.getExtras();
	     String message = bundle.getString("alarm_message");
	     
	     Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	     
	     LocationManager mlocManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	     LocationListener mlocListener = new MyLocationListener();
	
	     mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

	    } catch (Exception e) {
	     Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
	     e.printStackTrace();
	
	    }
	 }

	 public class MyLocationListener implements LocationListener {
	
		 @Override
		 public void onLocationChanged(Location loc) {
			 loc.getLatitude();
			 loc.getLongitude();
			 String Text = "My current location is: " +"Latitud = " + loc.getLatitude() +"Longitud = " + loc.getLongitude();
			 Toast.makeText( context,Text,Toast.LENGTH_SHORT).show();
		 }
	
		 @Override
		 public void onProviderDisabled(String provider) {
			 Toast.makeText( context,"Gps Disabled",Toast.LENGTH_SHORT ).show();
		 }
	
		 @Override
		 public void onProviderEnabled(String provider) {
			 Toast.makeText( context, "Gps Enabled", Toast.LENGTH_SHORT).show();
		 }
		 
		 @Override
		 public void onStatusChanged(String provider, int status, Bundle extras) {
			 Toast.makeText( context, "Status Listener", Toast.LENGTH_SHORT).show();
		 }
	
	 }
 
}
