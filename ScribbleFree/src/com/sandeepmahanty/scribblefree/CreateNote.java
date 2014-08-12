/*
 * 
 */
package com.sandeepmahanty.scribblefree;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.sandeepmahanty.scribblefree.adapter.ScribbletsDBAdapter;
import com.sandeepmahanty.scribblefree.model.HtmlElements;

/**
 * The Class CreateNote.
 */
public class CreateNote extends Activity {
	
	/** The title. */
	private EditText title;
	
	/** The content. */
	private EditText content;
	
	/** The db helper. */
	private ScribbletsDBAdapter dbHelper;
	
	/** The is list mode. */
	private static boolean isListMode=false; 
	
	/** The indent level. */
	private static int INDENT_LEVEL=0;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.create_new_note);
		title = (EditText)findViewById(R.id.create_note_title);
		content = (EditText)findViewById(R.id.create_note_content);
		content.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
				
				if (keyEvent.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
			
				switch(keyCode){
				case KeyEvent.KEYCODE_ENTER : if(isListMode){
												
													addHtmlElement(HtmlElements.BULLET_TYPE_FILLED);
													return true;
											}
											return false;
				case KeyEvent.KEYCODE_BACK: 
					finish();
					Log.d("Back key Called", "Called");
					return true;
				default:return false;
				}
				
			}
		});
	}
	
	/**
	 * Adds the html element.
	 *
	 * @param element the element
	 */
	private void addHtmlElement(String element){
		
		String text=content.getText().toString();
		if(element.equals(HtmlElements.BULLET_TYPE_FILLED) || element.equals(HtmlElements.BULLET_TYPE_HOLLOW)){
			text+=Html.fromHtml("<br/>");
		}
		
		text+=" "+Html.fromHtml(element)+Html.fromHtml(HtmlElements.ELEMENT_TYPE_TAB);
		content.setText(text);
		content.setSelection(content.getText().length());
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
			
		super.onPause();
		saveNote();
		
	}
	
	/**
	 * Save note.
	 */
	private void saveNote(){
		
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
	
	/**
	 * Adds the bullet.
	 *
	 * @param view the view
	 */
	public void addBullet(View view){
		if(isListMode){
			isListMode=false;
			((ImageButton)view).setImageResource(R.drawable.ic_bullet_view);
			
		}else{			
			isListMode= true;
			INDENT_LEVEL=1;
			addHtmlElement(HtmlElements.BULLET_TYPE_FILLED);			
			((ImageButton)view).setImageResource(R.drawable.ic_list_view);
		}
		
	}
	
	/**
	 * Adds the right indent.
	 *
	 * @param view the view
	 */
	public void addRightIndent(View view){
		if(INDENT_LEVEL<4){
			String bullet=Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED).toString();
			String text= content.getText().toString();
			text=text.substring(0, text.lastIndexOf(bullet));
			text+="   "+Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED);
			content.setText(text);
			content.setSelection(content.getText().length());
			INDENT_LEVEL+=1;
			if(INDENT_LEVEL>4)
				INDENT_LEVEL=4;
		}			
	}
	
	/**
	 * Adds the left indent.
	 *
	 * @param view the view
	 */
	public void addLeftIndent(View view){
		if(INDENT_LEVEL>0){
			String bullet=Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED).toString();
			String text= content.getText().toString();
			text=text.substring(0, text.lastIndexOf(bullet)-4);
			text+=Html.fromHtml(HtmlElements.ELEMENT_TYPE_TAB)+" "+Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED);
			content.setText(text);
			content.setSelection(content.getText().length());
			INDENT_LEVEL-=1;
			if(INDENT_LEVEL<0)
				INDENT_LEVEL=0;
		}
	}
}
