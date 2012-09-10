package com.jmonte.seriesquotes.widgets;

import android.graphics.drawable.Drawable;
import com.google.android.maps.OverlayItem;
import java.util.ArrayList;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CurrentItemizedOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public CurrentItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}
}
