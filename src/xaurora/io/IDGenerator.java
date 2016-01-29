package xaurora.io;
import java.math.BigInteger;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class IDGenerator {
	private static IDGenerator instance = null;
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	private static final String HASH_TYPE = "MD5";
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
	
	public String GenerateID(String url,int type){
		String output = url+getNow()+type;
		System.out.println(output);
		byte[] id = output.getBytes();
		try {
			MessageDigest md = MessageDigest.getInstance(HASH_TYPE);
			md.reset();
			md.update(output.getBytes());
			byte[] newID = md.digest(id);
			BigInteger bigInt = new BigInteger(1,newID);
			String hashID = bigInt.toString(16);
			while(hashID.length()<32){
				hashID = "0"+hashID;
			}
			return hashID;
			//return id;
		} catch (NoSuchAlgorithmException e){
			// SHOW ERROR LOG MESSAGE
			return id.toString();
		}
		
	}
}
