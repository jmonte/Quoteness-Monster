package com.jmonte.seriesquotes.web;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jmonte.seriesquotes.adapters.DBAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;

public class UpdateViaWeb extends Thread {
	Handler mHandler;
	final static int STATE_DONE =0;
	final static int STATE_RUNNING = 1;
	int mState;
	int total;
	int current;
	public Activity act = null;
	
	int quote_id = 0;
	int category_id = 0;
	int author_id = 0;
	
	int final_quote = 0;
	int final_category = 0;
	int final_author = 0;
	int jsonLength = 0;
	
	boolean isConnected = true;
	
	UpdateViaWeb(Handler h,int quote_id,int category_id,int author_id) {
		mHandler =h; 
		this.quote_id = quote_id;
		this.category_id = category_id;
		this.author_id = author_id;
		mState = STATE_RUNNING;
	}
	
	
	private void getQuotes() {
		JSONArray jsonQuotes = null;
		JSONArray jsonCategories = null;
		JSONArray jsonAuthors = null;
		
		int superTotal = 0;
		
		try {

			  JSONObject jsonObjSend = new JSONObject();

			  try {
				   jsonObjSend.put("key_1", "value_1");
				   jsonObjSend.put("key_2", "value_2");
				   JSONObject header = new JSONObject();
				   header.put("deviceType","Android"); // Device type
				   header.put("deviceVersion","2.0"); // Device OS version
				   header.put("language", "es-es"); // Language of the Android client
				   jsonObjSend.put("header", header);
			  } catch (JSONException e) {
				  e.printStackTrace();
			  }
			  
			  // code that connects to the server
			  JSONObject jsonObjRecv = HttpClient.SendHttpPost("http://qmonster.digichubs.com/json.php?id="+quote_id, jsonObjSend);
			  try {
				  jsonQuotes = jsonObjRecv.getJSONArray("quotes");
			  } catch (JSONException e) {
				  e.printStackTrace();
			  }
			  
			  jsonObjRecv = HttpClient.SendHttpPost("http://qmonster.digichubs.com/jsonAuthors.php?id="+author_id, jsonObjSend);

			  try {
				  jsonAuthors = jsonObjRecv.getJSONArray("authors");
			  } catch (JSONException e) {
				  e.printStackTrace();
			  }
			 jsonObjRecv = HttpClient.SendHttpPost("http://qmonster.digichubs.com/jsonCategories.php?id="+category_id, jsonObjSend);
			  try {
				  jsonCategories = jsonObjRecv.getJSONArray("categories");
				  } catch (JSONException e) {
					  e.printStackTrace();
				  }
			  
			  
		} catch (Exception e) {
			Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("error", true);
            b.putString("errorMessage", "Unable to Connect to Server");
            msg.setData(b);
            mHandler.sendMessage(msg);
            isConnected = false;
		}
		try {
			superTotal += jsonQuotes.length() + jsonAuthors.length() +jsonCategories.length() - 3;
		} catch (Exception e) {
			
		}
		if(mState == STATE_RUNNING && isConnected ) {
			try {
				jsonLength = jsonQuotes.length();		
			} catch (Exception e) {
				e.printStackTrace();
			}
			current = 0;
			DBAdapter db = null;
	    	if(jsonLength  > 0 ) {
		        try {
		        	db = new DBAdapter(act);
		        	db.open();
					  for(int i = 0; i < jsonLength;i++) {
						  try {
							  db.insertQuote(Integer.parseInt(jsonQuotes.getJSONObject(i).getString("id")), 
									  jsonQuotes.getJSONObject(i).getString("quote"), Integer.parseInt(jsonQuotes.getJSONObject(i).getString("author_id")),
										 Integer.parseInt(jsonQuotes.getJSONObject(i).getString("category_id")));
				              Message msg = mHandler.obtainMessage();
				              Bundle b = new Bundle();
				              b.putInt("superTotal",superTotal);
				              b.putInt("current", current);
				              final_quote = Integer.parseInt(jsonQuotes.getJSONObject(i).getString("id"));
				              msg.setData(b);
				              mHandler.sendMessage(msg);
				              current++;
						  } catch (Exception e) {
							  e.printStackTrace();
						  }
					  }
		        } catch(Exception e) {
		        	       
		        } finally {
		        	db.close();
		        }
	    	} else {
	    		  Message msg = mHandler.obtainMessage();
	              Bundle b = new Bundle();
	              b.putBoolean("empty",true);
	              b.putString("type","Quotes");
	              msg.setData(b);
	              mHandler.sendMessage(msg);
			}
	    	
	    	try {
				jsonLength = jsonAuthors.length();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db = null;
		    if(jsonLength  > 0 ) {
		    	try {
			       	db = new DBAdapter(act);
			       	db.open();
					int i = 0;
					for(; i < jsonLength;i++) {
						  try {
							  db.insertAuthor(Integer.parseInt(jsonAuthors.getJSONObject(i).getString("id")), 
									  jsonAuthors.getJSONObject(i).getString("name"));
				              Message msg = mHandler.obtainMessage();
				              Bundle b = new Bundle();
				              b.putInt("superTotal",superTotal);
				              b.putInt("current", current);
				              final_author = Integer.parseInt(jsonAuthors.getJSONObject(i).getString("id"));
				              msg.setData(b);
				              mHandler.sendMessage(msg);
				              current++;
						  } catch (Exception e) {
						  }
					  }			
				}catch(Exception e) {
				} finally {
					db.close();
				}
			} else {
				Message msg = mHandler.obtainMessage();
	              Bundle b = new Bundle();
	              b.putBoolean("empty",true);
	              b.putString("type","Authors");
	              msg.setData(b);
	              mHandler.sendMessage(msg);
			}
		    
		    try {
				jsonLength = jsonCategories.length();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db = null;
	    	if(jsonLength  > 0 ) {
		        try {
		        	db = new DBAdapter(act);
		        	db.open();
				int i = 0;
				for(; i < jsonLength;i++) {
					  try {
						  db.insertCategory(Integer.parseInt(jsonCategories.getJSONObject(i).getString("id")), 
								  jsonCategories.getJSONObject(i).getString("name"));
			              Message msg = mHandler.obtainMessage();
			              Bundle b = new Bundle();
			              b.putInt("current", current);
			              final_category = Integer.parseInt(jsonCategories.getJSONObject(i).getString("id"));
			              b.putInt("final_quote",final_quote);
			              b.putInt("final_author",final_author);
			              b.putInt("final_category",final_category);
			              b.putInt("quote_total", jsonQuotes.length());
			              b.putInt("author_total", jsonAuthors.length());
			              b.putInt("category_total", jsonCategories.length());
			              b.putInt("superTotal",superTotal);
			              msg.setData(b);
			              mHandler.sendMessage(msg);
			              current++;
					  } catch (Exception e) {
					  }
				}			
			} catch(Exception e) {
				
			} finally {
				db.close();
			}
		} else {
			Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("empty",true);
            b.putString("type","Categories");
            msg.setData(b);
            mHandler.sendMessage(msg);
		}
    
	    	
		}
	}
	
	public void run() {
		getQuotes();
	}
	
	public void setState(int state) {
		mState = state;
	}
}
