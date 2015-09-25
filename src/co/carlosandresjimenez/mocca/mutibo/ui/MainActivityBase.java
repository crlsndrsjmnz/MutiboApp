/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

import co.carlosandresjimenez.mocca.mutibo.R;
import co.carlosandresjimenez.mocca.mutibo.base.BaseApp;
import co.carlosandresjimenez.mocca.mutibo.cloud.CallableTask;
import co.carlosandresjimenez.mocca.mutibo.cloud.TaskCallback;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MainActivityBase extends FragmentActivity implements Observer,
		ConnectionCallbacks, OnConnectionFailedListener {

	private final static String LOG_TAG = MainActivityBase.class
			.getCanonicalName();

	public static final int NUMBER_OF_TOKEN_RETRIES = 1;

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_TOKEN_AUTH = 100;
	private static final int RC_PLAYSERVICES_ERROR = 101;
	private static final int RC_SIGN_IN = 102;
	private static final int RC_UNUSED = 103;

	private final static List<String> GPLUS_API_SCOPES = Arrays
			.asList(new String[] { "https://www.googleapis.com/auth/plus.login" });

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	protected BaseApp base;
	boolean qSetsLoaded;
	boolean isOnline = true;

	protected int token_retries;
	protected int soundStreamsLoaded = 0;
	protected String sAuthToken = "";

	// Fragments
	protected MainMenuFragment mMainMenuFragment;
	protected LoadIndicatorFragment mLoadingScreenFragment;
	protected GamePlayFragment mGamePlayFragment;
	protected SettingsFragment mSettingsFragment;

	protected static final String MAINMENU_FRAGMENT_TAG = "MainMenuFragment";
	protected static final String LOADING_FRAGMENT_TAG = "LoadingScreenFragment";
	protected static final String GAMEPLAY_FRAGMENT_TAG = "GamePlayFragment";
	protected static final String SETTINGS_FRAGMENT_TAG = "SettingsFragment";

	protected static final int DIFFICULTY_LEVEL_EASY = 1;
	protected static final int DIFFICULTY_LEVEL_MEDIUM = 2;
	protected static final int DIFFICULTY_LEVEL_HARD = 3;

	/*
	 * Track whether the sign-in button has been clicked so that we know to
	 * resolve all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;
	protected boolean mShowSignIn = false;

	// Automatically start the sign-in flow when the Activity starts
	private boolean mAutoStartSignInFlow = true;

	boolean mTokenRequested;

	/*
	 * Store the connection result from onConnectionFailed callbacks so that we
	 * can resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	/*
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	// AudioManager
	protected AudioManager mAudioManager;
	// SoundPool
	protected SoundPool mSoundPool;
	// ID for the bubble popping sound
	protected int mSoundIDCorrect;
	protected int mSoundIDWrong;

	// Audio volume
	protected float mStreamVolume;

	static final int SOUND_CORRECT = 1;
	static final int SOUND_WRONG = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		buildApiClient();
	}

	public void initObserver() {
		base = (BaseApp) getApplication();
		base.getObserver().addObserver(this);
	}

	public void initSoundsEfects() {

		soundStreamsLoaded = 0;
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		mStreamVolume = (float) mAudioManager
				.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
				/ mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);

		// make a new SoundPool, allowing up to 10 streams
		mSoundPool = new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 0);

		mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {

				soundStreamsLoaded++;
			}
		});

		mSoundIDCorrect = mSoundPool.load(this, R.raw.correct_answer, 1);
		mSoundIDWrong = mSoundPool.load(this, R.raw.wrong_answer, 1);
	}

	public void buildApiClient() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).addApi(Plus.API)
					.addScope(Plus.SCOPE_PLUS_LOGIN).addApi(Games.API)
					.addScope(Games.SCOPE_GAMES).build();
		}
	}

	public void onResume() {
		initObserver();

		initSoundsEfects();

		if (base.getObserver() != null) {
			base.getObserver().addObserver(this);
		}

		super.onResume();
	}

	public void onPause() {
		// Release all SoundPool resources
		if (mSoundPool != null) {
			mSoundPool.unload(mSoundIDCorrect);
			mSoundPool.unload(mSoundIDWrong);
			mSoundPool.release();
			mSoundPool = null;
		}
		mAudioManager.unloadSoundEffects();

		if (base.getObserver() != null) {
			base.getObserver().deleteObserver(this);
		}

		super.onPause();
	}

	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void update(Observable observable, Object data) {
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Sign-in into google
	 * */
	void signInToGooglePlus() {

		if (!mGoogleApiClient.isConnecting()) {
			mGoogleApiClient.connect();
			mSignInClicked = true;
		}

		updateSignInStatusUI(false);
	}

	void signOutOfGooglePlus() {

		mSignInClicked = false;
		Games.signOut(mGoogleApiClient);

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}

		updateSignInStatusUI(true);
	}

	public boolean isSignedIn() {
		return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		Log.d(LOG_TAG, "resolveSignInError()");

		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;

				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		} else {
			updateSignInStatusUI(true);
		}

	}

	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked || mAutoStartSignInFlow) {
				mAutoStartSignInFlow = false;
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		switch (requestCode) {
		case RC_SIGN_IN:
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
				updateSignInStatusUI(true);
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
			break;
		case RC_TOKEN_AUTH:
			if (responseCode == RESULT_OK) {
				requestTokenTask();
			}
			break;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mSignInClicked = false;
		Log.d(LOG_TAG, "User is connected!");

		updateSignInStatusUI(false);

		if (!isOnline()) {
			Log.e(LOG_TAG, "Internet connection required to use this game");
			Toast.makeText(this,
					"You require internet connection to use this game",
					Toast.LENGTH_LONG).show();
			isOnline = false;
			return;
		}

		if (!isSignedIn()) {
			Log.e(LOG_TAG, "Not Signed in");
		}

		if (!mTokenRequested) {
			mTokenRequested = true;
			requestTokenTask();
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}

	public String getAuthTokenBlocking() {
		String token = null;
		String username = null;
		String scope = null;

		try {

			if (isSignedIn()) {
				username = Plus.AccountApi.getAccountName(mGoogleApiClient);
				scope = "oauth2:" + TextUtils.join(" ", GPLUS_API_SCOPES);

				token = GoogleAuthUtil.getToken(MainActivityBase.this,
						username, scope);

				base.setSession(UIUtils.createSession(username, token));
			}

		} catch (IOException transientEx) {
			// Network or server error, try later
			transientEx.printStackTrace();
			Log.e(LOG_TAG, transientEx.toString());
		} catch (UserRecoverableAuthException e) {
			// Recover (with e.getIntent())
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
			Intent recover = e.getIntent();
			startActivityForResult(recover, RC_TOKEN_AUTH);
		} catch (GoogleAuthException authEx) {
			// The call is not ever expected to succeed
			// assuming you have already verified that
			// Google Play services is installed.
			authEx.printStackTrace();
			Log.e(LOG_TAG, authEx.toString());
		}

		return token;
	}

	public void invalidateTokenBlocking() {
		Log.d(LOG_TAG, "Invalidating Token... " + sAuthToken);

		try {
			if (isSignedIn())
				GoogleAuthUtil.clearToken(MainActivityBase.this, sAuthToken);
			else
				Log.e(LOG_TAG, "invalidateTokenBlocking() NOT SIGNED IN");

		} catch (IOException transientEx) {
			// Network or server error, try later
			transientEx.printStackTrace();
			Log.e(LOG_TAG, transientEx.toString());
		} catch (GooglePlayServicesAvailabilityException playEx) {
			// Recover (with e.getIntent())
			playEx.printStackTrace();
			Log.e(LOG_TAG, playEx.toString());

			GooglePlayServicesUtil.getErrorDialog(
					playEx.getConnectionStatusCode(), MainActivityBase.this,
					RC_PLAYSERVICES_ERROR);
		} catch (GoogleAuthException authEx) {
			// The call is not ever expected to succeed assuming you
			// have already
			// verified that Google Play services is installed.
			authEx.printStackTrace();
			Log.e(LOG_TAG, authEx.toString());
		}
	}

	public void revokeToken() {
		if (isSignedIn()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(LOG_TAG,
									"User access revoked!"
											+ arg0.getStatusCode() + "  "
											+ arg0.getStatusMessage());
							buildApiClient();
							mGoogleApiClient.connect();
							updateSignInStatusUI(true);
						}

					});
		}

	}

	public void requestTokenTask() {
		CallableTask.invoke(new Callable<String>() {

			@Override
			public String call() throws Exception {
				return getAuthTokenBlocking();
			}
		}, new TaskCallback<String>() {

			@Override
			public void success(String result) {
				if (result != null && !result.equals("")) {
					base.saveSession();
					mTokenRequested = true;
				} else {
					Log.e(LOG_TAG, "Error retrieving token from Google");
				}
				
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void invalidateAnGetNewToken() {
		CallableTask.invoke(new Callable<String>() {

			@Override
			public String call() throws Exception {
				invalidateTokenBlocking();

				return getAuthTokenBlocking();
			}

		}, new TaskCallback<String>() {

			@Override
			public void success(String result) {
				if (result != null && !result.equals("")) {
					base.saveSession();
					mTokenRequested = true;
				} else {
					Log.e(LOG_TAG, "Error retrieving a new token from Google");
				}
			}

			@Override
			public void error(Exception e) {
				e.printStackTrace();
			}
		});
	}

	public boolean isAtFragment(String fragmentTag) {
		Fragment myFragment = getSupportFragmentManager().findFragmentByTag(
				fragmentTag);

		if (myFragment != null) {
			return myFragment.isVisible();
		} else {
			return false;
		}
	}

	public void onSaveFinalScore(long score) {
		if (isSignedIn()) {
			Games.Leaderboards.submitScore(mGoogleApiClient,
					getString(R.string.leaderboard_general), score);
		} else {
			Log.e(LOG_TAG, "User not signed in!");
		}
	}

	public void onShowLeaderboards() {
		if (isSignedIn()) {
			startActivityForResult(
					Games.Leaderboards
							.getAllLeaderboardsIntent(mGoogleApiClient),
					RC_UNUSED);
		} else {
			Log.e(LOG_TAG, "Leaderboards not available");
		}
	}

	public void updateSignInStatusUI(boolean mShowSignIn) {
		this.mShowSignIn = mShowSignIn;

		if (isAtFragment(MAINMENU_FRAGMENT_TAG)) {
			mMainMenuFragment.setShowSignInButton(mShowSignIn);
		} else if (isAtFragment(SETTINGS_FRAGMENT_TAG)) {
			mSettingsFragment.setShowSignInButton(mShowSignIn);
		}
	}
}
