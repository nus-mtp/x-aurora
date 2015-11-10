package xaurora.security;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class Security {
	
	private static Security s;
	private static final String initVector = "Xaur0r4oOoApPs5S";
	protected Security(){
		
	}
	public static Security getInstance(){
		if (s == null){
			return new Security();
		}
		return s;
	}
	public static byte[] encrypt(byte[] content){
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec("XauroraXaurora12".getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);
			//System.out.println(cipher.doFinal(content));
			return cipher.doFinal(content);
		} catch(Exception e){
			e.printStackTrace();
		}
		//log the error message (Encryption failed)
		return content;
	}
	
	public static byte[] decrypt(byte[] content){
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec("XauroraXaurora12".getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec,iv);
			//System.out.println(c.doFinal(content));
			return cipher.doFinal(content);
		} catch(Exception e){
			e.printStackTrace();
		}
		//log the error message (Encryption failed)
		return content;
	}
	
}
