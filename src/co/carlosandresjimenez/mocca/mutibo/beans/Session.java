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

public class Session implements Parcelable {

	private String sessionId;
	private String username;
	private String datetime;
	private String device;
	private String ipAddress;
	private String token;
	private Boolean clearCache;

	public Session() {
	}

	public Session(Parcel orig) {
		this.sessionId = orig.readString();
		this.username = orig.readString();
		this.datetime = orig.readString();
		this.device = orig.readString();
		this.ipAddress = orig.readString();
		this.token = orig.readString();
		this.clearCache = orig.readByte() != 0;
	}

	public Session(String sessionId, String username, String datetime,
			String device, String ipAddress, String token, Boolean clearCache,
			String errorMsg) {
		super();
		this.sessionId = sessionId;
		this.username = username;
		this.datetime = datetime;
		this.device = device;
		this.ipAddress = ipAddress;
		this.token = token;
		this.clearCache = clearCache;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Boolean getClearCache() {
		return clearCache;
	}

	public void setClearCache(Boolean clearCache) {
		this.clearCache = clearCache;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String toString() {
		return ":::Session:::" + "\n sessionId: " + sessionId + "\n username: "
				+ username + "\n datetime: " + datetime + "\n device: "
				+ device + "\n ipAddress: " + ipAddress + "\n token: " + token
				+ "\n clearCache: " + clearCache;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sessionId);
		dest.writeString(username);
		dest.writeString(datetime);
		dest.writeString(device);
		dest.writeString(ipAddress);
		dest.writeString(token);
		dest.writeByte((byte) (clearCache ? 1 : 0));
	}

	public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {
		public Session createFromParcel(Parcel orig) {
			return new Session(orig);
		}

		public Session[] newArray(int size) {
			return new Session[size];
		}
	};
}
