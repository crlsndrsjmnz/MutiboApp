/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.beans;

import java.util.Observable;

public class SyncNotifier extends Observable {
	
	public static final int INVALID_SESSION_CODE = 1;
	public static final String INVALID_SESSION_MSG = "Invalid Session";	

	public static final int NO_QSETS_RECEIVED_CODE = 100;
	public static final String NO_QSETS_RECEIVED_MSG = "No Question Sets were received from the server";
	
	private int numberOfElements;
	private int errorCode;
	private String errorMessage;

	public SyncNotifier() {
		this.numberOfElements = 0;
		this.errorCode = 0;
		this.errorMessage = "";
	}

	public SyncNotifier(int numberOfElements) {
		this.numberOfElements = numberOfElements;
		this.errorCode = 0;
		this.errorMessage = "";
	}
	
	/**
	 * @param value
	 *            the value to set
	 */
	public void clearValues() {
		this.numberOfElements = 0;
		this.errorCode = 0;
		this.errorMessage = "";
	}

	/**
	 * @return the value
	 */
	public int getNumberOfElements() {
		return numberOfElements;
	}
	
	/**
	 * @param value
	 *            the value to set
	 */
	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	@Override
	public String toString() {
		return "SyncNotifier [numberOfElements=" + numberOfElements
				+ ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ "]";
	}
}
