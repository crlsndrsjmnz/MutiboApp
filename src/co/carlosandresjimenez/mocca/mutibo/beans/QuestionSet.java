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

public class QuestionSet implements Parcelable {

	private Long setId;

	private String movieName1;
	private String movieName2;
	private String movieName3;
	private String movieName4;
	private int wrongMovieNum;
	private String questionType;
	private String reason;
	private int difficulty;
	private Answer userAnswer;

	public QuestionSet() {
	}

	public QuestionSet(Parcel orig) {
		this.setId = orig.readLong();
		this.movieName1 = orig.readString();
		this.movieName2 = orig.readString();
		this.movieName3 = orig.readString();
		this.movieName4 = orig.readString();
		this.wrongMovieNum = orig.readInt();
		this.questionType = orig.readString();
		this.reason = orig.readString();
		this.difficulty = orig.readInt();
	}

	public QuestionSet(String movieName1, String movieName2, String movieName3,
			String movieName4, int wrongMovieNum, String questionType, String reason, int difficulty) {
		super();
		this.movieName1 = movieName1;
		this.movieName2 = movieName2;
		this.movieName3 = movieName3;
		this.movieName4 = movieName4;
		this.wrongMovieNum = wrongMovieNum;
		this.questionType = questionType;
		this.reason = reason;
		this.difficulty = difficulty;
	}

	public Answer getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(Answer userAnswer) {
		this.userAnswer = userAnswer;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public Long getSetId() {
		return setId;
	}

	public void setSetId(Long setId) {
		this.setId = setId;
	}

	public String getMovieName1() {
		return movieName1;
	}

	public void setMovieName1(String movieName1) {
		this.movieName1 = movieName1;
	}

	public String getMovieName2() {
		return movieName2;
	}

	public void setMovieName2(String movieName2) {
		this.movieName2 = movieName2;
	}

	public String getMovieName3() {
		return movieName3;
	}

	public void setMovieName3(String movieName3) {
		this.movieName3 = movieName3;
	}

	public String getMovieName4() {
		return movieName4;
	}

	public void setMovieName4(String movieName4) {
		this.movieName4 = movieName4;
	}

	public int getWrongMovieNum() {
		return wrongMovieNum;
	}

	public void setWrongMovieNum(int wrongMovieNum) {
		this.wrongMovieNum = wrongMovieNum;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	/**
	 * Two Sets are considered equal if they have exactly the same values for
	 * their movieName1, movieName2, movieName3, movieName4, wrongMovieNum,
	 * reason.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof QuestionSet) {
			QuestionSet other = (QuestionSet) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(movieName1, other.movieName1)
					&& Objects.equal(movieName2, other.movieName2)
					&& Objects.equal(movieName3, other.movieName3)
					&& Objects.equal(movieName4, other.movieName4)
					&& wrongMovieNum == other.wrongMovieNum
					&& Objects.equal(reason, other.reason);
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
		dest.writeLong(setId);
		dest.writeString(movieName1);
		dest.writeString(movieName2);
		dest.writeString(movieName3);
		dest.writeString(movieName4);
		dest.writeInt(wrongMovieNum);
		dest.writeString(questionType);
		dest.writeString(reason);
		dest.writeInt(difficulty);
	}

	public static final Parcelable.Creator<QuestionSet> CREATOR = new Parcelable.Creator<QuestionSet>() {
		public QuestionSet createFromParcel(Parcel orig) {
			return new QuestionSet(orig);
		}

		public QuestionSet[] newArray(int size) {
			return new QuestionSet[size];
		}
	};

	@Override
	public String toString() {
		return "QuestionSet [setId=" + setId + ", movieName1=" + movieName1
				+ ", movieName2=" + movieName2 + ", movieName3=" + movieName3
				+ ", movieName4=" + movieName4 + ", wrongMovieNum="
				+ wrongMovieNum + ", questionType=" + questionType
				+ ", reason=" + reason + ", difficulty=" + difficulty
				+ ", userAnswer=" + userAnswer + "]";
	}



}
