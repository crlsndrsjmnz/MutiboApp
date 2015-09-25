/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.ui;

import co.carlosandresjimenez.mocca.mutibo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoadIndicatorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
    	View v = inflater.inflate(R.layout.fragment_loading, container, false);
    	
    	return v;
	}
	
}
