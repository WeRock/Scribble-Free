package com.sandeepmahanty.scribblefree;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sandeepmahanty.scribblefree.adapter.ScribbletCursorAdapter;
import com.sandeepmahanty.scribblefree.adapter.ScribbletsDBAdapter;

public class LockerViewFragment extends Fragment {

	private ScribbletsDBAdapter dbHelper;
	private SimpleCursorAdapter dataAdapter;
	private ListView notesList;
	private Cursor cursor;
	private int selectedItems=0;
	private ArrayList<Integer> selectedNotes;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_home, container, false);
		dbHelper = new ScribbletsDBAdapter(getActivity());
        dbHelper.open();
       
        displayNotes(view);
		return view;
	}
	public void refreshDataSet(){
		dbHelper.open();
		cursor = dbHelper.getAllSecureNotes();
		dataAdapter.changeCursor(cursor);
	}
	@Override
	public void onResume() {
			
		refreshDataSet();
		super.onResume();
	}
private void displayNotes(View view) {
    	
    	cursor = dbHelper.getAllSecureNotes();
    	
    	String[] columns = new String[]{
    			ScribbletsDBAdapter.COL_NOTE_TITLE,
    			ScribbletsDBAdapter.COL_NOTE_CONTENT,
    			ScribbletsDBAdapter.COL_NOTE_DATE,
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
				Intent intent= new Intent(getActivity(), EditNote.class);
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

    	    @Override
    	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    	        // Respond to clicks on the actions in the CAB
    	        switch (item.getItemId()) {
    	            case R.id.menu_delete:
    	            	deleteNotes(selectedNotes);
    	                mode.finish(); // Action picked, so close the CAB
    	                
    	                return true;
    	            case R.id.menu_move_to_locker:
    	            	moveOutOfLocker(selectedNotes);
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
		
		Cursor cur=dbHelper.getAllSecureNotes();
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
	private void moveOutOfLocker(ArrayList<Integer> list){
	
		Cursor cur=dbHelper.getAllSecureNotes();
		
		if(list.size()==1){
			dbHelper.updateNote(cur.getString(0),cur.getString(1),cur.getString(2),cur.getString(4),cur.getString(3),"false");
		}
		else {			
		
			while(cur.moveToNext()){
				for(Integer item:list){
					
					if(item==cur.getPosition()){
						dbHelper.updateNote(cur.getString(0),cur.getString(1),cur.getString(2),cur.getString(4),cur.getString(3),"false");
					}
				}
			}
		}
		Toast.makeText(getActivity(),"Notes successfully moved out of locker", Toast.LENGTH_SHORT).show();
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
	public void onPause() {
		super.onPause();
		if(dbHelper!=null)
			dbHelper.close();
	}
}
