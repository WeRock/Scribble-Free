package com.sandeepmahanty.scribblefree;

import java.util.Calendar;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.sandeepmahanty.scribblefree.adapter.ScribbletsDBAdapter;

public class EditNote extends Activity {
	
	private EditText title;
	private EditText content;
	private ScribbletsDBAdapter dbHelper;
	private String row_id="";	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_note);
		dbHelper = new ScribbletsDBAdapter(getApplicationContext());
		dbHelper.open();
		
		Bundle bundle= getIntent().getExtras();
		row_id= bundle.getString("com.sandeepmahanty.scribblefree.id");
		Cursor cursor = dbHelper.getOneNote(row_id);
				
		title = (EditText) findViewById(R.id.edit_note_title);
		content =(EditText) findViewById(R.id.edit_note_content);
		title.setText(cursor.getString(1));
		content.setText(cursor.getString(2));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mnu_edit = menu.add(0, 0, 1, "Edit");
		mnu_edit.setIcon(R.drawable.ic_action_edit_dark);
		mnu_edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getTitle().equals("Edit")){
			
			Toast.makeText(getApplicationContext(), "Edit clicked", Toast.LENGTH_LONG).show();
			
			title.setEnabled(true);
			content.setEnabled(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		dbHelper = new ScribbletsDBAdapter(getApplicationContext());
        dbHelper.open();
		Calendar date= Calendar.getInstance();
		String day=String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		String month=String.valueOf(date.get(Calendar.MONTH));
		String year=String.valueOf(date.get(Calendar.YEAR));
		String dt=day+"/"+month+"/"+year.substring(2);
		if(content.getText().toString()!=null && content.getText().toString().length()>0){
			
			String note_title="";
			if(title.getText().toString().length()==0){
				note_title="Note";
			}
			
			else{
				note_title=title.getText().toString();
			}	
			
			//Need to make sure that the note can be secured from here as well "false" should be flexible
			dbHelper.updateNote(row_id,note_title, content.getText().toString(),"1",dt,"false");
						
		}
		else
		{
			Toast.makeText(getApplicationContext(), "No content specified. Note not modified!", Toast.LENGTH_SHORT).show();
		}
		
		dbHelper.close();
	}

}
