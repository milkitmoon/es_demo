package com.milkit.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static final long ONE_MINUTE_IN_MILLIS = 60000;		//60초

	public static String plusDay(Date srcdate, String targetDateFormat, int addDay) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(targetDateFormat);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(srcdate);
		cal.add(Calendar.DAY_OF_MONTH, addDay);
		srcdate = cal.getTime();
		
		return dateFormat.format(srcdate);
	}

	public static Date plusDay(Date srcdate, int addDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(srcdate);
		cal.add(Calendar.DAY_OF_MONTH, addDay);
		srcdate = cal.getTime();
		
		return srcdate;
	}

	public static Date plusMin(Date srcdate, int addMin) {
		long longTime = srcdate.getTime();

		return new Date(longTime + (addMin * ONE_MINUTE_IN_MILLIS));
	}
	
	public static int compareDate(Date srcDate, Date compareDate) {
		if (srcDate.equals(compareDate)) return 0;

		return !srcDate.after(compareDate) ? 1 : -1;
	}

	public static String getFormatedTimeString(Date date, String format) {
		String timeString = "";
		
		if(date != null && format != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			timeString =  dateFormat.format(date);
		}
		
		return timeString;
	}

	public static String getFormatedTimeString(long time, String outputFormat) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(outputFormat);
		
	    return format.format(date);
	}
}
