package com.sandeepmahanty.scribblefree;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.sandeepmahanty.scribblefree.adapter.ScribbletsDBAdapter;

public class EditScribble extends Activity {
	
	private ScribbletsDBAdapter dbHelper;
	private String row_id="";	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scribble_edit);
		
		dbHelper = new ScribbletsDBAdapter(getApplicationContext());
		dbHelper.open();
		
		Bundle bundle= getIntent().getExtras();
		row_id= bundle.getString("com.sandeepmahanty.scribblefree.id");
		Cursor cursor = dbHelper.getOneScribble(row_id);
		Log.d("Row",row_id);
		ImageView imgView=(ImageView)findViewById(R.id.scribble_edit_view);
		setImageContent(imgView, cursor);
		
	}
	
	private void setImageContent(ImageView view, Cursor cursor){
		Log.d("Cursor", cursor.getString(2)+"/"+cursor.getString(1));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(2)+"/"+cursor.getString(1), options);
		view.setImageBitmap(bitmap);
		view.setScaleType(ScaleType.FIT_CENTER);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		dbHelper.close();
	}

}
