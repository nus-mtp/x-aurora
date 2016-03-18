package xaurora.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

import xaurora.security.Security;



/**
 * 
 * Description:
 * 		This class is mainly for generation of Expected output in Security Test. (Encryption)
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public final class SecurityInputProcessor {
	
	public static void main(String[] args){
		//Performance Analysis
		long startTime = System.currentTimeMillis();
		produceExpectedOutput();
		long endTime = System.currentTimeMillis();
		System.out.println("Finished in "+(double)(endTime-startTime)/1000.0+" seconds.");
	}

	/**
	 * Description: Product Expected output for Security Test
	 * 1. Reads all text file under the Input folder
	 * 2. Encrypt the text data of each text file in the input folder
	 * 3. Output the encrypted Text to the Expected Output folder
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	private static void produceExpectedOutput(){
		File temp = new File(SecurityTestInputGenerator.NEW_EMPTY_STRING);
		File storeDir = new File(temp.getAbsolutePath()+SecurityTestInputGenerator.RANDOM_INPUT_DIRECTORY);
		Stack<File> allFiles = new Stack<File>();
		int count = 0;
		allFiles.push(storeDir);
		//Recursively search through the directory tree
		//It repeats with codes in other places of this project like DataFileIO since there is no function pointer in Java
		//to make it more reusable. Also since the information retrieve is different in different context.
		//The only way I can imagine is to repeat this.
		while(!allFiles.isEmpty()){
			File f = allFiles.pop();
			if(f.isDirectory()){
				File[] files = f.listFiles();
				for(File t:files){
					allFiles.push(t);
				}
			} else{
				count++;
				try{
					Path path = Paths.get(f.getAbsolutePath());
					File outputFile = new File(temp.getAbsolutePath()+SecurityTestInputGenerator.EXPECTED_OUTPUT_DIRECTORY+
							SecurityTestInputGenerator.PATH_SEPARATOR+
							SecurityTestInputGenerator.RANDOM_OUTPUT_FILENAME+count+SecurityTestInputGenerator.DEFAULT_FILE_EXTENSION);
					if(!outputFile.exists()){
						outputFile.createNewFile();
					}
					//read the input file
					byte[] content = Files.readAllBytes(path);
					FileOutputStream fos = new FileOutputStream(outputFile.getAbsoluteFile());
					//write the output file
					fos.write(Security.encrypt(content, f.getName().replace(SecurityTestInputGenerator.DEFAULT_FILE_EXTENSION,SecurityTestInputGenerator.NEW_EMPTY_STRING)));
					fos.flush();
					fos.close();
				} catch(IOException e){
					e.printStackTrace();
				}
				
			}
		}
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
