/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.cloud;

import co.carlosandresjimenez.mocca.mutibo.beans.Answer;
import co.carlosandresjimenez.mocca.mutibo.beans.Session;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Messenger;

/**
 * @class AnswerIntentService
 *
 * @brief This class extends the IntentService, which provides a framework that
 *        simplifies programming and processing Android Started Services
 *        concurrently.
 * 
 *        AnswerIntentService receives an Intent containing a Answer
 *        and a Messenger (which is an IPC mechanism). It saves it
 *        into the cloud, then returns a message error by using the supplied Messenger.
 * 
 *        The IntentService class implements the CommandProcessor pattern and
 *        the Template Method Pattern. The Messenger is used as part of the
 *        Active Object pattern.
 */
public class AnswerIntentService extends IntentService {

	/**
	 * The default constructor for this service. Simply forwards construction to
	 * IntentService, passing in a name for the Thread that the service runs in.
	 */
	public AnswerIntentService() {
		super("IntentService Worker Thread");
	}

	/**
	 * Optionally allow the instantiator to specify the name of the thread this
	 * service runs in.
	 */
	public AnswerIntentService(String name) {
		super(name);
	}

	public static Intent makeIntent(Context context, Handler handler, Session currSession,
			Answer answer) {

		return DownloadUtils.makeMessengerIntent(context,
				AnswerIntentService.class, handler, currSession, answer);
	}

	/**
	 * Hook method called when a component calls startService() with the proper
	 * intent. This method serves as the Executor in the Command Processor
	 * Pattern. It receives an Intent, which serves as the Command, and executes
	 * some action based on that intent in the context of this service.
	 * 
	 * This method is also a Hook Method in the Template Method Pattern. The
	 * Template class has an overall design goal and strategy, but it allows
	 * subclasses to how some steps in the strategy are implemented. For
	 * example, IntentService handles the creation and lifecycle of a started
	 * service, but allows a user to define what happens when an Intent is
	 * actually handled.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		DownloadUtils.saveAndRespond(
				(Session) intent.getExtras().get(CloudManager.SESSION_KEY),
				CloudManager.ANSWER_SERVICE,
				(Messenger) intent.getExtras().get(CloudManager.MESSENGER_KEY),
				(Answer) intent.getExtras().get(CloudManager.ANSWER_KEY));

	}
}
