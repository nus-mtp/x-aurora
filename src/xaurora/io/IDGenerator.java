package xaurora.io;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class IDGenerator {
	private static IDGenerator instance = null;
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	private IDGenerator(){
		
	}
	public static IDGenerator instanceOf(){
		if(instance == null){
			instance = new IDGenerator();
		}
		return new IDGenerator();
	}
	
	private static String getNow(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
	
	public byte[] GenerateID(String url,int type){
		String output = url+getNow()+type;
		byte[] id = output.getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] newID = md.digest(id);
			return newID;
		} catch (NoSuchAlgorithmException e){
			// SHOW ERROR LOG MESSAGE
			return id;
		}
		
	}
}
