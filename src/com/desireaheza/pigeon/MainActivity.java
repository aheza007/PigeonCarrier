package com.desireaheza.pigeon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public  class MainActivity extends FragmentActivity implements ActionBar.TabListener{

	public static final String TAG=MainActivity.class.getSimpleName();
	public static final int TAKE_PICTURE_REQUEST=0;
	public static final int CHOOSE_PICTURE_REQUEST=1;
	public static final int TAKE_VIDEO_REQUEST=2;
	public static final int CHOOSE_VIDEO_REQUEST=3;
	public static final int MEDIA_TYPE_IMAGE=4;
	public static final int MEDIA_TYPE_VIDEO=5;
	public static final int MAX_VIDEO_SIZE=1024*1024*10;
	protected Uri mMediaUri;
	
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	protected DialogInterface.OnClickListener mDialogListener= new 
			DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case 0://take a picture
					/*
					 * Standard Intent action that can be sent to have the camera application capture an image and return it. 
					 * The caller may pass an extra EXTRA_OUTPUT to control where this image will be written.
					 */
					Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mMediaUri=getOutPutMediaFile(MEDIA_TYPE_IMAGE);
					//String whereIsMyFile=mMediaUri.toString();
					//System.out.println(whereIsMyFile);
					if(mMediaUri==null){
						//there is a error
						
						Toast.makeText(MainActivity.this, "there was error when trying to save picture", 
								Toast.LENGTH_LONG).show();
					}
					else{
						//The name of the Intent-extra used to indicate a content resolver Uri to be used to store the
						//requested image or video.
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
						startActivityForResult(takePictureIntent,TAKE_PICTURE_REQUEST);
					}
					break;
				case 1://choose picture
					/*
					 * Allow the user to select a particular kind of data and return it
					 */
					Intent choosePictureIntent=new Intent(Intent.ACTION_GET_CONTENT);
					choosePictureIntent.setType("image/*");
					startActivityForResult(choosePictureIntent, CHOOSE_PICTURE_REQUEST);
					
					break;
				case 2://take video
					Intent videoRecoderIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					mMediaUri = getOutPutMediaFile(MEDIA_TYPE_VIDEO);
					if(mMediaUri==null){
							//there is a error
							Toast.makeText(MainActivity.this, R.string.error_external_storage, 
									Toast.LENGTH_LONG).show();
						}
						else{
							videoRecoderIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
							videoRecoderIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
							videoRecoderIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
							startActivityForResult(videoRecoderIntent, TAKE_VIDEO_REQUEST);
						}
					 break;
				case 3://choose video
					/*
					 * Allow the user to select a particular kind of data and return it
					 */
					Intent chooseVideoIntent=new Intent(Intent.ACTION_GET_CONTENT);
					chooseVideoIntent.setType("Video/*");
					Toast.makeText(MainActivity.this,R.string.bigFileWarning,Toast.LENGTH_LONG).show();
					startActivityForResult(chooseVideoIntent, CHOOSE_VIDEO_REQUEST);
					break;
			}
			
		}

		private Uri getOutPutMediaFile(int mediaType) {
			 // To be safe, we have to check that the SDCard is mounted
		    // using Environment.getExternalStorageState() before doing this.
			String appName=MainActivity.this.getString(R.string.app_name);
			if(isExternalMemoryAvailable()){
				//return the Uri
				//1. Get the external Storage directory
				
				File mediaStorageDir=new File(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);
				
				//2. Create our SUBDIRECTORY
				if(!mediaStorageDir.exists()){
					/*
					 * Creates the directory named by the trailing filename of this file, including the complete 
					 * directory path required to create this directory.
					 */
					if(!mediaStorageDir.mkdirs()){
						Log.e(TAG,"Failed to create a Directory");
						return null;
					}
				}
				//3. Create a file name	
				//4. Create a file
				File mediaFile;
				Date now=new Date();
				String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(now);
				String path= mediaStorageDir.getPath()+ File.separator;
				if(mediaType==MEDIA_TYPE_IMAGE){
					mediaFile=new File(path+ "IMG_"+ timeStamp + ".jpg");
		
				}
				else if (mediaType==MEDIA_TYPE_VIDEO) {
					mediaFile=new File(path+ "VID_"+ timeStamp + ".mp4");
				}
				else{
					return null;
				}
				Log.d(TAG,"File path"+Uri.fromFile(mediaFile));
				//5. return the file's Uri
				return Uri.fromFile(mediaFile);
			}
			else
			{
				return null;
			}

		}

		private boolean isExternalMemoryAvailable() {
			// returns MEDIA_MOUNTED if the media is present and mounted at its mount point with read/write access. 
			String state=Environment.getExternalStorageState();
			if(state.equals(Environment.MEDIA_MOUNTED))
				return true;
			else
				return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		// Set up the action bar.
		final android.app.ActionBar mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.show();
		
		//this is used to help tract statistics around application opens(how users are using our application) 
        ParseAnalytics.trackAppOpened(getIntent());
		// Create an Intent to call our login activity when the App is launched
		
        /**
         * It would be bothersome if the user had to log in every time they open the app. 
         * We can avoid this by using the cached currentUser object.
         */
        ParseUser currentUser=ParseUser.getCurrentUser();
        
        if (currentUser!=null)
	        {
	        	String name=currentUser.getUsername(); 
	        	Log.i(TAG, name);
	        }
        else{
				navigateToLoginScreen();
        }
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
							@Override
							public void onPageSelected(int position) {
								mActionBar.setSelectedNavigationItem(position);
							}
						});

				// For each of the sections in the app, add a tab to the action bar.
				for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
					// Create a tab with text corresponding to the page title defined by
					// the adapter. Also specify this Activity object, which implements
					// the TabListener interface, as the callback (listener) for when
					// this tab is selected.
					mActionBar.addTab(mActionBar.newTab()
							.setText(mSectionsPagerAdapter.getPageTitle(i))
							.setIcon(mSectionsPagerAdapter.getIcon(i))
							.setTabListener(this));
				}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK){
		
			// add it to the Gallery
			
			if(requestCode==CHOOSE_VIDEO_REQUEST ||requestCode==CHOOSE_PICTURE_REQUEST ){
				if(data==null){
					Toast.makeText(this,R.string.general_error, Toast.LENGTH_LONG).show();
				}
				else{
					mMediaUri=data.getData();
				}
				Log.i(TAG,"Media URI" + mMediaUri);
				if (requestCode==CHOOSE_VIDEO_REQUEST){
					int videoFile_Size=0;
					InputStream inputFromGallery=null;
					try {
						/*
						 * Open a stream on to the content associated with a content URI. 
						 * If there is no data associated with the URI, FileNotFoundException is thrown.
						 */
						inputFromGallery=getContentResolver().openInputStream(mMediaUri);
						/*
						 * Returns an estimated number of bytes that can be read or skipped without blocking for more input. 
						 */
						videoFile_Size=inputFromGallery.available();
						
						if(videoFile_Size > MAX_VIDEO_SIZE){
							
							Toast.makeText(MainActivity.this,R.string.file_Too_Targe_Warning,
									Toast.LENGTH_LONG).show();
						}
						
					} catch (FileNotFoundException e) {
						Toast.makeText(this,R.string.error_opening_the_file, Toast.LENGTH_LONG).show();
						e.printStackTrace();
					} catch (IOException e) {
						Toast.makeText(this,R.string.error_opening_the_file, Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					finally{
						try {
							inputFromGallery.close();
						} catch (IOException e) {
							/*
							 * blank
							 */
						}
					}
				}
			}
			else{
				/* Add it to Gallery
				 * Broadcast Action: Request the media scanner to scan a file and add it to the media database. 
				 * The path to the file is contained in the Intent.mData field
				 */
				Intent mediaScanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mMediaUri);
				sendBroadcast(mediaScanIntent);
			}
			Intent recipientsIntent=new Intent(this, RecipientsActivity.class);
			recipientsIntent.setData(mMediaUri);
			
			String fileType;
			
			if(requestCode==CHOOSE_PICTURE_REQUEST||requestCode==TAKE_PICTURE_REQUEST) {
					fileType=ParseConstants.TYPE_IMAGE;
			}
			else{
					fileType=ParseConstants.TYPE_VIDEO;
			}
			recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
			startActivity(recipientsIntent);
		}
		else if(resultCode!=RESULT_CANCELED){
			Toast.makeText(this,R.string.general_error, Toast.LENGTH_LONG).show();
		}
	}

	private void navigateToLoginScreen() {
		
		Intent intent=new Intent(this,LoginActivity.class);
		/*
		 * If set, this activity will become the start of a new task on this history stack. A task 
		 * (from the activity that started it to the next task activity) defines an atomic group of
		 *  activities that the user can move to.
		 */
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//TODO TASK and back Stack
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_logout:
						ParseUser.logOut();
						navigateToLoginScreen();
				        break;
		case R.id.action_edit_friends:
			    		Intent intent=new Intent(this,EditBuddyActivity.class);
						startActivity(intent);
						break;
		case R.id.action_take_picture:
						AlertDialog.Builder builder=new AlertDialog.Builder(this);
						builder.setItems(R.array.camera_choice, mDialogListener);
						AlertDialog dialog=builder.create();
						dialog.show();
						break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		// When the given tab is selected, switch to the corresponding page in the ViewPager.
				mViewPager.setCurrentItem(tab.getPosition());		
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	

}
