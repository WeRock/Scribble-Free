package com.sandeepmahanty.scribblefree;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LockerFragment extends Fragment {
	
	public LockerFragment(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) { 
			
        View rootView = null;  
        
        rootView = inflater.inflate(R.layout.locker_layout, container, false);
       
        return rootView;
    }
	
}
