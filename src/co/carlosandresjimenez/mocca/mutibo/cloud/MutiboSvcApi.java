/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.cloud;

import java.util.Collection;

import co.carlosandresjimenez.mocca.mutibo.beans.Answer;
import co.carlosandresjimenez.mocca.mutibo.beans.QuestionSet;
import co.carlosandresjimenez.mocca.mutibo.beans.Session;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * This interface defines an API for a VideoSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * 
 * 
 * @author Carlos Andres Jimenez 
 *
 */
public interface MutiboSvcApi {
	
	public static final String SESSIONID_PARAMETER = "id";	
	public static final String QSETNUM_PARAMETER = "num";
	public static final String QSETSTART_PARAMETER = "start";
	public static final String RANDOM_PARAMETER = "random";
	public static final String DIFFICULTY_PARAMETER = "difficulty";	

	public static final String QSET_PATH = "/qset";
	public static final String QSET_FIND_ALL = QSET_PATH + "/all";
	public static final String QSET_DELETE_ALL = QSET_PATH + "/deleteall";

	public static final String ANSWER_PATH = "/answer";
	
	public static final String TOKEN_PATH = "/oauth/token";
	public static final String SESSION_PATH = "/oauth/session";
	
	public static final int MAX_DIFFICULTY_LEVEL = 3;	

	@GET(QSET_PATH)
	public Collection<QuestionSet> getSetList(
			@Query(SESSIONID_PARAMETER) String sessionId,
			@Query(QSETNUM_PARAMETER) int numberOfQSets,
			@Query(RANDOM_PARAMETER) boolean randomize,
			@Query(DIFFICULTY_PARAMETER) int difficulty);
	
	@GET(QSET_FIND_ALL)
	public Collection<QuestionSet> getSetList();
	
	@POST(QSET_PATH)
	public boolean addSet(@Body QuestionSet v);

	@GET(QSET_DELETE_ALL)
	public boolean removeAll();	

	@GET(ANSWER_PATH)
	public Collection<Answer> getAnswerList();
	
	@POST(ANSWER_PATH)
	public int addAnswer(@Body Answer a);
	
	@POST(SESSION_PATH)
	public String saveSession(@Body Session s);	
}





