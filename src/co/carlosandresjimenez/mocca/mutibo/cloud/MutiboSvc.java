/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.cloud;

import co.carlosandresjimenez.mocca.mutibo.beans.Session;
import co.carlosandresjimenez.mocca.mutibo.oauth.SecuredRestBuilder;

import retrofit.RestAdapter.LogLevel;

public class MutiboSvc {

	public static final String CLIENT_ID = "mobile";
	
	public static final String SERVER = "https://stable-plasma-775.appspot.com";

	private static MutiboSvcApi videoSvc_;

	public static synchronized MutiboSvcApi connect(Session session) {

		videoSvc_ = new SecuredRestBuilder()
			 .setSession(session)
			.setEndpoint(SERVER)
			.setLogLevel(LogLevel.BASIC).build()
			.create(MutiboSvcApi.class);	
		
		return videoSvc_;
	}
}
