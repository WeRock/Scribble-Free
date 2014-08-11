package com.sandeepmahanty.scribblefree.adapter;

import com.sandeepmahanty.scribblefree.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScribbletCursorAdapter extends SimpleCursorAdapter {
	
	private int mLayout;
    LayoutInflater mInflater;
    
	public ScribbletCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {		
		
		super(context, layout, c, from, to, flags);
		mInflater = LayoutInflater.from(context);
		mLayout=layout;
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = mInflater.inflate(mLayout, parent, false);
        // edit: no need to call bindView here. That's done automatically
        return v;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		TextView title=(TextView) view.findViewById(R.id.title);
		TextView content=(TextView) view.findViewById(R.id.content);
		TextView day=(TextView) view.findViewById(R.id.day);
		TextView month=(TextView) view.findViewById(R.id.month);
		TextView year=(TextView) view.findViewById(R.id.year);
		TextView row_id=(TextView) view.findViewById(R.id.row_id_hidden);
		title.setText(cursor.getString(1));
		content.setText(cursor.getString(2));
		String date = cursor.getString(3);
		Log.d("Date", date);
		Log.d("Content", cursor.getString(2));
		String dayOfWeek= date.substring(0,date.indexOf("/"));
		date=date.substring(date.indexOf("/")+1);
		String mon=date.substring(0,date.indexOf("/"));
		row_id.setText(cursor.getString(0));
		if(Integer.parseInt(mon)<10){
			mon="0"+mon;
		}
		date=date.substring(date.indexOf("/")+1);
		String yr=date;
		day.setText(dayOfWeek);
		month.setText(mon);
		year.setText(yr);
		
	}
	

}
