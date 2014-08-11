package com.sandeepmahanty.scribblefree;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sandeepmahanty.scribblefree.adapter.ScribbletsDBAdapter;
import com.sandeepmahanty.scribblefree.model.HtmlElements;

public class CreateNote extends Activity {
	
	private EditText title;
	private EditText content;
	private ScribbletsDBAdapter dbHelper;
	
	private static boolean isListMode=false; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.create_new_note);
		title = (EditText)findViewById(R.id.create_note_title);
		content = (EditText)findViewById(R.id.create_note_content);
		content.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					if(isListMode){
						String text=content.getText().toString();
						Log.d("After Pressing enter: ", text);
						text+=Html.fromHtml("<br/>")+" "+Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED)+" ";
						content.setText(text);
						content.setSelection(content.getText().length());						
					}					
				}
				return false;
			}
		});
	}
	
	
	@Override
	protected void onPause() {
			
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
			
			dbHelper.createNote(note_title, content.getText().toString(),"1",dt,"false");
						
		}
		else
		{
			Toast.makeText(getApplicationContext(), "No content specified. Note not created!", Toast.LENGTH_SHORT).show();
		}
		dbHelper.close();
		
	}
	
	public void addBullet(View view){
		if(isListMode){
			isListMode=false;
			((Button)view).setText("Bullet Mode");
			
		}else{			
			isListMode= true;
			String text=content.getText().toString();
			text+=Html.fromHtml("<br/>")+" "+Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED)+" ";
			content.setText(text);
			content.setSelection(content.getText().length());
			((Button)view).setText("List Mode");
		}
		
	}
}
