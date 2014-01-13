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
public class DateUtil {
	static final String TAG = "DateUtil";
	private static GregorianCalendar calendar = new  GregorianCalendar();


	private static HashMap<String, SimpleDateFormat> dateFormats = new HashMap<String, SimpleDateFormat>();

	public static String formatDate(Date date, String format){
		SimpleDateFormat sdf = dateFormats.get(format);
		if (sdf==null){
			sdf = new SimpleDateFormat(format);
			dateFormats.put(format, sdf);
		}
		return sdf.format(date);
	}

	public static Date parseDate(String dateStr, String format){
		SimpleDateFormat sdf = dateFormats.get(format);
		if (sdf==null){
			sdf = new SimpleDateFormat(format);
			dateFormats.put(format, sdf);
		}
		try{
			return sdf.parse(dateStr);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return new Date(0);
	}


	public static Date getDate(int year, int month, int day, int hour, int minute){
		calendar.set(year, month, day, hour, minute);
		return calendar.getTime();
	}

}
