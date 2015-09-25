/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.ui;

import java.util.Observable;
import java.util.Observer;

import co.carlosandresjimenez.mocca.mutibo.R;
import co.carlosandresjimenez.mocca.mutibo.beans.Answer;
import co.carlosandresjimenez.mocca.mutibo.beans.QuestionSet;
import co.carlosandresjimenez.mocca.mutibo.beans.Session;
import co.carlosandresjimenez.mocca.mutibo.beans.SyncNotifier;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends MainActivityBase implements Observer,
		MainMenuFragment.Listener, GamePlayFragment.Listener,
		SettingsFragment.Listener {

	private final static String LOG_TAG = MainActivity.class.getCanonicalName();
	
	final boolean retryObtainToken = true;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		qSetsLoaded = false;
		token_retries = 1;

		// create fragments
		mMainMenuFragment = new MainMenuFragment();
		mLoadingScreenFragment = new LoadIndicatorFragment();
		mGamePlayFragment = new GamePlayFragment();
		mSettingsFragment = new SettingsFragment();

		// listen to fragment events
		mMainMenuFragment.setListener(this);
		mGamePlayFragment.setListener(this);
		mSettingsFragment.setListener(this);

		// add initial fragment (welcome fragment)
		switchToMainMenu();
	}

	// Switch UI to the given fragment
	void switchToFragment(Fragment newFrag, String fragmentTag) {
		getSupportFragmentManager().beginTransaction()
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, newFrag, fragmentTag).commit();
	}

	@Override
	public void update(Observable observable, Object data) {

		SyncNotifier syncNotifier = (SyncNotifier) observable;

		switch (syncNotifier.getErrorCode()) {
		case SyncNotifier.INVALID_SESSION_CODE:
			qSetsLoaded = false;
			
			if (!isAtFragment(MAINMENU_FRAGMENT_TAG) && !isAtFragment(SETTINGS_FRAGMENT_TAG))
				switchToMainMenu();
			
			if (retryObtainToken && token_retries <= NUMBER_OF_TOKEN_RETRIES) {
				Log.e(LOG_TAG,"ERROR FOUND: Invalid session. RETRYING...");
				Toast.makeText(this, "Invalid session. Retrying...", Toast.LENGTH_LONG).show();
				
				token_retries++;
				invalidateAnGetNewToken();
			} else {
				Log.e(LOG_TAG,"ERROR FOUND: Invalid session. NOT RETRYING");
				Toast.makeText(this, "Invalid session. Please try again later.", Toast.LENGTH_LONG).show();
			}
			
			return;
		case SyncNotifier.NO_QSETS_RECEIVED_CODE:
			
			return;
		default:
			break;
		}
		
		if (!qSetsLoaded && soundStreamsLoaded >= 2 && syncNotifier.getNumberOfElements() > 0) {
			qSetsLoaded = true;
			
			if (isAtFragment(LOADING_FRAGMENT_TAG)) {
				switchToGameplay();
			}
		}
	}

	@Override
	public void onStartGameRequested() {
		
		if (!isOnline()) {
			Toast.makeText(this, "You require internet connection to use this game", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (isSignedIn()) {
			if (qSetsLoaded)
				switchToGameplay();
			else
				switchToLoadingScreen();
		} else {
			Toast.makeText(this, "Please Sign in", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onShowAchievementsRequested() {
	}

	@Override
	public void onShowLeaderboardsRequested() {
		onShowLeaderboards();
	}

	@Override
	public void onSignInButtonClicked() {
		signInToGooglePlus();
	}

	@Override
	public void onSignOutButtonClicked() {
		base.setSession(null);
		signOutOfGooglePlus();
	}

	@Override
	public Session onSessionRequested() {
		return base.getSession();
	}
	
	@Override
	public void onNewSessionRequested() {
	}	

	@Override
	public QuestionSet onQuestionSetRequested() {
		return base.getNextQSet();
	}

	@Override
	public void onSaveAnswerRequested(Answer answer) {
		base.saveAnswer(answer);
	}

	@Override
	public void onClearGameRequested() {
		token_retries = 1;
		base.clearGame();
	}

	@Override
	public void onShowMainActivityRequested() {
		qSetsLoaded = false;
		switchToMainMenu();
	}

	@Override
	public void onSaveFinalScoreRequested(long score) {
		onSaveFinalScore(score);
	}
	
	@Override
	public void onRevokeTokenRequested() {
	}
	
	public void switchToMainMenu() {
		switchToFragment(mMainMenuFragment, MAINMENU_FRAGMENT_TAG);
	}

	public void switchToLoadingScreen() {
		switchToFragment(mLoadingScreenFragment, LOADING_FRAGMENT_TAG);
	}

	public void switchToGameplay() {
		switchToFragment(mGamePlayFragment, GAMEPLAY_FRAGMENT_TAG);
	}
	
	public void switchToSettings() {
		switchToFragment(mSettingsFragment, SETTINGS_FRAGMENT_TAG);
	}
	
	@Override
	public void onInvalidateTokenRequested() {
		mTokenRequested = false;
		invalidateAnGetNewToken();
	}

	@Override
	public void onPlaySoundEffect(int soundType) {
		switch (soundType) {
		case SOUND_CORRECT:
			mSoundPool.play(mSoundIDCorrect, mStreamVolume, mStreamVolume, 1, 0, 1.0f);
			break;
		case SOUND_WRONG:
			mSoundPool.play(mSoundIDWrong, mStreamVolume, mStreamVolume, 1, 0, 1.0f);
			break;
		}
	}

	@Override
	public boolean showSignIn() {
		return mShowSignIn;
	}
	
	public void setShowSignIn(boolean mShowSignIn) {
		this.mShowSignIn = mShowSignIn;
	}

	@Override
	public void onShowMainmenuRequested() {
		switchToMainMenu();
	}

	@Override
	public void onChangeSoundVolume(int volumeIndex) {
		mStreamVolume = (float) (mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) * volumeIndex) / 100;
	}

	@Override
	public int onVolumeProgressRequested() {
		return (int) ((mStreamVolume * 100)/ mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
	}

	@Override
	public void onSetDifficultyLevel(int difficulty) {
		base.setDifficultyLevel(difficulty);
	}

	@Override
	public int onDifficultyLevelRequested() {
		return base.getDifficultyLevel();
	}

	@Override
	public void onShowSettingsRequested() {
		switchToSettings();
	}
	
	public void onBackPressed() {
		if (isAtFragment(MAINMENU_FRAGMENT_TAG)) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Exit")
					.setMessage("Are you sure you want to exit the game?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
	
							}).setNegativeButton("No", null).show();
		
		} else if (isAtFragment(GAMEPLAY_FRAGMENT_TAG)) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("End Game")
			.setMessage("Are you sure you want to end the game? Your score won't be saved.")
			.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							onClearGameRequested();
							onNewSessionRequested();
							onShowMainActivityRequested();
						}

					}).setNegativeButton("No", null).show();
		
		} else {
			switchToMainMenu();
		}
	}
}
