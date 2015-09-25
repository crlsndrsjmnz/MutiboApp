/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class Answer implements Parcelable {

	private Long answerId;

	private Long setId;
	private int answerPoint;
	private String username;
	private int userRating;
	private String sessionId;
	
    public static final int WRONG_ANSWER = 0;
    public static final int CORRECT_ANSWER = 1;

	public Answer() {
	}

	public Answer(Parcel orig) {
		this.setId = orig.readLong();
		this.answerPoint = orig.readInt();
		this.username = orig.readString();
		this.userRating = orig.readInt();
		this.sessionId = orig.readString();
	}	
	
	public Answer(Long setId, int answerPoint, String username, int userRating, String sessionId) {
		super();
		this.setId = setId;
		this.answerPoint = answerPoint;
		this.username = username;
		this.userRating = userRating;
		this.sessionId = sessionId;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}

	public Long getSetId() {
		return setId;
	}

	public void setSetId(Long setId) {
		this.setId = setId;
	}

	public int getAnswerPoint() {
		return answerPoint;
	}

	public void setAnswerPoint(int answerPoint) {
		this.answerPoint = answerPoint;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getUserRating() {
		return userRating;
	}

	public void setUserRating(int userRating) {
		this.userRating = userRating;
	}

	/**
	 * Two Sets are considered equal if they have exactly the same values for
	 * their movieName1, movieName2, movieName3, movieName4, wrongMovieNum, 
	 * reason.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Answer) {
			Answer other = (Answer) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(setId, other.setId)
					&& Objects.equal(answerPoint, other.answerPoint)
					&& Objects.equal(username, other.username)
					&& Objects.equal(userRating, other.userRating)
					&& Objects.equal(sessionId, other.sessionId);
		} else {
			return false;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeLong(setId);
		dest.writeInt(answerPoint);
		dest.writeString(username);
		dest.writeInt(userRating);
		dest.writeString(sessionId);
	}

	public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
		public Answer createFromParcel(Parcel orig) {
			return new Answer(orig);
		}

		public Answer[] newArray(int size) {
			return new Answer[size];
		}
	};	

}
