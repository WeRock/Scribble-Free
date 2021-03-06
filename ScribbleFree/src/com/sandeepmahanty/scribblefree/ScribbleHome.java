package com.sandeepmahanty.scribblefree;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sandeepmahanty.scribblefree.adapter.NavDrawerListAdapter;
import com.sandeepmahanty.scribblefree.model.Constants;
import com.sandeepmahanty.scribblefree.model.NavDrawerItem;

public class ScribbleHome extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mnu_create = menu.add(0, 0, 1, "Create");
		mnu_create.setIcon(R.drawable.ic_action_new);
		mnu_create.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		Log.d("Activity", "Called");

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}else if(item.getTitle().equals("Create")){
			
			Toast.makeText(getApplicationContext(), "Create clicked", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(getApplicationContext(), CreateNote.class);
			startActivity(intent);
			return true;
		}
	
			return false;
		
	}

	
	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		Bundle args = new Bundle();
	    
		switch (position) {
		case 0:
			fragment = new HomeFragment();			
			args.putString("fragment_id","Home");			
			break;
		case 1:
			fragment = new FindPeopleFragment();
			break;
		case 2:
			fragment = new LockerFragment();
			break;
		case 3:
			fragment = new DrawingListFragment();
			break;
		case 4:
			fragment = new PagesFragment();
			break;
		case 5:
			fragment = new WhatsHotFragment();
			break;

		default:
			break;
		}
		
		if (fragment != null) {
			
			fragment.setArguments(args);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	public void keypadPress(View view){
		TextView display = (TextView)findViewById(R.id.pin_box);
		
		switch(view.getId()){
		case R.id.keypad_one:
			display.setText(display.getText()+"1");
			break;
		case R.id.keypad_two:
			display.setText(display.getText()+"2");
			break;
		case R.id.keypad_three:
			display.setText(display.getText()+"3");
			break;
		case R.id.keypad_four:
			display.setText(display.getText()+"4");
			break;
		case R.id.keypad_five:
			display.setText(display.getText()+"5");
			break;
		case R.id.keypad_six:
			display.setText(display.getText()+"6");
			break;
		case R.id.keypad_seven:
			display.setText(display.getText()+"7");
			break;
		case R.id.keypad_eight:
			display.setText(display.getText()+"8");
			break;
		case R.id.keypad_nine:
			display.setText(display.getText()+"9");
			break;
		case R.id.keypad_clear:
			display.setText("");
			break;
		case R.id.keypad_OK:
			checkPassword(display.getText().toString());
			break;
		
		}
	}
	
	void checkPassword(String pass){
		
		SharedPreferences prefs = getSharedPreferences(Constants.SCRIBBLE_PREFS, 0);
		String password = prefs.getString(Constants.PASS_KEY,"12345");
		if(password.equals(pass)){	
			FragmentManager manager = getFragmentManager();
			FragmentTransaction ftr= manager.beginTransaction();
			ftr.replace(R.id.frame_container,new LockerViewFragment());
			ftr.commit();
			Log.d("finished Transaction","Looks like it");
		}
		else{
			Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
		}
		
		
	}

}
