/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.base;

import java.util.LinkedList;
import java.util.Queue;

import co.carlosandresjimenez.mocca.mutibo.beans.Answer;
import co.carlosandresjimenez.mocca.mutibo.beans.QuestionSet;
import co.carlosandresjimenez.mocca.mutibo.beans.Session;
import co.carlosandresjimenez.mocca.mutibo.beans.SyncNotifier;
import co.carlosandresjimenez.mocca.mutibo.cloud.CloudManager;
import android.app.Application;
import android.os.Build;
import android.util.Log;

public class BaseApp extends Application {

	SyncNotifier syncNotifier;
	CloudManager cloudManager;
	Queue<QuestionSet> qsets;
	Session session;
	int difficultyLevel;
	
	private final static String LOG_TAG = BaseApp.class.getCanonicalName();

	private static BaseApp singleton;

	// Returns the application instance
	public static BaseApp getInstance() {
		return singleton;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;

		// This was added to fix the issue with the IntentService detailed here:
		// http://stackoverflow.com/questions/4280330/onpostexecute-not-being-called-in-asynctask-handler-runtime-exception
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
			try {
				Class.forName("android.os.AsyncTask");
			} catch (ClassNotFoundException e) {
				Log.e(LOG_TAG, "Class android.os.AsyncTask not found!!!");
				e.printStackTrace();
			}
		}
		
		difficultyLevel = 1;
		syncNotifier = new SyncNotifier();
		cloudManager = new CloudManager();	
	}
	
	public void startNewGame() {
		qsets = new LinkedList<QuestionSet>();
		syncNotifier.clearValues();
		syncNotifier.notifyObservers();
	}
	
	public void clearGame() {
		qsets = null;
		syncNotifier.clearValues();
		session.setClearCache(true);
		saveSession();
	}
	
	public SyncNotifier getObserver() {
		return syncNotifier;
	}

	public void addQSets(Queue<QuestionSet> qsets) {
		if (this.qsets == null || qsets.size() == 0) {
			syncNotifier.setErrorMessage(SyncNotifier.NO_QSETS_RECEIVED_MSG);
			syncNotifier.setErrorCode(SyncNotifier.NO_QSETS_RECEIVED_CODE);
		} else {
			this.qsets.addAll(qsets);
			syncNotifier.setNumberOfElements(qsets.size());
		}
		syncNotifier.notifyObservers();
	}

	public QuestionSet getNextQSet() {
		QuestionSet qset = this.qsets.poll();
		syncNotifier.setNumberOfElements(qsets.size());
		syncNotifier.notifyObservers();
		return qset;
	}

	public void saveQSet(QuestionSet qset) {
		cloudManager.runService(CloudManager.QSET_SERVICE, qset);
	}	
	
	public void saveAnswer(Answer answer) {
		cloudManager.runService(CloudManager.ANSWER_SERVICE, answer);
	}
	
	public void saveSession() {
		cloudManager.runService(CloudManager.SESSION_SERVICE, session);
	}	

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public void invalidateSession(String errorMessage) {
		this.session = null;
		syncNotifier.setErrorMessage(SyncNotifier.INVALID_SESSION_MSG);
		syncNotifier.setErrorCode(SyncNotifier.INVALID_SESSION_CODE);
		syncNotifier.notifyObservers();
	}

	public int getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}
}
