package com.sandeepmahanty.scribblefree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sandeepmahanty.scribblefree.adapter.ScribbletsDBAdapter;
import com.sandeepmahanty.scribblefree.model.Constants;

public class DrawingFragment extends Fragment {
	
	private ScribbletsDBAdapter dbHelper;
	
	private DrawingView drawView;
	
	//buttons
	private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
	//sizesgetActivity
	private float smallBrush, mediumBrush, largeBrush;
	
	public DrawingFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
			View rootView = inflater.inflate(R.layout.fragment_drawing, container, false);        
        
			//get drawing view
      		drawView = (DrawingView)rootView.findViewById(R.id.drawing);

      		//get the palette and first color button
      		LinearLayout paintLayout = (LinearLayout)rootView.findViewById(R.id.paint_colors_1);
      		currPaint = (ImageButton)paintLayout.getChildAt(0);
      		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

      		//sizes from dimensions
      		smallBrush = getResources().getInteger(R.integer.small_size);
      		mediumBrush = getResources().getInteger(R.integer.medium_size);
      		largeBrush = getResources().getInteger(R.integer.large_size);

      		//draw button
      		drawBtn = (ImageButton)rootView.findViewById(R.id.draw_btn);
      		drawBtn.setOnClickListener(new DrawingToolsHandler());

      		//set initial size
      		drawView.setBrushSize(mediumBrush);

      		//erase button
      		eraseBtn = (ImageButton)rootView.findViewById(R.id.erase_btn);
      		eraseBtn.setOnClickListener(new DrawingToolsHandler());

      		//new button
      		newBtn = (ImageButton)rootView.findViewById(R.id.new_btn);
      		newBtn.setOnClickListener(new DrawingToolsHandler());

      		//save button
      		saveBtn = (ImageButton)rootView.findViewById(R.id.save_btn);
      		saveBtn.setOnClickListener(new DrawingToolsHandler());
      		
      		for(int i=0;i<paintLayout.getChildCount();i++){
      			paintLayout.getChildAt(i).setOnClickListener(new DrawingToolsHandler());
      		}
      		dbHelper = new ScribbletsDBAdapter(getActivity());
      		dbHelper.open();
      		setHasOptionsMenu(true);
        return rootView;
    }
	public void paintClicked(View view){
		//use chosen color

		//set erase false
		drawView.setErase(false);
		drawView.setBrushSize(drawView.getLastBrushSize());

		if(view!=currPaint){
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			//update ui
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint=(ImageButton)view;
		}
	}	
	
	@Override
	public void onPause() {
		super.onPause();
		if(dbHelper!=null)
			dbHelper.close();
	}

	public class DrawingToolsHandler implements OnClickListener{

		@Override
		public void onClick(View view) {
			if(view.getId()==R.id.draw_btn){
				//draw button clicked
				final Dialog brushDialog = new Dialog(getActivity());
				brushDialog.setTitle("Brush size:");
				brushDialog.setContentView(R.layout.brush_chooser);
				//listen for clicks on size buttons
				ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
				smallBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(false);
						drawView.setBrushSize(smallBrush);
						drawView.setLastBrushSize(smallBrush);
						brushDialog.dismiss();
					}
				});
				ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
				mediumBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(false);
						drawView.setBrushSize(mediumBrush);
						drawView.setLastBrushSize(mediumBrush);
						brushDialog.dismiss();
					}
				});
				ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
				largeBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(false);
						drawView.setBrushSize(largeBrush);
						drawView.setLastBrushSize(largeBrush);
						brushDialog.dismiss();
					}
				});
				//show and wait for user interaction
				brushDialog.show();
			}
			else if(view.getId()==R.id.erase_btn){
				//switch to erase - choose size
				final Dialog brushDialog = new Dialog(getActivity());
				brushDialog.setTitle("Eraser size:");
				brushDialog.setContentView(R.layout.brush_chooser);
				//size buttons
				ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
				smallBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(smallBrush);
						brushDialog.dismiss();
					}
				});
				ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
				mediumBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(mediumBrush);
						brushDialog.dismiss();
					}
				});
				ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
				largeBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						drawView.setErase(true);
						drawView.setBrushSize(largeBrush);
						brushDialog.dismiss();
					}
				});
				brushDialog.show();
			}
			else if(view.getId()==R.id.new_btn){
				//new button
				AlertDialog.Builder newDialog = new AlertDialog.Builder(getActivity());
				newDialog.setTitle("New drawing");
				newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
				newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						drawView.startNew();
						dialog.dismiss();
					}
				});
				newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.cancel();
					}
				});
				newDialog.show();
			}
			else if(view.getId()==R.id.save_btn){
				//save drawing
				AlertDialog.Builder saveDialog = new AlertDialog.Builder(getActivity());
				saveDialog.setTitle("Save drawing");
				saveDialog.setMessage("Save drawing to device Gallery?");
				saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						
						//save drawing
						drawView.setDrawingCacheEnabled(true);
						//attempt to save
						File newFolder=  new File(Environment.getExternalStorageDirectory(),Constants.EXT_STORAGE_DIR);
										
						if(!newFolder.exists()){							
							newFolder.mkdir();							
						}
						Random generator = new Random();
						int n = 10000;
						n = generator.nextInt(n);
						String title= "MyScribble";
						title+=n+".png";
						boolean imgSaved=false;
						try {
							Bitmap bitmap= drawView.getDrawingCache();
							File imgFile = new File(newFolder,title);
							FileOutputStream out = new FileOutputStream(imgFile);
							imgSaved=bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
							out.flush();
							out.close();
					
						} catch (FileNotFoundException e) {
							Log.d("Image", "Failed To Insert");
							e.printStackTrace();
						} catch (IOException e) {
							Log.d("Image", "Failed To Insert");
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Calendar date= Calendar.getInstance();
						String day=String.valueOf(date.get(Calendar.DAY_OF_MONTH));
						String month=String.valueOf(date.get(Calendar.MONTH));
						String year=String.valueOf(date.get(Calendar.YEAR));
						String dt=day+"/"+month+"/"+year.substring(2);
						//feedback
						if(imgSaved){
							dbHelper.createScribble(title, newFolder.getAbsolutePath(), dt);
							Toast savedToast = Toast.makeText(getActivity(), 
									"Drawing saved to Gallery!", Toast.LENGTH_SHORT);
							savedToast.show();
						}
						else{
							Toast unsavedToast = Toast.makeText(getActivity(), 
									"Oops! Image could not be saved.", Toast.LENGTH_SHORT);
							unsavedToast.show();
						}
						drawView.destroyDrawingCache();
					}
				});
				saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						dialog.cancel();
					}
				});
				saveDialog.show();
			}
			else{
				paintClicked(view);
			}
			
		}
		
	}
	
	
}
