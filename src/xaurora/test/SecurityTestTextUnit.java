package xaurora.test;

import java.io.ObjectOutputStream;

/**
 * @author GAO RISHENG A0101891L
 * Description: simple Helper data structure for testing that stores the content and a filename
 */
public final class SecurityTestTextUnit {
	private byte[] text; //it can be either cipher or plaintext;
	private String filename; //represents its filename;
	public SecurityTestTextUnit(byte[] text,String filename) {
		this.text = text;
		this.filename = filename;
	}
	
	//Secure Programming, avoid public code accessing data,
	//final keyword avoid attack from creating inherit get method
	public final byte[] getText(){
		return this.text;
	}
	public final String getName(){
		return this.filename;
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
