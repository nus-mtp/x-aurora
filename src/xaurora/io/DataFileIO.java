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

import xaurora.security.*;
import xaurora.system.TimeManager;
import xaurora.text.TextIndexer;
import xaurora.util.DataFileMetaData;

import java.util.*;
public class DataFileIO {
	private static final String DEFAULT_SYNC_DIRECTORY = "/local_data/";
	private static final String DEFAULT_INDEX_DIRECTORY = "/index_data/";
	private static final String DEFAULT_FILE_EXTENSION = ".txt";
	private static final String TEXT_FILE_TYPE = "txt";
	private static final String ERR_MSG_MD5_COLLISION = "ERROR MESSAGE: MD5 COLLISION";
	private static final int INDEX_ZERO = 0;
	private static final String NEW_EMPTY_STRING = "";
	private static final char FILE_EXTENSION_DELIMITER = '.';
	private String syncDirectory = DEFAULT_SYNC_DIRECTORY;
	private String indexDirectory = DEFAULT_INDEX_DIRECTORY;
	private static DataFileIO instance = null;
	
	//Singleton Class constructor
	//This is to limits that only 1 instance of DataFileIO will be created
	private DataFileIO(){
		this.createLocalDirectories();
	}
	public static DataFileIO instanceOf(){
		if(instance == null){
			instance = new DataFileIO();
		}
		
		return instance;
	}
	//Create local synchronization path and Lucene Indexing directory
	//Default Sync Directory: /project_directory/local_data/
	//Default Indexing Directory: /project_directory/index_data/
	public void createLocalDirectories(){
		File temp = new File(NEW_EMPTY_STRING);
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
	
	//Description: return the current sync directory
	public String getSyncDirectory(){
		return this.syncDirectory;
	}
	
	//Description:��return the current Indexing directory
	public String getIndexDirectory(){
		return this.indexDirectory;
	}
	
	//Description: base on a MD5 hased String ID generated from the source url, generate the respective datafile path
	//pre-condition:��A correctly MD5-hashed String ID which identifies the source.
	//post-condition: An absolute path of this respective ID
	private String generateDataFilePath(String id) {
		String dstpath = this.syncDirectory+"\\"+new String(id)+DEFAULT_FILE_EXTENSION;
		return dstpath;
	}
	
	//Description: Writing extracted contents from the web page into text datafile with encryption
	//DataFile format: the first line is always the source URL and from the 
	//				   second line onwards, there will be the extracted text content
	//				   from the source URL
	//Pre-condition: The URL from the source, a byte array storing the extracted text data
	//				 and a new created File that used to store these data
	//Post-condition: a new Datafile will be created, if the file creation fails
	//				  an Error will be raised
	private void writeDataFileWithEncryption(String url, byte[] content, File dstFile)
			throws IOException {
		dstFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(dstFile.getAbsolutePath());
		String overallContent = url+"\n"+new String(content);
		fos.write(Security.encrypt(overallContent.getBytes()));
		fos.close();
	}
	
	//Description: The complete datafile generation process
	//pre-condition: the URL of the source in String, the MD5-hashed ID of the source
	//				 and the extracted text content from the web page storing in
	//				a byte array
	//post-condition: new datafile storing these data will be created into the
	//				 local sync directory and all the text data will be added into
	//				indexing system for future usage (e.g search)
	public void createDataFile(String url,String id,byte[] content){
		String dstpath = generateDataFilePath(id);
		//Store the data in the lucene indexing system.
		TextIndexer.getInstance().createIndexDocumentFromWeb(new String(content), url, dstpath);
		File dstFile = new File(dstpath);
		if(dstFile.exists()){
			System.err.println(ERR_MSG_MD5_COLLISION);
			
		} else {
			try{
				writeDataFileWithEncryption(url, content, dstFile);
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
	//Description: Get all the content from all the data files within the sync directory
	//pre-condition: An Empty arraylist that used to store the data.
	//post-condition: return an ArrayList of String storing all the data extracted from all the data files within the sync directory
	private void extractFolder(ArrayList<String> content) {
		File dir = new File(this.syncDirectory); 
		Stack<File> s = new Stack<File>();
		s.push(dir);
		while (!s.isEmpty()){
			File f = s.pop();
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
	//Post-condition: the data stored in the encrypted file will be decrypted and
	//				  store into the input arraylist.
	private void readFileContent(ArrayList<String> content, File f) {
		try{
			Path path = Paths.get(f.getAbsolutePath());
			byte[] data = Files.readAllBytes(path);
			
			byte[] decrypted = Security.decrypt(data);
			String output = NEW_EMPTY_STRING;
			for(int i = INDEX_ZERO;i<decrypted.length;i++){
				output+=String.valueOf((char)decrypted[i]);
			}
			content.add(output);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	//Description: Check for expired file and delete its local datafile and indexing entity
	//Pre-condition: NIL
	//Post-condition: NIL
	public synchronized void autoCheckForExpiredFile(){
		ArrayList<DataFileMetaData> allMetaData = this.getAllMetaData();
		for(int index = 0; index<allMetaData.size();index++){
			if(TimeManager.getInstance().isExpired(allMetaData.get(index).getLastModified())){
				//System.out.println(allMetaData.get(index).getFilename());
				TextIndexer.getInstance().deleteByField(TextIndexer.FIELD_FILENAME, allMetaData.get(index).getFilename());
			}
		}
	}
	//Description: Removing a local datafile from from given datafile filename
	//Pre-condition: A valid String filename of a local datafile
	//Post-condition: The respective data text file is successfully deleted
	public void removeDataFile(String filename){
		Path filePath = Paths.get(this.syncDirectory+"\\"+filename+DEFAULT_FILE_EXTENSION);
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
	//Description: read all the useful meta data from all the data files in the system in the current
	//			   synchronization directory
	//Pre-condition: NIL
	//Post-condition: return an arraylist of dataFileMetaData which stores all following meta data:
	//				  filename,
	//				  URL
	//				  Host name of the source
	//				  Length of file
	public ArrayList<DataFileMetaData> getAllMetaData(){
		ArrayList<DataFileMetaData> result = new ArrayList<DataFileMetaData>();
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
					DataFileMetaData tempEntity = new DataFileMetaData(f.getName().substring(0, f.getName().lastIndexOf(".")),getUrlFromFile(f));
                                        tempEntity.addFileMetaData(f.length(), f.lastModified());
					result.add(tempEntity);					
				}
			}
		}
		return result;
	}
	//Description: This is to recreate the whole indexing system from the current sync directory
	//Pre-condition: NIL
	//Post-condition: construct the indexing system with all data read successfully from the sync directory
	public synchronized void updateIndexingFromFiles(){
		ArrayList<DataFileMetaData> allMetaData = this.getAllMetaData();
		ArrayList<String> content = this.getContent();
		for(int index = 0;index<allMetaData.size();index++){
			TextIndexer.getInstance().createIndexDocumentFromWeb(content.get(index), allMetaData.get(index).getUrl(), allMetaData.get(index).getFilename());
		}
	}
}
