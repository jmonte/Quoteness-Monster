package com.jmonte.seriesquotes.widgets;


public class ExtendedCheckBox implements Comparable<ExtendedCheckBox>
{	   
    private String mText = "";
    private boolean mChecked;
    
    public ExtendedCheckBox(String text, boolean checked) 
    {
    	/* constructor */ 
        mText = text;
        mChecked = checked;
    }
    public void setChecked(boolean value)
    {
    	this.mChecked = value;
    }
    public boolean getChecked()
    {
    	return this.mChecked;
    }
    
    public String getText() {
         return mText;
    }
    
    public void setText(String text) {
         mText = text;
    }

    /** Make CheckBoxifiedText comparable by its name */
    //@Override
    public int compareTo(ExtendedCheckBox other) 
    {
         if(this.mText != null)
              return this.mText.compareTo(other.getText());
         else
              throw new IllegalArgumentException();
    }
}
