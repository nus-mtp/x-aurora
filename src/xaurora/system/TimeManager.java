package xaurora.system;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeManager {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static final long MILLISECONDS_PER_DAY = 86400000;
	private static final int NUMBER_OF_DAYS = 0;
	public static String formatDateInMilliseconds(long input){
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT,Locale.getDefault());
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(input);
		return formatter.format(calendar.getTime());
	}
	public static boolean isExpired(long input){
		long currentTime = System.currentTimeMillis();
		return currentTime - input > NUMBER_OF_DAYS * MILLISECONDS_PER_DAY;
	}
}
