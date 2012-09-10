package com.jmonte.seriesquotes.widgets;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ExtendedCheckBoxListAdapter extends BaseAdapter {

	/** Remember our context so we can use it when constructing views. */
	private Context mContext;

	private List<ExtendedCheckBox> mItems = new ArrayList<ExtendedCheckBox>();

	/**
	 * 
	 * @param context - Render context
	 */
	public ExtendedCheckBoxListAdapter(Context context) {
		mContext = context;     
		
	}

	/**
	 * Add a new Item at the end of the exiting ones
	 * @param it - New item to be added
	 */
	public void addItem(ExtendedCheckBox it) { 
    	mItems.add(it); 
    }

	/**
	 * Will force to use a list of items
	 * @param lit - List of items to be used
	 */
	public void setListItems(List<ExtendedCheckBox> lit) { 
    	mItems = lit; 
    }

    /** 
     * @return The number of items this adapter offers 
     */
    public int getCount() { 
    	return mItems.size(); 
    }

    /**
     * Return item at a specific position
     */
    public Object getItem(int position) { 
    	return mItems.get(position); 
    }
    
    /**
     * Returns the position of an element
     */
    public int GetPosition( ExtendedCheckBox item ) {
    	int count = getCount();
    	for ( int i = 0; i < count; i++ )
    	{
    		if ( item.compareTo((ExtendedCheckBox)getItem(i)) == 0 )
    			return i;
    	}
    	return -1;
    }

    /**
     * Set selection of an item
     * @param value - true or false
     * @param position - position
     */
    public void setChecked(boolean value, int position) {
        mItems.get(position).setChecked(value);
    }

    /**
     * Select all elements
     */
    public void selectAll() {
        for(ExtendedCheckBox cboxtxt: mItems)
             cboxtxt.setChecked(true);
        
        /* Things have changed, do a redraw. */
        this.notifyDataSetInvalidated();
    }
    
    /**
     * Deselect all elements
     */
    public void deselectAll() {
        for(ExtendedCheckBox cboxtxt: mItems)
            cboxtxt.setChecked(false);
        
       /* Things have changed, do a redraw. */
       this.notifyDataSetInvalidated();
    }

    /**
     * Decides if all items are selectable
     * @return - true or false
     */
    public boolean areAllItemsSelectable() { 
    	return false; 
    }

    /** 
     * Use the array index as a unique id
     */
    public long getItemId(int position) {
         return position;
    }

    /**
     * Do not recycle a view if one is already there, if not the data could get corrupted and
     * the checkbox state could be lost. 
     * @param convertView The old view to overwrite
     * @returns a CheckBoxifiedTextView that holds wraps around an CheckBoxifiedText */
    public View getView(int position, View convertView, ViewGroup parent ){
    	return new ExtendedCheckBoxListView(mContext, mItems.get(position));
    }
}
