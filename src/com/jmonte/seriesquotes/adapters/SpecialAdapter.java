package com.jmonte.seriesquotes.adapters;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class SpecialAdapter extends SimpleAdapter {
		
	public static int iterate = 1;
	
	public SpecialAdapter(Context context, List<Map<String,String>> items, int resource, String[] from, int[] to) {
		super(context, items, resource, from, to);
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  View view = super.getView(position, convertView, parent);
      if(iterate == 1) {
     	 view.setBackgroundColor(Color.WHITE);
     	 iterate = 2;
      } else {
     	 view.setBackgroundColor(Color.parseColor("#EFEFEF"));
     	 iterate = 1;
      }

	  return view;
	}
	
}
