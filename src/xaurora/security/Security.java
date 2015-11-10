package xaurora.security;
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class Security {
	private static Cipher c;
	private static Security s;
	protected Security(){
		try {
			c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			
			//c.init(Cipher.ENCRYPT_MODE,skeySpec);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public static Security getInstance(){
		if (s == null){
			return new Security();
		}
		return s;
	}
	public static byte[] encrypt(byte[] content){
		try {
			SecretKeySpec skeySpec = new SecretKeySpec("aurora".getBytes("UTF-8"), "AES");
			c.init(Cipher.ENCRYPT_MODE, skeySpec);
			return c.doFinal(content);
		} catch(Exception e){
			e.getMessage();
		}
		//log the error message (Encryption failed)
		return content;
	}
}
