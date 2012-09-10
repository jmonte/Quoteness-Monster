package com.jmonte.seriesquotes;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.BaseDialogListener;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionEvents;

import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;

import com.jmonte.seriesquotes.adapters.DBAdapter;


import android.content.DialogInterface;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.database.Cursor;
import android.view.View.OnClickListener;

import android.app.AlertDialog;

import java.util.ArrayList;

public class QuoteActivity extends Activity implements OnGestureListener{
	
	// the list of quotes that are in the list
	ArrayList<Integer> quoteList = new ArrayList<Integer>();
	
	// the index of the quoteList that is currently in use
	int index = 0;
	
	// VIew controls
	Button next = null;
	Button previous = null;
	TextView quote = null;
	TextView author = null;
	TextView category= null;
	Button addFavorites = null;
	TextView quoteStatus = null;
	Button share = null;
	QuoteActivity quoteActivity = null;
	
	// facebook settings
	private static final String[] PERMISSIONS =
        new String[] {"publish_stream", "read_stream", "offline_access"};
	public static final String APP_ID = "131596186857539";
	private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    boolean fromFavorites = false; // use for inserting the random quotes menu item on menu
    
    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quote);
        gestureScanner = new GestureDetector(this);
        
        // initialize view controls
		previous = (Button) findViewById(R.id.previous);
		next = (Button) findViewById(R.id.next);
		share = (Button) findViewById(R.id.share);
		addFavorites = (Button) findViewById(R.id.favorites);
		quote = (TextView) findViewById(R.id.quote);
		author = (TextView) findViewById(R.id.author);
		category= (TextView) findViewById(R.id.category);
		quoteStatus = (TextView) findViewById(R.id.quoteStatus);
		// set the listener on the views
		controlMethods();

		// gets the quote list passed by FavoriteList
		ArrayList<Integer> tempList = this.getIntent().getIntegerArrayListExtra("quoteList");
		
        DBAdapter db = new DBAdapter(this);
        db.open();
        
		if(tempList != null) {
			quoteList = tempList;
			fromFavorites = true;
		} else {
			quoteList = db.getRandomQuotesId();
		}
		
		if(this.getIntent().getIntExtra("widgetid", 0) > 0) {
			
			quoteList.set(0,this.getIntent().getIntExtra("widgetid", 0) );
		}
		// listener for the index from the FavoriteList
		index = this.getIntent().getIntExtra("index", 0);
		
		
		Cursor c = db.getQuote(quoteList.get(index));
		if(c.moveToFirst()) {
			do {
				quote.setText(c.getString(1));
				author.setText(c.getString(2));
				category.setText(c.getString(3));
				// taga set ng favorites text pagka nakalagay sa favorites
				if(c.getInt(4) == 1) {
					addFavorites.setText("Remove On Favorites");
				} else {
					addFavorites.setText("Add To Favorites");
				}
			} while(c.moveToNext());
		}
		
		quoteStatus.setText(Html.fromHtml("<u>"+(index+1)+"</u><br/>"+quoteList.size()));
		
		c.close();
		db.close();
		
		// initialize facebook
		 mFacebook = new Facebook();
		 mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		 
		//for the next and previous buttons
		refreshButtons();
		quoteActivity = this;
    }
    
    public void controlMethods() {
		addFavorites.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				favorites();
			}
		});
		
		next.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(index < quoteList.size() ) {
					newQuote(true);
				}
			}
		});
				
		previous.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(index > 0 ) {
					newQuote(false);
				}
			}
		});

		share.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				share();
			}
		});
    }
    
    public void postFacebook() {
    	try {
	    	if(!mFacebook.isSessionValid()) {
				mFacebook.authorize( share.getContext(), APP_ID, PERMISSIONS,
	                    new LoginDialogListener());
			} else {
	            mFacebook.dialog(QuoteActivity.this, "stream.publish", 
	                    new SampleDialogListener(),quote.getText().toString());
			}
    	} catch (Exception e) {
    		
    	}
    }
    
    public void sendSMS() {
    	Intent sendIntent = new Intent(Intent.ACTION_VIEW);
    	sendIntent.putExtra("sms_body", quote.getText().toString()); 
    	sendIntent.setType("vnd.android-dir/mms-sms");
    	startActivity(sendIntent);   
    }
    
    public void sendEmail() {
    	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent .setType("plain/text");
    	emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"webmaster@website.com"});
    	emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "Quoteness Monster: " + category.getText());
    	emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, quote.getText());
    	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
    
    public void share() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select A Method");
		final String[] items = new String[] {"Wall on Facebook" , "Text Message","Email It"};
		builder.setIcon(R.drawable.share);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch (item) {
		    		case 0:
		    			postFacebook();
		    			break;
		    		case 1:
		    			sendSMS();
		    			break;
		    		case 2:
		    			sendEmail();
		    			break;
		    	} 
		        
		    }
		});
		AlertDialog alert = builder.create();
		
		alert.show();
    }
    
    public void favorites() {
        DBAdapter db = new DBAdapter(this);
        db.open();
		if(db.isInFavorites(quoteList.get(index))) {
			db.deleteFavorite(quoteList.get(index));
			Toast.makeText(QuoteActivity.this, "Quote removed to your favorites", Toast.LENGTH_SHORT).show();
			addFavorites.setText("Add To Favorites");
		}else {
			db.insertFavorite(quoteList.get(index));
			Toast.makeText(QuoteActivity.this, "Quote Added to your favorites", Toast.LENGTH_SHORT).show();
			addFavorites.setText("Remove On Favorites");
		}
		db.close();
    }
    
    public void refreshButtons() {
    	if(index == 0 ) {
			previous.setEnabled(false);
		} else {
			previous.setEnabled(true);
		}
		if(index == quoteList.size() - 1 ) {
			next.setEnabled(false);
		} else {
			next.setEnabled(true);			
		}
		quoteStatus.setText(Html.fromHtml("<u>"+(index+1)+"</u><br/>"+quoteList.size()));
    }

    public void newQuote(boolean isNext) {
    	if(isNext) {
    		index = index+1;
    	} else {
    		index = index-1;
    	}
        DBAdapter db = new DBAdapter(this);
        db.open();
		Cursor c = db.getQuote(quoteList.get(index));
		if(c.moveToFirst()) {
			do {
				quote.setText(c.getString(1));
				author.setText(c.getString(2));
				category.setText(c.getString(3));
				// taga set ng favorites text pagka nakalagay sa favorites
				if(c.getInt(4) == 1) {
					addFavorites.setText("Remove On Favorites");
				} else {
					addFavorites.setText("Add To Favorites");
				}
			} while(c.moveToNext());
		}
		c.close();
		db.close();
		refreshButtons();
    }
    
    final int CATEGORIES_MENU = 0;
    final int FAVORITES_MENU = 1;
    final int SETTINGS_MENU = 2;
    final int RANDOM_MENU = 3;
    public boolean onCreateOptionsMenu(Menu menu) {
    	if(!fromFavorites) {
	        menu.add(0, CATEGORIES_MENU, 0, "Categories").setIcon(R.drawable.category_menu);
	        menu.add(0, FAVORITES_MENU, 0, "Favorites").setIcon(R.drawable.love);
    	}
        menu.add(0, SETTINGS_MENU, 0, "Settings").setIcon(R.drawable.equalizer);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent  data) {
    	if(requestCode == CATEGORY_ACTIVITY ) {
    		controlMethods();
    		ArrayList<Integer> tempList = this.getIntent().getIntegerArrayListExtra("quoteList");
            DBAdapter db = new DBAdapter(this);
            db.open();
    		if(tempList != null) {
    			quoteList = tempList;
    			fromFavorites = true;
    		} else {
    			quoteList = db.getRandomQuotesId();
    		}
    		
    		// listener for the index from the FavoriteList
    		index = this.getIntent().getIntExtra("index", 0);
    		Cursor c = db.getQuote(quoteList.get(index));
    		if(c.moveToFirst()) {
    			do {
    				quote.setText(c.getString(1));
    				author.setText(c.getString(2));
    				category.setText(c.getString(3));
    				// taga set ng favorites text pagka nakalagay sa favorites
    				if(c.getInt(4) == 1) {
    					addFavorites.setText("Remove On Favorites");
    				} else {
    					addFavorites.setText("Add To Favorites");
    				}
    			} while(c.moveToNext());
    		}
    		
    		quoteStatus.setText(Html.fromHtml("<u>"+(index+1)+"</u><br/>"+quoteList.size()));
    		
    		c.close();
    		db.close();
    		
    		// initialize facebook
    		 mFacebook = new Facebook();
    		 mAsyncRunner = new AsyncFacebookRunner(mFacebook);
    		 
    		//for the next and previous buttons
    		refreshButtons();
    	} else if (requestCode == FAVORITE_ACTIVITY) {
    	       DBAdapter db = new DBAdapter(this);
    	        db.open();
    			Cursor c = db.getQuote(quoteList.get(index));
    			if(c.moveToFirst()) {
    				do {
    					if(c.getInt(4) == 1) {
    						addFavorites.setText("Remove On Favorites");
    					} else {
    						addFavorites.setText("Add To Favorites");
    					}
    				} while(c.moveToNext());
    			}
    			c.close();
    			db.close();
    	}
    }
    
    
    final int CATEGORY_ACTIVITY = 0;
    final int FAVORITE_ACTIVITY = 1;
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = null;
    	switch(item.getItemId()) {
    		case CATEGORIES_MENU:
    			intent = new Intent(QuoteActivity.this,CategoryList.class);
    			startActivityForResult(intent,CATEGORY_ACTIVITY );
    			break;
			case FAVORITES_MENU:
				DBAdapter db = new DBAdapter(this);
		        db.open();
				Cursor c = db.getAllFavorites("");
				if(c.getCount() == 0 ) {
					Toast.makeText(QuoteActivity.this, "You have not selected any favorites. Maybe you can add the one on top", Toast.LENGTH_LONG).show();
				} else {
					/*
					intent = new Intent(QuoteActivity.this,FavoriteList.class);
					startActivityForResult(intent,FAVORITE_ACTIVITY );
					*/
				}
				break;
    		case SETTINGS_MENU:
    			/*
				intent = new Intent(QuoteActivity.this,SettingActivity.class);
				startActivity(intent);
    			break;
    			*/
    		/*
    		case RANDOM_MENU:
    			this.finish();
    			intent = new Intent(QuoteActivity.this,QuoteActivity.class);
    			startActivity(intent);
    			break;
    		*/
    	}
        return false;
    }
    
    private GestureDetector gestureScanner;
    
    @Override
    public boolean onTouchEvent(MotionEvent me) {
    	return gestureScanner.onTouchEvent(me); 
    }
    
    @Override
    public boolean onDown(MotionEvent e) {
    	return true;
    }
    
    @Override
    public boolean onFling(MotionEvent e1,MotionEvent e2,float velocityX, float velocityY) {
    	
    	try {
    		if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
    			if(index < quoteList.size() ) {
					newQuote(true);
				}
    		} else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(index > 0 ) {
					newQuote(false);
				}
    		}
    		
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	return true;
    }
    @Override
    public void onLongPress(MotionEvent e) {
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    	return true;
    }
    
    @Override
    public void onShowPress(MotionEvent e) {
    } 
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
	    return true;
    }
    

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		super.dispatchTouchEvent(ev);
		return gestureScanner.onTouchEvent(ev);
	}

    
    public class SampleAuthListener implements AuthListener {
        
        public void onAuthSucceed() {
        	System.out.println("Yahooo");
        }

        public void onAuthFail(String error) {
        	System.out.println("Google!!");
        }
    }
    
    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
        }
        
        public void onLogoutFinish() {
        }
    }

    
    
    public class WallPostRequestListener extends BaseRequestListener {
        
        public void onComplete(final String response) {
            QuoteActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                	Toast.makeText(QuoteActivity.this, "Quote Posted to Facebook!!", Toast.LENGTH_SHORT).show();
                }
            });
            
        }
    }

    public class SampleDialogListener extends BaseDialogListener {
        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
                mAsyncRunner.request(postId, new WallPostRequestListener());
            } else {
                Log.d("Facebook-Example", "No wall post made");
            }
        }
    }
    
    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
            //mFacebook.dialog(QuoteActivity.this, "stream.publish", 
                    //new SampleDialogListener(),quote.getText().toString());
            mFacebook.dialog(QuoteActivity.this, "stream.publish", 
                    new SampleDialogListener(),quote.getText().toString());
        }

        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }
        
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }
}