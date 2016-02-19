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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import xaurora.security.*;
import xaurora.text.TextIndexer;

import java.util.*;
public class DataFileIO {
	private static final String DEFAULT_SYNC_DIRECTORY = "/local_data/";
	private static final String DEFAULT_INDEX_DIRECTORY = "/index_data";
	private static final String DEFAULT_FILE_EXTENSION = ".txt";
	private static final String TEXT_FILE_TYPE = "txt";
	private static final String ERR_MSG_MD5_COLLISION = "ERROR MESSAGE: MD5 COLLISION";
	private static final int INDEX_ZERO = 0;
	private static final String NEW_EMPTY_STRING = "";
	private static final char FILE_EXTENSION_DELIMITER = '.';
	private static final String SOURCE_UNKNOWN = "unknown";
	private String syncDirectory = DEFAULT_SYNC_DIRECTORY;
	private String indexDirectory = DEFAULT_INDEX_DIRECTORY;
	private static DataFileIO instance = null;
	private DataFileIO(){
		this.createLocalDirectories();
	}
	public static DataFileIO instanceOf(){
		if(instance == null){
			instance = new DataFileIO();
		}
		
		return instance;
	}
	public void createLocalDirectories(){
		File temp = new File("");
		File storeDir = new File(temp.getAbsolutePath()+this.syncDirectory);
		File indexDir = new File(temp.getAbsolutePath()+this.indexDirectory);
		if(storeDir.mkdir()||storeDir.exists()){
			this.syncDirectory = storeDir.getAbsolutePath();
		}
		if(indexDir.mkdir()||indexDir.exists()){
			this.indexDirectory = indexDir.getAbsolutePath();
		}
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
	public String getSyncDirectory(){
		return this.syncDirectory;
	}
	
	public String getIndexDirectory(){
		return this.indexDirectory;
	}
	//Description: Generate the file name from an ID and write the data into the text file
	//pre-condition: a String type id and the data needs to be written into the file
	//post-condition: nil
	public void createDataFile(String url,String id,byte[] content){
		String dstpath = this.syncDirectory+"\\"+new String(id)+DEFAULT_FILE_EXTENSION;
		System.out.println(dstpath);
		//Store the data in the lucene indexing system.
		TextIndexer.getInstance().createIndexDocumentFromWeb(new String(content), url, dstpath);
		File dstFile = new File(dstpath);
		if(dstFile.exists()){
			System.err.println(ERR_MSG_MD5_COLLISION);
			
		} else {
			try{
				dstFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(dstFile.getAbsolutePath());
				String overallContent = url+"\n"+new String(content);
				fos.write(Security.encrypt(overallContent.getBytes()));
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
			
			byte[] decrypted = Security.decrypt(data);
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
	public void removeDataFile(String filename){
		Path filePath = Paths.get(filename);
		try {
			Files.deleteIfExists(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private String getUrlFromFile(File f){
		ArrayList<String> temp = new ArrayList<String>();
		readFileContent(temp,f);
		String result = "";
		if(temp.size()>0){
			result = temp.get(0);
		}
		return result;
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
	
	public ArrayList<String> getAllFilenamesFromDirectory(){
		ArrayList<String> result = new ArrayList<String>();
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
					result.add(f.getName());
				}
			}
		}
		return result;
	}
	public ArrayList<String> getAllURLFromDirectory(){
		ArrayList<String> result = new ArrayList<String>();
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
					result.add(getUrlFromFile(f));
				}
			}
		}
		return result;
	}
	public ArrayList<String> getAllSourceFromDirectory(){
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> temp = getAllURLFromDirectory();
		for(int index = 0;index<temp.size();index++){
			result.add(getHostFromURL(temp.get(index)));
		}
		return result;
	}
	public ArrayList<String> getAllFileLengthFromDirectory(){
		ArrayList<String> result = new ArrayList<String>();
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
					result.add((double)f.length()/1024.0+ " KB");
				}
			}
		}
		return result;
	}
	public ArrayList<String> getAllCreatedTimeFromDirectory(){
		ArrayList<String> result = new ArrayList<String>();
		File dir = new File(this.syncDirectory); 
		Stack<File> s = new Stack<File>();
		return result;
	}
	private static String getHostFromURL(String url)
	{
		String host = SOURCE_UNKNOWN;
		try
		{
			URL sourceURL = new URL(url);
			host = sourceURL.getHost();
		}catch (MalformedURLException e) {

			//e.printStackTrace(); log here
		}
		return host;
	}
}
