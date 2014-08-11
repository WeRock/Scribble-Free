package com.sandeepmahanty.scribblefree;

import java.util.ArrayList;

import com.sandeepmahanty.scribblefree.adapter.ScribbletCursorAdapter;
import com.sandeepmahanty.scribblefree.adapter.ScribbletsDBAdapter;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.OnItemClickListener;

public class DrawingListFragment extends Fragment {
	
	ScribbletCursorAdapter dataAdapter;
	ScribbletsDBAdapter dbHelper;
	private ListView notesList;
	private Cursor cursor;
	private int selectedItems=0;
	private ArrayList<Integer> selectedNotes;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,false);
		setHasOptionsMenu(true);
		
		 dbHelper = new ScribbletsDBAdapter(getActivity());
	     dbHelper.open();
	       
	        displayNotes(rootView);
		return rootView;
	}
	public void refreshDataSet(){
		dbHelper.open();
		cursor = dbHelper.getAllScribbles();
		dataAdapter.changeCursor(cursor);
	}
	@Override
	public void onResume() {
			
		refreshDataSet();
		super.onResume();
	}
private void displayNotes(View view) {
    	
    	cursor = dbHelper.getAllScribbles();
    	
    	String[] columns = new String[]{
    			ScribbletsDBAdapter.COL_SCRIBBLE_TITLE,
    			ScribbletsDBAdapter.COL_SCRIBBLE_PATH,
    			ScribbletsDBAdapter.COL_SCRIBBLE_DATE,
    	};
    	
    	int[] views = new int[]{
    			R.id.title,
    			R.id.content,
    			R.id.date    			
    	};
    	
    	dataAdapter = new ScribbletCursorAdapter(getActivity(), R.layout.scribblet_view, cursor, columns, views, 0);
    	
    	notesList = (ListView)view.findViewById(R.id.scribblet_list);
    	notesList.setAdapter(dataAdapter);   
    	notesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    	notesList.setOnItemClickListener(new OnItemClickListener() {
			
		
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				
				TextView rowId = (TextView)v.findViewById(R.id.row_id_hidden);
				Intent intent= new Intent(getActivity(), EditScribble.class);
				intent.putExtra( "com.sandeepmahanty.scribblefree.id", rowId.getText());
				startActivity(intent);
			}
		});
    	notesList.setMultiChoiceModeListener(new MultiChoiceModeListener() {

    	    @Override
    	    public void onItemCheckedStateChanged(ActionMode mode, int position,
    	                                          long id, boolean checked) {
    	    	
    	        // Here you can do something when items are selected/de-selected,
    	        // such as update the title in the CAB
    	    	
    	    	View selectedView = notesList.getChildAt(position);
    	    	if(selectedView!=null){
    	    		selectedView.setSelected(checked);
    	    		
    	    	}
    	    	if(checked){
    	    		selectedItems+=1;
    	    	}
    	    	else{
    	    		selectedItems-=1;
    	    		if(selectedItems<0)
    	    			selectedItems=0;
    	    	}
    	    	mode.setTitle("Selected "+selectedItems+" note(s)");
    	    	    	    	   	    	
    	    	if(checked){
    	    		
    	    		selectedNotes.add(position);
    	    	}
    	    	else{
    	    		removeIfExists(selectedNotes, position);
    	    	}	
    	    	
    	    }
    	    private void moveToLocker(ArrayList<Integer> list){
    	    	
    			Cursor cur=dbHelper.getAllNotes();
    			
    			if(list.size()==1){
    				
    				dbHelper.updateNote(cur.getString(0),cur.getString(1),cur.getString(2),cur.getString(4),cur.getString(3),"true");
    			}
    			else {			
    				
    				while(cur.moveToNext()){
    					for(Integer item:list){
    						Log.d("Outside if", "Outside if");
    						if(item==cur.getPosition()){
    							Log.d("Inside if", "Inside if");
    								dbHelper.updateNote(cur.getString(0),cur.getString(1),cur.getString(2),cur.getString(4),cur.getString(3),"true");
    						}
    					}
    				}
    			}
    			Toast.makeText(getActivity(),"Notes successfully moved to locker", Toast.LENGTH_SHORT).show();
    			refreshDataSet();
    		}
    		private void removeIfExists(ArrayList<Integer> l,int position){
    			
    			for(int i=0;i<l.size();i++){
    				
    				if(l.get(i)==position){
    					l.remove(i);
    					break;
    				}
    			}
    				
    		}
    		
    	
    	    @Override
    	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    	        // Respond to clicks on the actions in the CAB
    	        switch (item.getItemId()) {
    	            case R.id.menu_delete:
    	            	deleteNotes(selectedNotes);
    	                mode.finish(); // Action picked, so close the CAB
    	                
    	                return true;
    	            case R.id.menu_move_to_locker:
    	            	moveToLocker(selectedNotes);
    	            	mode.finish();
    	            	return true;
    	            default:
    	                return false;
    	        }
    	    }

    	    @Override
    	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    	        // Inflate the menu for the CAB
    	        MenuInflater inflater = mode.getMenuInflater();
    	        inflater.inflate(R.menu.context_menu, menu);
    	        if(selectedNotes==null){
    	    		
    	    		selectedNotes = new ArrayList<Integer>();
    	    	}
    	        selectedItems=0;
                selectedNotes.clear();
    	        return true;
    	    }

    	    @Override
    	    public void onDestroyActionMode(ActionMode mode) {
    	        // Here you can make any necessary updates to the activity when
    	        // the CAB is removed. By default, selected items are deselected/unchecked.
    	    }

    	    @Override
    	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    	        // Here you can perform updates to the CAB due to
    	        // an invalidate() request
    	    	
    	    	return false;
    	    }
    	});    	
    	
    	
    }
	
	private void deleteNotes(ArrayList<Integer> list){
				
		Cursor cur=dbHelper.getAllNotes();
		if(list.size()==1){
			dbHelper.removeNote(cur.getString(0));
		}else {
			
			while(cur.moveToNext()){
				for(Integer item:list){
					
					if(item==cur.getPosition()){
						dbHelper.removeNote(cur.getString(0));
					}
				}
			}
		}
		Toast.makeText(getActivity(),"Notes Deleted successfully", Toast.LENGTH_SHORT).show();
		refreshDataSet();
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		MenuItem mnu_create = menu.add(0, 0, 1, "CreateScribble");
		mnu_create.setIcon(R.drawable.ic_action_edit_dark);
		mnu_create.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getTitle().equals("CreateScribble")){			
			Toast.makeText(getActivity(), "Create  Scribblet clicked", Toast.LENGTH_LONG).show();
			getFragmentManager().beginTransaction().replace(R.id.frame_container, new DrawingFragment()).commit();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
		
	};
	
	@Override
	public void onPause() {
		super.onPause();
		if(dbHelper!=null)
			dbHelper.close();
		
	}

}
