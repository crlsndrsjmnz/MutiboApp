/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.oauth;

import java.util.concurrent.Executor;

import co.carlosandresjimenez.mocca.mutibo.beans.Session;

import retrofit.Endpoint;
import retrofit.ErrorHandler;
import retrofit.Profiler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Log;
import retrofit.RestAdapter.LogLevel;
import retrofit.converter.Converter;

public class SecuredRestBuilder extends RestAdapter.Builder {

	private class OAuthHandler implements RequestInterceptor {

		private String username;
		private String sessionId;

		public OAuthHandler(String username, String sessionId) {
			super();
			this.username = username;
			this.sessionId = sessionId;
		}

		/**
		 * Every time a method on the client interface is invoked, this method is
		 * going to get called. The method checks adds a Username and the session key
		 * to the request headers. 
		 */
		@Override
		public void intercept(RequestFacade request) {
			request.addHeader("Username", username );
			request.addHeader("SessionId", sessionId );
		}

	}

	private String username;
	private String sessionId;
	private boolean createSession;
	
	@Override
	public SecuredRestBuilder setEndpoint(String endpoint) {
		return (SecuredRestBuilder) super.setEndpoint(endpoint);
	}

	@Override
	public SecuredRestBuilder setEndpoint(Endpoint endpoint) {
		return (SecuredRestBuilder) super.setEndpoint(endpoint);
	}

	@Override
	public SecuredRestBuilder setErrorHandler(ErrorHandler errorHandler) {

		return (SecuredRestBuilder) super.setErrorHandler(errorHandler);
	}

	@Override
	public SecuredRestBuilder setExecutors(Executor httpExecutor,
			Executor callbackExecutor) {

		return (SecuredRestBuilder) super.setExecutors(httpExecutor,
				callbackExecutor);
	}

	@Override
	public SecuredRestBuilder setRequestInterceptor(
			RequestInterceptor requestInterceptor) {

		return (SecuredRestBuilder) super
				.setRequestInterceptor(requestInterceptor);
	}

	@Override
	public SecuredRestBuilder setConverter(Converter converter) {

		return (SecuredRestBuilder) super.setConverter(converter);
	}

	@Override
	public SecuredRestBuilder setProfiler(@SuppressWarnings("rawtypes") Profiler profiler) {

		return (SecuredRestBuilder) super.setProfiler(profiler);
	}

	@Override
	public SecuredRestBuilder setLog(Log log) {

		return (SecuredRestBuilder) super.setLog(log);
	}

	@Override
	public SecuredRestBuilder setLogLevel(LogLevel logLevel) {

		return (SecuredRestBuilder) super.setLogLevel(logLevel);
	}

	public SecuredRestBuilder setSession(Session session) {
		if (session != null && session.getSessionId() != null) {
			this.username = session.getUsername();
			this.sessionId = session.getSessionId();
			this.createSession = false;
		} else {
			this.createSession = true;
		}
		return this;
	}

	@Override
	public RestAdapter build() {
		
		if (!createSession) {
			if (username == null || sessionId == null) {
				throw new SecuredRestException(
						"You must specify both a username and sessionId for a "
								+ "SecuredRestBuilder before calling the build() method.");
			}	
		}
		
		OAuthHandler hdlr = new OAuthHandler(username, sessionId);
		setRequestInterceptor(hdlr);
		
		return super.build();
	}
}