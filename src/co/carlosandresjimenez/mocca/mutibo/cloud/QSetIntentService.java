/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.cloud;

import co.carlosandresjimenez.mocca.mutibo.beans.Session;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Messenger;

public class QSetIntentService extends IntentService {

	/**
     * The default constructor for this service. Simply forwards
     * construction to IntentService, passing in a name for the Thread
     * that the service runs in.
     */
    public QSetIntentService() { 
        super("IntentService Worker Thread"); 
    }

    /**
     * Optionally allow the instantiator to specify the name of the
     * thread this service runs in.
     */
    public QSetIntentService(String name) {
        super(name);
    }

    public static Intent makeIntent(Context context,
                                    Handler handler,
                                    Session session,
                                    Object obj) {
    	
        return DownloadUtils.makeMessengerIntent(context, 
        										 QSetIntentService.class, 
        										 handler,
        										 session,
        										 obj);
    }

    /**
     * Hook method called when a component calls startService() with
     * the proper intent.  This method serves as the Executor in the
     * Command Processor Pattern. It receives an Intent, which serves
     * as the Command, and executes some action based on that intent
     * in the context of this service.
     * 
     * This method is also a Hook Method in the Template Method
     * Pattern. The Template class has an overall design goal and
     * strategy, but it allows subclasses to how some steps in the
     * strategy are implemented. For example, IntentService handles
     * the creation and lifecycle of a started service, but allows a
     * user to define what happens when an Intent is actually handled.
     */
    @Override
    protected void onHandleIntent (Intent intent) {
    	
    	DownloadUtils.downloadAndRespond(QSetIntentService.this,
    									 (Session) intent.getExtras().get(CloudManager.SESSION_KEY),
    			    				     intent.getData(), 
    			    				     (Messenger) intent.getExtras().get(CloudManager.MESSENGER_KEY),
    			    				     (Integer) intent.getExtras().get(CloudManager.DIFFICULTY_LEVEL_KEY));
    	
    }
}
