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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainMenuFragment extends Fragment implements OnClickListener {

    public interface Listener {
        public void onStartGameRequested();
        public void onShowAchievementsRequested();
        public void onShowLeaderboardsRequested();
        public void onSignInButtonClicked();
        public void onSignOutButtonClicked();
        public void onRevokeTokenRequested();
        public void onInvalidateTokenRequested();
        public boolean showSignIn();
        public boolean isSignedIn();
        public void setShowSignIn(boolean mShowSignIn);
        public void onShowSettingsRequested();
    }
    
	Listener mListener = null;
	
	private Button startGameButton;
	private Button btLeaderboards;
	private Button btSettings;
	private Button btRevoke;
	private Button btInvalidate;
	private SignInButton signInButton;
	private ProgressBar loadingSignIn;
	
	boolean mShowSignIn;
	
    public void setListener(Listener l) {
        mListener = l;
    }	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mainmenu, container, false);

    	startGameButton = (Button) v.findViewById(R.id.button_start_game);
    	btLeaderboards = (Button) v.findViewById(R.id.button_leaderboards);
    	signInButton = (SignInButton) v.findViewById(R.id.sign_in_button);
    	btSettings = (Button) v.findViewById(R.id.button_settings);
    	loadingSignIn = (ProgressBar) v.findViewById(R.id.progressBarSignIn);
    	
    	btRevoke = (Button) v.findViewById(R.id.button_revoke);
    	btInvalidate = (Button) v.findViewById(R.id.button_invalidate);
    	
    	btRevoke.setVisibility(Button.GONE);
    	btInvalidate.setVisibility(Button.GONE);

    	startGameButton.setOnClickListener(this);
    	btLeaderboards.setOnClickListener(this);
    	btSettings.setOnClickListener(this);
    	signInButton.setOnClickListener(this);
        
    	setShowSignInButton(mListener.showSignIn());
    	
        return v;
    }
    
	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	void updateUI() {
		if (mShowSignIn) {
			UIUtils.setGooglePlusButtonText(signInButton, "Sign in");
			mShowSignIn = true;
		} else {
			signInButton.setVisibility(SignInButton.INVISIBLE);
			mShowSignIn = false;
			
			if (mListener.isSignedIn()) {
				loadingSignIn.setVisibility(ProgressBar.GONE);
				startGameButton.setEnabled(true);			
			}
		}
		
		mListener.setShowSignIn(mShowSignIn);
	}
    
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_in_button:
			onGooglePlusButtonClicked();
			break;
		case R.id.button_start_game:
			mListener.onStartGameRequested();
			break;
		case R.id.button_settings:
			mListener.onShowSettingsRequested();
			break;
		case R.id.button_leaderboards:
			mListener.onShowLeaderboardsRequested();
			break;
		case R.id.button_revoke:
			mListener.onRevokeTokenRequested();
			break;
		case R.id.button_invalidate:
			mListener.onInvalidateTokenRequested();
			break;
		}
	}
	
    public void setShowSignInButton(boolean showSignIn) {
        this.mShowSignIn = showSignIn;
        updateUI();
    }
    
	public void onGooglePlusButtonClicked() {
		if (mShowSignIn) {
			mListener.onSignInButtonClicked();
		} else {
			mListener.onSignOutButtonClicked();
		}
	}
}
