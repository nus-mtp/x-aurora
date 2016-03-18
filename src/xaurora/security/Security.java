package xaurora.security;

import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Description: This Class is mainly in charge of maintaining the confidentiality of the extracted text data by applying
 * AES Encryption algorithm to the plain text content. Also, it will be in charge of data decryption;
 * @author GAO RISHENG A0101891L
 *
 */
public final class Security {
	private static final int CASE_DIFFERENCE = 32;
	private static final int SUM_OF_DIGITS = 105;
	private static final String ENCRYPT_ALGORITHM = "AES";
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String ENCRYPT_METHOD = "AES/CBC/PKCS5PADDING";
	private static final String ENCRYPT_SEED = "x4Ur0raXaurORAe3";
	private static final int INDEX_ZERO = 0;
	private static final int IV_LENGTH = 16;

	/**
	 * Description: encrypt the plain text content with given IV
	 * @param content, the plain text in bytes
	 * @param initVector the String IV
	 * @return	Cipher text data in bytes
	 * @author GAO RISHENG A0101891L
	 */
	public static byte[] encrypt(byte[] content,String initVector){
		try {
			byte[] secretKey = ENCRYPT_SEED.getBytes(DEFAULT_CHARSET);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey, ENCRYPT_ALGORITHM);
			Cipher cipher = Cipher.getInstance(ENCRYPT_METHOD);
			byte[] iv = createIV(initVector);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec,ivspec);
			return cipher.doFinal(content);
		} catch(Exception e){
			e.printStackTrace();
		}
		//log the error message (Encryption failed)
		return content;
	}
	
	/**
	 * Description: decrypt the encrypted text content with given IV
	 * @param content, the encrypted cipher text in bytes
	 * @param initVector the String IV
	 * @return	Plain text data in bytes
	 * @author GAO RISHENG A0101891L
	 */
	public static byte[] decrypt(byte[] content,String initVector){
		try {
			byte[] secretKey = ENCRYPT_SEED.getBytes(DEFAULT_CHARSET);
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey, ENCRYPT_ALGORITHM);
			Cipher cipher = Cipher.getInstance(ENCRYPT_METHOD);
			byte[] iv = createIV(initVector);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec,ivspec);
			return cipher.doFinal(content);
		} catch(Exception e){
			e.printStackTrace();
		}
		//log the error message (Encryption failed)
		return content;
	}
	/**
	 * Description: generate the IV for AES Encryption by doing modifications to the filename
	 * @param initVector, is usually the file name of a text file
	 * @return the IV of AES Encryption in byte array
	 */
	private static byte[] createIV(String initVector){
		char[] characters = initVector.toCharArray();
		byte[] output = new byte[IV_LENGTH];
		for(int index = INDEX_ZERO;index<characters.length;index++){
			
			if(characters[index]>='A'&&characters[index]<='Z'){
				output[index%IV_LENGTH]=(byte) ((char) characters[index] + CASE_DIFFERENCE);
			}
			if(characters[index]>='a'&&characters[index]<='z'){
				output[index%IV_LENGTH]=(byte) ((char) characters[index] - CASE_DIFFERENCE);
			}
			if(characters[index]>='0'&&characters[index]<='9'){
				//reversing the digit
				output[index%IV_LENGTH]=(byte) (SUM_OF_DIGITS-(char)characters[index]);
			
			}

		}
		return output;
		
	}
	/**
	 * Secure Programming. Making this Object not-clonable. Object.clone() allows cloning the data of an object without initialize it
	 * which may leak the chances for attacker to access the data internally
	 * @Author GAO RISHENG A0101891L
	 */
	public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
	}
	
	/**
	 * Secure Programming. Disable the serialize option of the object which avoid attacker to print the object in serialize manner
	 * and inspect the internal status of the object
	 * @author GAO RISHENG A0101891L
	 */
	private final void writeObject(ObjectOutputStream out)
			throws java.io.IOException {
			        throw new java.io.IOException("Object cannot be serialized");
			}
}
