package xaurora.system;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeManager {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static final int MILLISECONDS_PER_SECOND = 1000;
	private static final long DEFAULT_EXPIRE_INTERVAL = 259200000;//3 days
	private long expireInterval;
	private static TimeManager classInstance;
	private TimeManager(){
		
		this.expireInterval = DEFAULT_EXPIRE_INTERVAL;
	}
	public static TimeManager getInstance(){
		if(classInstance == null){
			classInstance = new TimeManager();
		}
		return classInstance;
	}
	public String formatDateInMilliseconds(long input){
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT,Locale.getDefault());
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(input);
		return formatter.format(calendar.getTime());
	}
	public boolean isExpired(long input){
		long currentTime = System.currentTimeMillis();
		return currentTime - input > this.expireInterval;
	}
	public long getExpiredInterval(){
		return this.expireInterval;
	}
	
	public void setExpiredInterval(long seconds){
		this.expireInterval = seconds * MILLISECONDS_PER_SECOND;
	}
}
