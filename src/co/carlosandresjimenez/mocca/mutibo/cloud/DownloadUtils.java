/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.cloud;

import java.util.Collection;
import java.util.concurrent.Callable;

import co.carlosandresjimenez.mocca.mutibo.beans.Answer;
import co.carlosandresjimenez.mocca.mutibo.beans.QuestionSet;
import co.carlosandresjimenez.mocca.mutibo.beans.Session;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * @class DownloadUtils
 *
 * @brief This class encapsulates several static methods so that all Services
 *        can access them without redefining them in each Service.
 */
public class DownloadUtils {

	public static Intent makeMessengerIntent(Context context, 
												Class<?> service,
												Handler handler, 
												Session currSession, 
												Object o) {

		Messenger messenger = new Messenger(handler);
		Intent intent = new Intent(context, service);

		intent.putExtra(CloudManager.MESSENGER_KEY, messenger);
		intent.putExtra(CloudManager.SESSION_KEY, currSession);

		if (o instanceof QuestionSet) {
			intent.putExtra(CloudManager.QSETLIST_KEY, (QuestionSet) o);
		} else if (o instanceof Answer) {
			intent.putExtra(CloudManager.ANSWER_KEY, (Answer) o);
		} else if (o instanceof Session) {
			intent.putExtra(CloudManager.NEWSESSION_KEY, (Session) o);
		} else if (o instanceof Integer) {
			intent.putExtra(CloudManager.DIFFICULTY_LEVEL_KEY, (Integer) o);
		}

		return intent;
	}

	public static void sendPath(Collection<QuestionSet> qsets,
									Messenger messenger) {

		Message msg = Message.obtain();

		QuestionSet[] qsetsArray = new QuestionSet[qsets.size()];
		qsets.toArray(qsetsArray);

		Bundle data = new Bundle();
		data.putInt(CloudManager.SERVICETYPE_KEY, CloudManager.QSET_SERVICE);
		data.putParcelableArray(CloudManager.QSETLIST_KEY, qsetsArray);

		// Make the Bundle the "data" of the Message.
		msg.setData(data);

		try {
			// Send the Message back to the client Activity.
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void downloadAndRespond(Context context, 
											Session currSession,
											Uri uri, 
											Messenger messenger,
											Integer difficulty) {
		
		DownloadUtils.getQsets(messenger, currSession, difficulty);
	}

	public static Collection<QuestionSet> getQsets(Messenger messenger,
														Session currSession,
														Integer difficulty) {

		final MutiboSvcApi lConnection = MutiboSvc.connect(currSession);
		final Messenger lMessenger = messenger;
		final String lSessionId = currSession.getSessionId();
		final Integer lDifficulty = difficulty;

		if (lConnection != null) {
			CallableTask.invoke(new Callable<Collection<QuestionSet>>() {

				@Override
				public Collection<QuestionSet> call() throws Exception {
					return lConnection.getSetList(lSessionId, CloudManager.NUMBER_OF_QSETS_TO_DOWNLOAD, true, lDifficulty); 
				}
			}, new TaskCallback<Collection<QuestionSet>>() {

				@Override
				public void success(Collection<QuestionSet> result) {
					sendPath(result, lMessenger);
				}

				@Override
				public void error(Exception e) {
					sendResponseCode(CloudManager.ERROR_RESPONSE_SERVICE, CloudManager.GET_QSET_ERROR_CODE, lMessenger);
				}
			});

		}

		return null;
	}

	public static int saveAndRespond(Session currSession, 
										int serviceType,
										Messenger messenger, 
										Object obj) {

		final MutiboSvcApi svc = MutiboSvc.connect(currSession);
		final Messenger msg = messenger;
		final Object object = obj;
		final int iServiceType = serviceType;
		
		if (svc != null) {
			CallableTask.invoke(new Callable<String>() {

				@Override
				public String call() throws Exception {
					if (object instanceof Answer)
						return String.valueOf((svc.addAnswer((Answer) object)));
					else if (object instanceof Session) {
						String result = svc.saveSession((Session) object);
						
						if (result != null && !result.equals("INVALID_TOKEN"))
							return result;
						else
							throw new Exception(CloudManager.INVALID_TOKEN_ERROR_CODE);

					} else
						throw new Exception("UNIDENTIFIED FLYNG OBJECT");
				}
			}, new TaskCallback<String>() {

				@Override
				public void success(String result) {
					sendResponseCode(iServiceType, result, msg);
				}

				@Override
				public void error(Exception e) {
					sendResponseCode(CloudManager.ERROR_RESPONSE_SERVICE, e.getMessage(), msg);
				}
			});

		}

		return 1;
	}

	public static void sendResponseCode(int serviceType, 
											String responseCode,
											Messenger messenger) {

		Message msg = Message.obtain();

		Bundle data = new Bundle();

		data.putInt(CloudManager.SERVICETYPE_KEY, serviceType);
		data.putString(CloudManager.RESPONSECODE_KEY, responseCode);

		// Make the Bundle the "data" of the Message.
		msg.setData(data);

		try {
			// Send the Message back to the client Activity.
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
