/*
 * 
 */
package com.sandeepmahanty.scribblefree;

import java.util.Calendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sandeepmahanty.scribblefree.adapter.ScribbletsDBAdapter;
import com.sandeepmahanty.scribblefree.model.Constants;
import com.sandeepmahanty.scribblefree.model.HtmlElements;
import com.sandeepmahanty.scribblefree.model.ColorPicker;
/**
 * The Class CreateNote.
 */
public class CreateNote extends Activity implements ColorPicker.OnColorChangedListener {
	
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
	
	private int START,END;
	private int CURRENT_COLOR;
	private int DEFAULT_COLOR;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.activity = this; 
		setContentView(R.layout.create_new_note);
		title = (EditText)findViewById(R.id.create_note_title);
		content = (EditText)findViewById(R.id.create_note_content);
		content.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
				
				/* Making sure that onKey is called once for ACTION_UP */
				if (keyEvent.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
			
				switch(keyCode){
					case KeyEvent.KEYCODE_ENTER : 
								if(isListMode){
									addHtmlElement(Constants.OPERATION_ADD_BULLET);
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
	private void addHtmlElement(int operation){
		
		/* Getting the code for the bullet */
		String bullet=Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED).toString();		
			
		/* Getting the current text in the edit text */
		String text= content.getText().toString();
		
		/* Content after the bullet */
		String contentAfterBullet=null;
		
		switch(operation){
		case Constants.OPERATION_ADD_BULLET:
			
			/* Adding the break and then appending the bullet element */
			text+=Html.fromHtml("<br/>")+""+Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED)+""+Html.fromHtml(HtmlElements.ELEMENT_TYPE_NBSP);
			
			INDENT_LEVEL=1;
			break;
		case Constants.OPERATION_ADD_LEFT_INDENT:
			
			if(text.length()>0 && text!=null){
				Toast.makeText(getApplication(), "Char: "+text.charAt(content.getSelectionStart()-2)+" Length: "+bullet.length(), Toast.LENGTH_SHORT).show();
				
				/* Getting the content after last bullet */
				contentAfterBullet = text.substring(text.lastIndexOf(bullet)+1);
				
				/* Removing the space from left side (Dirty work need to make it better */
				String breakCode= Html.fromHtml("<br/>").toString();
				
				if(text.lastIndexOf(breakCode)!=-1){
					text=text.substring(0, text.lastIndexOf(bullet)-1);
				}
				else{
					text=text.substring(0, text.lastIndexOf(bullet));
				}
				
				/* Generating the final text after removing indent */
				text+=Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED)+contentAfterBullet;
			}			
			break;
		case Constants.OPERATION_ADD_RIGHT_INDENT:
			if(text.length()>0 && text!=null){
				/* Getting the content after last bullet */
				contentAfterBullet = text.substring(text.lastIndexOf(bullet)+1);
				
				/* Getting the text before last bullet */
				text=text.substring(0, text.lastIndexOf(bullet));
				
				/* Generating the final text after adding indent */
				text+="\t"+Html.fromHtml(HtmlElements.BULLET_TYPE_FILLED)+contentAfterBullet;
			}
			break;
			
		case Constants.OPERATION_ADD_COLOR:
			if(text.length()>0 && START!=END){
				String contentBefore = text.substring(0,START);
				String contentAfter= text.substring(END);
				String contentToColor=text.substring(START,END);
				HtmlElements.R=Color.red(CURRENT_COLOR);
				HtmlElements.G=Color.green(CURRENT_COLOR);
				HtmlElements.B=Color.blue(CURRENT_COLOR);
				
				String completeText="<u>"+contentToColor+"</u>";
				text= contentBefore+Html.fromHtml(completeText)+contentAfter;
				Toast.makeText(activity, "Content HTML: "+text,Toast.LENGTH_LONG).show();
				
			}
			break;
		default:break;
		}			
			
		/* Setting the text to edit text */
		content.setText(text);
		
		/* Setting the pointer to end */
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
			((ImageButton)view).setImageResource(R.drawable.bullet_list);
			
		}else{			
			isListMode= true;
			INDENT_LEVEL=1;
			addHtmlElement(Constants.OPERATION_ADD_BULLET);			
			((ImageButton)view).setImageResource(R.drawable.list_view);
		}
		
	}
	
	/**
	 * Adds the right indent.
	 *
	 * @param view the view
	 */
	public void addRightIndent(View view){
		if(INDENT_LEVEL<4 && isListMode){
			
			/* Adding the Right indent */
			addHtmlElement(Constants.OPERATION_ADD_RIGHT_INDENT);
			
			/* Increasing the indent level */
			INDENT_LEVEL+=1;
			
			/* Making sure the indent is within bounds */
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
		if(INDENT_LEVEL>1 && isListMode){
			
			/* Removing the right indent */
			addHtmlElement(Constants.OPERATION_ADD_LEFT_INDENT);
			
			/* Decreasing the indent level */
			INDENT_LEVEL-=1;
			
			/* Making sure the indent is within bounds */
			if(INDENT_LEVEL<0)
				INDENT_LEVEL=0;
		}
	}
	Activity activity;
	/**
	 * Adds the color.
	 *
	 * @param view the view
	 */
	public void addColor(View view){	
		START	=	content.getSelectionStart();
		END	=	content.getSelectionEnd();
		new ColorPicker(activity,CreateNote.this, Color.WHITE).show();		
	}
	
	private void changeColor(int color){
		
		CURRENT_COLOR=color;
		
		addHtmlElement(Constants.OPERATION_ADD_COLOR);
		/*Spannable text= new SpannableString(content.getText());
		if(color!=0){
			text.setSpan(new ForegroundColorSpan(color), START, END, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		content.setText(text);
		content.setSelection(content.getText().length());		
		*/
	}
	/**
	 * Changes the font.
	 *
	 * @param view the view
	 */
	public void changeFontSize(View view){
		
	}

	@Override
	public void colorChanged(int color) {	
		changeColor(color);
		Toast.makeText(activity, "Color: "+color, Toast.LENGTH_SHORT).show();
	}
}
