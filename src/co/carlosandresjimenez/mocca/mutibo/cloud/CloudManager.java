/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.cloud;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import co.carlosandresjimenez.mocca.mutibo.base.BaseApp;
import co.carlosandresjimenez.mocca.mutibo.beans.Answer;
import co.carlosandresjimenez.mocca.mutibo.beans.QuestionSet;
import co.carlosandresjimenez.mocca.mutibo.beans.Session;
import co.carlosandresjimenez.mocca.mutibo.beans.SyncNotifier;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

public class CloudManager implements Observer {

	private BaseApp base;

	public static final int QSET_SERVICE = 0;
	public static final int ANSWER_SERVICE = 1;
	public static final int SESSION_SERVICE = 2;
	public static final int GENERIC_SERVICE = 3;
	public static final int ERROR_RESPONSE_SERVICE = 4;
	
	/**
	 * The key used to store/retrieve a Messenger extra from a Bundle.
	 */
	public static final String MESSENGER_KEY = "MESSENGER";
	public static final String SERVICETYPE_KEY = "SERVICETYPE";
	public static final String ANSWER_KEY = "ANSWER";
	public static final String NEWSESSION_KEY = "NEW_SESSION";
	public static final String SESSION_KEY = "SESSION";
	public static final String RESPONSECODE_KEY = "RESPONSECODE";
	public static final String QSETLIST_KEY = "QSETLIST";
	public static final String DIFFICULTY_LEVEL_KEY = "DIFFICULTY";

	public static final int SAVE_SUCCESSFUL = 1;
	public static final int SAVE_UNSUCCESSFUL = -1;

	public static final String SAVE_ERROR_CODE = "000020";
	public static final String GET_QSET_ERROR_CODE = "0000021";
	public static final String INVALID_TOKEN_ERROR_CODE = "000022";
	
	private final static String LOG_TAG = CloudManager.class.getCanonicalName();

	public static final int NUMBER_OF_QSETS_TO_DOWNLOAD = 5;
	public static final int MINIMUM_QSETS_ON_MEMORY = 4;

	public CloudManager() {
		base = BaseApp.getInstance();
		base.getObserver().addObserver(this);
	}
	
	static class MessengerHandler extends Handler {

		// A weak reference to the enclosing class
		WeakReference<CloudManager> outerClass;

		/**
		 * A constructor that gets a weak reference to the enclosing class. We
		 * do this to avoid memory leaks during Java Garbage Collection.
		 * 
		 * @see https 
		 *      ://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk
		 *      /lIYDavGYn5UJ
		 */
		public MessengerHandler(CloudManager outer) {
			outerClass = new WeakReference<CloudManager>(outer);
		}

		// Handle any messages that get sent to this Handler
		@Override
		public void handleMessage(Message msg) {

			int serviceType = 0;
			final String responseCode;
			final CloudManager cloudManager = outerClass.get();
			final BaseApp base = BaseApp.getInstance();
			
			Handler mHandler = new Handler(base.getMainLooper());
		    
			if (cloudManager != null) {
				Bundle bundle = msg.getData();

				bundle.setClassLoader(QuestionSet.class.getClassLoader());
				serviceType = bundle.getInt(CloudManager.SERVICETYPE_KEY);
				
				switch (serviceType) {
				case CloudManager.QSET_SERVICE:
					
					Parcelable[] parcelableQSets = bundle
							.getParcelableArray(CloudManager.QSETLIST_KEY);

					final Queue<QuestionSet> qsets = new LinkedList<QuestionSet>();
					for (int i = 0; i < parcelableQSets.length; i++) {
						qsets.add((QuestionSet) parcelableQSets[i]);
					}

					mHandler.post(new Runnable() {
				        @Override
				        public void run() {
				        	base.addQSets(qsets);
				        }
				    });
					
					break;
					
				case CloudManager.ANSWER_SERVICE:
					responseCode = bundle.getString(CloudManager.RESPONSECODE_KEY);

					break;
					
				case CloudManager.SESSION_SERVICE:
					responseCode = bundle.getString(CloudManager.RESPONSECODE_KEY);

					if (base.getSession() != null){
						base.getSession().setClearCache(false);
						base.getSession().setSessionId(responseCode);
						
						mHandler.post(new Runnable() {
					        @Override
					        public void run() {
								base.startNewGame();
					        }
					    });
						
					}
					break;
				case CloudManager.ERROR_RESPONSE_SERVICE:
					responseCode = bundle.getString(CloudManager.RESPONSECODE_KEY);
					
					if (responseCode.equals(CloudManager.INVALID_TOKEN_ERROR_CODE)) {
						Log.e(LOG_TAG, "Invalid Session - back to login screen... ");
						
						mHandler.post(new Runnable() {
					        @Override
					        public void run() {
					        	base.invalidateSession(responseCode);
					        }
					    });
						
					} else {
						Log.e(LOG_TAG, "Error: " + responseCode);	
					}
				}
			}
		}
	}

	/**
	 * Instantiate the MessengerHandler, passing in the CloudManager to be
	 * stored as a WeakReference
	 */
	MessengerHandler handler = new MessengerHandler(this);

	public void runService(int serviceType) {
		switch (serviceType) {
		case CloudManager.QSET_SERVICE:
			base.startService(QSetIntentService.makeIntent(base, handler, base.getSession(), Integer.valueOf(base.getDifficultyLevel())));
			break;
		case CloudManager.ANSWER_SERVICE:

		}
	}

	public void runService(int serviceType, Object o) {
		
		switch (serviceType) {
		case CloudManager.QSET_SERVICE:

			break;
		case CloudManager.ANSWER_SERVICE:
			base.startService(AnswerIntentService.makeIntent(base, handler, base.getSession(),
					(Answer) o));
			break;
		case CloudManager.SESSION_SERVICE:
			base.startService(SessionIntentService.makeIntent(base, handler, null,
					(Session) o));
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		SyncNotifier syncNotifier = (SyncNotifier) observable;
		
		switch (syncNotifier.getErrorCode()) {
		case SyncNotifier.INVALID_SESSION_CODE:
			
			return;
		case SyncNotifier.NO_QSETS_RECEIVED_CODE:
			
			return;
		default:
			break;
		}
		
		if (syncNotifier.getNumberOfElements() < CloudManager.MINIMUM_QSETS_ON_MEMORY) {
			runService(CloudManager.QSET_SERVICE);
		}
	}
}
