/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.ui;

import co.carlosandresjimenez.mocca.mutibo.R;

import com.google.android.gms.common.SignInButton;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsFragment extends Fragment implements OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener {

    public interface Listener {
        public void onChangeSoundVolume(int volumeIndex);
        public int onVolumeProgressRequested();
        public boolean showSignIn();
        public void setShowSignIn(boolean mShowSignIn);
        public void onSignInButtonClicked();
        public void onSignOutButtonClicked();
        public void onShowMainmenuRequested();
        public void onSetDifficultyLevel(int difficulty);
        public int onDifficultyLevelRequested();
        public void onBackPressed();
    }
    
	Listener mListener = null;
		
    public void setListener(Listener l) {
        mListener = l;
    }	
    
	MainActivity mOpener;
	
	private SignInButton signInButton;
	private ImageButton btSaveSettings;
	private SeekBar sbVolumeControl;
	
	private RadioGroup rgLevels;
	private RadioButton rbLevelEasy;
	private RadioButton rbLevelMedium;
	private RadioButton rbLevelHard;
	
	boolean mShowSignIn;
	
	int progressChanged = 0;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOpener = (MainActivity) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        
    	signInButton = (SignInButton) v.findViewById(R.id.sign_in_button);
    	btSaveSettings = (ImageButton) v.findViewById(R.id.imageButtonBack);
    	sbVolumeControl = (SeekBar) v.findViewById(R.id.seekBar1);
    	
    	rgLevels = (RadioGroup) v.findViewById(R.id.radioGroup);
    	rbLevelEasy = (RadioButton) v.findViewById(R.id.radioEasy);
    	rbLevelMedium = (RadioButton) v.findViewById(R.id.radioMedium);
    	rbLevelHard = (RadioButton) v.findViewById(R.id.radioHard);
    	
    	setShowSignInButton(mListener.showSignIn());
    	setDifficultyLevel(mListener.onDifficultyLevelRequested());

    	sbVolumeControl.setProgress(mListener.onVolumeProgressRequested());
    	
    	rgLevels.setOnCheckedChangeListener(this);
    	signInButton.setOnClickListener(this);
    	btSaveSettings.setOnClickListener(this);

    	sbVolumeControl.setOnSeekBarChangeListener(this);
    	
        return v;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_in_button:
			onGooglePlusButtonClicked();
			break;
		case R.id.imageButtonBack:
			mListener.onBackPressed();
			break;
		}
		
	}
	
	public void onGooglePlusButtonClicked() {
		if (mShowSignIn) {
			mListener.onSignInButtonClicked();
		} else {
			mListener.onSignOutButtonClicked();
		}
	}
	
	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	void updateUI() {
		if (mShowSignIn) {
			UIUtils.setGooglePlusButtonText(signInButton, "Sign in");
			mShowSignIn = true;
		} else {
			UIUtils.setGooglePlusButtonText(signInButton, "Sign out");
			mShowSignIn = false;
		}
		mListener.setShowSignIn(mShowSignIn);
	}
	
    public void setShowSignInButton(boolean showSignIn) {
    	this.mShowSignIn = showSignIn;
        updateUI();
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		this.progressChanged = progress;
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mListener.onChangeSoundVolume(progressChanged);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radioEasy:
			mListener.onSetDifficultyLevel(MainActivity.DIFFICULTY_LEVEL_EASY);
			break;
		case R.id.radioMedium:
			mListener.onSetDifficultyLevel(MainActivity.DIFFICULTY_LEVEL_MEDIUM);
			break;
		case R.id.radioHard:
			mListener.onSetDifficultyLevel(MainActivity.DIFFICULTY_LEVEL_HARD);
			break;
		}		
	}
	
	public void setDifficultyLevel(int difficulty) {
		rgLevels.clearCheck();
		
		switch (difficulty) {
		case MainActivity.DIFFICULTY_LEVEL_EASY:
			rbLevelEasy.setChecked(true);
			break;
		case MainActivity.DIFFICULTY_LEVEL_MEDIUM:
			rbLevelMedium.setChecked(true);
			break;
		case MainActivity.DIFFICULTY_LEVEL_HARD:
			rbLevelHard.setChecked(true);
			break;
		}
	}
}
