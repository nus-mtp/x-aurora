/*
 * This component acts as the main IO feature of the software. It receives the ID generated from the
 * Logic component and the data sent from the logic component, encrypt the data,
 *  and write it into a text file. Also, it read all the content in files within the sync directory, 
 *  decrypt the data and store that into and arraylist which can be sent to the logic component for 
 *  further usage.
 *  
 *   @author GAO RISHENG
 */



package xaurora.io;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import xaurora.dropbox.*;
import xaurora.security.*;
import java.util.*;
public class DataFileIO {
	private static final String DEFAULT_SYNC_DIRECTORY = "\\local_data";
	private static final String DEFAULT_FILE_EXTENSION = ".txt";
	private static final String TEXT_FILE_TYPE = "txt";
	private static final String ERR_MSG_MD5_COLLISION = "ERROR MESSAGE: MD5 COLLISION";
	private static final int INDEX_ZERO = 0;
	private static final String NEW_EMPTY_STRING = "";
	private static final char FILE_EXTENSION_DELIMITER = '.';
	private String syncDirectory = DEFAULT_SYNC_DIRECTORY;
	private static DataFileIO instance = null;
	private DataFileIO(){
		
	}
	public static DataFileIO instanceOf(){
		if(instance == null){
			instance = new DataFileIO();
		}
		
		return instance;
	}
	
	//Description: this method is to update the sync directory
	//pre-condition: the path must be a valid directory
	//post-condition: return true if the path is successfully updated, else return false
	public boolean setDirectory(String path){
		if(!new File(path).exists()||!new File(path).isDirectory()){
			return false;
		}
		this.syncDirectory = path;
		return true;
	}
	//Description: this method is to return the current sync directory
	public String getDirectory(){
		return this.syncDirectory;
	}
	//Description: Generate the file name from an ID and write the data into the text file
	//pre-condition: a String type id and the data needs to be written into the file
	//post-condition: nil
	public void createDataFile(String id,byte[] content){
		String dstpath = this.syncDirectory+new String(id)+DEFAULT_FILE_EXTENSION;
		System.out.println(dstpath);
		File dstFile = new File(dstpath);
		if(dstFile.exists()){
			System.err.println(ERR_MSG_MD5_COLLISION);
			
		} else {
			try{
				dstFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(dstFile.getAbsolutePath());
				Security c = Security.getInstance();
				
				fos.write(c.encrypt(content));
				fos.close();
			} catch(IOException e){
				
			}
		}
	}
	//Description: get all the content from all the data files within the sync directory
	//post-condition: return an ArrayList of String storing all the data extracted from all the data files within the sync directory
	public ArrayList<String> getContent(){
		ArrayList<String> content = new ArrayList<String>();
		
		extractFolder(content);
		
		return content;
	}
	//Description: get all the content from all the data files within the sync directory
		//post-condition: return an ArrayList of String storing all the data extracted from all the data files within the sync directory
	private void extractFolder(ArrayList<String> content) {
		File dir = new File(this.syncDirectory); 
		Stack<File> s = new Stack<File>();
		
		s.push(dir);
		
		while (!s.isEmpty()){
			File f = s.pop();
			//System.out.println(f.exists());
			
			if(f.exists()){
				if(f.isDirectory()){
					File[] subDir = f.listFiles();
					for(int i = INDEX_ZERO;i<subDir.length;i++){
						s.push(subDir[i]);
					}
					
				} else {
					
					if(getExtension(f).equals(TEXT_FILE_TYPE)) {
						readFileContent(content, f);
						
						
					}
				}
			}
		}
	}
	//Description: read all the data from a data file and store it into an ArrayList
	//pre-condition: an ArrayList that stores the content read from a data file and a valid data file
	private void readFileContent(ArrayList<String> content, File f) {
		try{
			Path path = Paths.get(f.getAbsolutePath());
			byte[] data = Files.readAllBytes(path);
			
			Security c = Security.getInstance();
			byte[] decrypted = c.decrypt(data);
			for(int i = INDEX_ZERO;i<decrypted.length;i++){
				System.out.println((char) decrypted[i]);
			}
			String output = NEW_EMPTY_STRING;
			for(int i = INDEX_ZERO;i<decrypted.length;i++){
				output+=String.valueOf((char)decrypted[i]);
			}
			content.add(output);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	//Description: read the file extension from the absolute path of the file
	//pre-condition: a valid file
	//post-condition: the file extension of the file.
	private static String getExtension(File f) {
        String ext = NEW_EMPTY_STRING;
        String s = f.getName();
        int i = s.lastIndexOf(FILE_EXTENSION_DELIMITER);

        if (i > INDEX_ZERO &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
