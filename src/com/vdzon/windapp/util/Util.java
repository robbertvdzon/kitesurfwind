package com.vdzon.windapp.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Robbert on 11/27/13.
 */
public class Util {
	static final String TAG = "Util";

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}

	public static int stringToInt(String string){
		return Integer.parseInt(string);
	}

	public static double stringToDouble(String string){
		return Double.parseDouble(string);
	}
	public static float stringToFloat(String string){
		return Float.parseFloat(string);
	}


	public static Object objectFromString( String s ) throws IOException , 	ClassNotFoundException {
		byte [] data = Base64Coder.decode(s);
		ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(  data ) );
		Object o  = ois.readObject();
		ois.close();
		return o;
	}

	public static String objectToString( Serializable o ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return new String( Base64Coder.encode( baos.toByteArray() ) );
	}


	public static String slurp(final InputStream is, final int bufferSize)
	{
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			}
			finally {
				in.close();
			}
		}
		catch (UnsupportedEncodingException ex) {
			/* ... */
		}
		catch (IOException ex) {
			/* ... */
		}
		return out.toString();
	}


	public static int calculateDay(Date date){
		int day = 0;
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(date);
		return cal.get(Calendar.YEAR)*1000 + cal.get(Calendar.DAY_OF_YEAR);
	}

	public static float calculateTime(Date date){
		int day = 0;
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(date);
		return (float)((float)cal.get(Calendar.HOUR_OF_DAY) + ((float)cal.get(Calendar.MINUTE))/60);
	}

	public int calculateCellCount(int dp){
		int cellCount = 2;// start with two, if that is too big, return 1
		while (true){
			int maxSize = 70*cellCount-30;
			if (maxSize>dp) return cellCount-1;// too large, use the previous version 
			cellCount++;
		}


	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp){
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px){
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}


	public static void readStream(InputStream in) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
