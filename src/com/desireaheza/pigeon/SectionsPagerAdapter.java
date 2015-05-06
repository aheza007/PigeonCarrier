package com.desireaheza.pigeon;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter{
	Context mContext;
	
	public SectionsPagerAdapter(Context context,FragmentManager fm) {
	    super(fm);
		mContext=context;
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a PlaceholderFragment (defined as a static inner class
		// below).
		//return PlaceholderFragment.newInstance(position + 1);
		switch (position) {
			case 0:
				return new InBoxFragment();
			case 1:
				return new FriendsFragment(); 
		}
		 return null;
		
	}
	
	@Override
	public int getCount() {
		// Show 2 total pages.
		return 2;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		//this method is used to show the title of the tabs in the ActionBar
		/*
		 * Locale represents a language/country/variant combination. Locales are used to alter the presentation
		 * of information such as numbers or dates to suit the conventions in the region they describe.
		 */
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return mContext.getString(R.string.title_section1).toUpperCase(l);

		case 1:
			return mContext.getString(R.string.title_section2).toUpperCase(l);
		
		}
		return null;
	}
	
	public int getIcon(int position) {
		//this method is used to return an ICON id
		
		switch (position) {
		case 0:
			return R.drawable.inbox_icon;

		case 1:
			return R.drawable.friends_icon;
		
		}
		return R.drawable.inbox_icon;
	}
}


