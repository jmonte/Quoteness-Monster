package com.jmonte.seriesquotes.widgets;

import com.jmonte.seriesquotes.adapters.DBAdapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.Toast;

public class ExtendedCheckBoxListView extends LinearLayout {
    
    private TextView mText;
    private CheckBox mCheckBox;
    private ExtendedCheckBox mCheckBoxText;
    private Context context;
    
    public static int iterate = 1;
    
    public ExtendedCheckBoxListView(Context context, ExtendedCheckBox aCheckBoxifiedText) {
         super(context);
         if(iterate == 1) {
        	 this.setBackgroundColor(Color.WHITE);
        	 iterate = 2;
         } else {
        	 this.setBackgroundColor(Color.parseColor("#EFEFEF"));
        	 iterate = 1;
         }
         this.setOrientation(HORIZONTAL);
         
         this.setPadding(5, 5, 5,5);
         this.context = context;
         mCheckBoxText = aCheckBoxifiedText;
         mCheckBox = new CheckBox(context);
         mCheckBox.setPadding(0, 0, 20, 0);
         mCheckBox.setChecked(aCheckBoxifiedText.getChecked());
         
         // Set the right listener for the checkbox, used to update
         // our data holder to change it's state after a click too
         mCheckBox.setOnClickListener( new OnClickListener()
         {
         	/**
         	 *  When clicked change the state of the 'mCheckBoxText' too!
         	 */
			@Override
			public void onClick(View v) {
				toggleCheckBoxState();
			}
         });         
         
         // Add the checkbox
         addView(mCheckBox,  new LinearLayout.LayoutParams(
                   LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
         
         mText = new TextView(context);
         mText.setText(aCheckBoxifiedText.getText());
         mText.setTextColor(Color.parseColor("#003366"));
         mText.setTextSize(25);
         addView(mText, new LinearLayout.LayoutParams(
                   LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
         
         // Remove some controls in order to prevent a strange flickering when clicking on the TextView!
         mText.setClickable(false);
         mText.setFocusable(false);
         mText.setFocusableInTouchMode(false);
         mText.setOnClickListener( new OnClickListener()
         {
 			@Override
 			public void onClick(View v) {
 				toggleCheckBoxState2();
 			}

          });        
         setOnClickListener( new OnClickListener()
         {
			@Override
			public void onClick(View v) {
				toggleCheckBoxState2();
			}

         });        
         
    }
    
    public void setText(String words) {
         mText.setText(words);
    }
    
    public void toggleCheckBoxState()
    {
    	setCheckBoxState(getCheckBoxState());
    }
    
    public void toggleCheckBoxState2()
    {
    	setCheckBoxState(!getCheckBoxState());
    }
    
    public void setCheckBoxState(boolean bool)
    {
    	boolean willUpdate = true;
    	
    	DBAdapter db = new DBAdapter(this.context);
    	db.open();
    	if(bool) {
    		db.insertMyCategory(mText.getText()+"");
    	} else {
    		if(db.getMyCategories().size() != 1) {
    			db.deleteMyCategory(mText.getText()+"");
    		} else {
    			Toast s = Toast.makeText(this.context,"At least 1 category should be active", Toast.LENGTH_LONG);
    			s.show();
    			willUpdate = false;

    		}
    	}
    	db.close();
    	if(willUpdate) {
    		System.out.println("Toogle Last");
	    	mCheckBox.setChecked(bool);
	    	mCheckBoxText.setChecked(bool);
    	} else {
    		mCheckBox.setChecked(true);
    	}
    	
    }
    
    public boolean getCheckBoxState()
    {
    	return mCheckBox.isChecked();
    }
}
