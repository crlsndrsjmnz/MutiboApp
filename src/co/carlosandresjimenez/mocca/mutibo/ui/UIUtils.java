/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;
import co.carlosandresjimenez.mocca.mutibo.beans.Session;

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class UIUtils {

	public static void setGooglePlusButtonText(SignInButton signInButton,
			String buttonText) {
		// Find the TextView that is inside of the SignInButton and set its text
		for (int i = 0; i < signInButton.getChildCount(); i++) {
			View v = signInButton.getChildAt(i);

			if (v instanceof TextView) {
				TextView tv = (TextView) v;
				tv.setText(buttonText);
				return;
			}
		}
	}

	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @param ipv4
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 */
	public static String getLocalIpAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase(
								Locale.getDefault());
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0,
										delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions
		return "";
	}

	public String getIpAddress() {

		HttpURLConnection connection = null;
		String response = "";
		try {
			connection = (HttpURLConnection) new URL(
					("http://whatismyip.akamai.com/")).openConnection();

			// read response
			response = streamToString(connection.getInputStream());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}

	public String streamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static String getDatetime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
				Locale.US);
		return sdf.format(new Date());
	}

	public static String getOSVersion() {
		StringBuilder builder = new StringBuilder();
		builder.append("Android ")
		       .append(Build.VERSION.RELEASE);

		return builder.toString();
	}

	public static Session createSession(String username, String token) {
		return new Session(null, username, getDatetime(), Build.MANUFACTURER
				+ ", " + Build.MODEL + ", " + UIUtils.getOSVersion(),
				getLocalIpAddress(true), token, false, "");
	}
}
