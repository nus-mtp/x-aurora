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
	private static final String PATH_SEPARATOR = "\\";
	private static final String NEWLINE = "\n";
	private static final String DEFAULT_SYNC_DIRECTORY = "/local_data/";
	private static final String DEFAULT_INDEX_DIRECTORY = "/index_data/";
	private static final String DEFAULT_FILE_EXTENSION = ".txt";
	private static final String TEXT_FILE_TYPE = "txt";
	private static final String ERR_MSG_MD5_COLLISION = "ERROR MESSAGE: MD5 COLLISION";
	private static final String ERR_INVALID_FILE_TYPE = "INVALID FILE TYPE";
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
	
	/**
	 * Create local synchronization path and Lucene Indexing directory
	 * Default Sync Directory: /project_directory/local_data/
	 * Default Indexing Directory: /project_directory/index_data/
	 */
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

	/**
	 * Description: this method is to update the sync directory
	 * @param path,must be a valid directory
	 * @return true if the path is successfully updated, else return false
	 * 
	 *@author GAO RISHENG A0101891L 	 
	 * 
	 */
	public boolean setDirectory(String path){
		if(!new File(path).exists()||!new File(path).isDirectory()){
			return false;
		}
		this.syncDirectory = path;
		return true;
	}
	
	/**
	 * 
	 * @return return the current sync directory
	 * @author GAO RISHENG A0101891L
	 */
	public String getSyncDirectory(){
		return this.syncDirectory;
	}
	

	/**
	 * @return the current Indexing directory
	 * @author GAO RISHENG A0101891L
	 */
	public String getIndexDirectory(){
		return this.indexDirectory;
	}
	

	/**
	 * Description: base on a MD5 hased String ID generated from the source url, generate the respective datafile path
	 * @param id, A correctly MD5- hashed String ID which identifies the source URL and the extraction time
	 * @return An absolute path of the data file to be created with this respective ID
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	private String generateDataFilePath(String id) {
		String dstpath = this.syncDirectory+PATH_SEPARATOR+new String(id)+DEFAULT_FILE_EXTENSION;
		return dstpath;
	}
	
	
	/**
	 * Description: Writing extracted contents from the web page into text datafile with encryption
	 *				DataFile format: the first line is always the source URL and from the 
	 *				second line onwards, there will be the extracted text content				   
	 *				from the source URL
	 * @param url, the url from the source
	 * @param content, the byte array storing the extracted text data
	 * @param dstFile, the destination data file path
	 * @throws IOException in case I/O Error occurs in the writing process
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	private void writeDataFileWithEncryption(String url, byte[] content, File dstFile)
			throws IOException {
		dstFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(dstFile.getAbsolutePath());
		String overallContent = url+NEWLINE+new String(content);
		fos.write(Security.encrypt(overallContent.getBytes()));
		//fos.write(overallContent.getBytes());
		fos.close();
	}
	

	/**
	 * The complete data file generation process
	 * @param url, the String URL of the source web site
	 * @param id, the MD5-hashed ID of the source
	 * @param content, the text content received from the web browser
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	public void createDataFile(String url,String id,byte[] content){
		String dstpath = generateDataFilePath(id);
		//Store the data in the lucene indexing system.
		long currentTime = System.currentTimeMillis();
		TextIndexer.getInstance().createIndexDocumentFromWeb(new String(content), url, dstpath,currentTime);
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
	
	
	/**
	 * Description: get all the content from all the data files within the sync directory
	 * @return the arraylist that stores all the text content extracted from the folder
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	public ArrayList<String> getContent(){
		ArrayList<String> content = new ArrayList<String>();
		
		extractFolder(content);
		
		return content;
	}
	
	/**
	 * Description: Get all the content from all the data files within the sync directory
	 * @param content, the arraylist that used to store all the extracted content in the local folder
	 * 
	 * @author GAO RISHENG A0101891L
	 */
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
						content.add(readFileContent(f));
					}
				}
			}
		}
	}
	
	/**
	 * Description: read all the data from a data file and store it into an ArrayList
	 * @param f, a valid text file that storing the encrypted data
	 * @return A String that contains the decrypted data from the encrypted text file
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	private String readFileContent(File f) {
		assert(f.isFile()&&f.exists()&&!f.isDirectory()&&!getExtension(f).equals(TEXT_FILE_TYPE)):ERR_INVALID_FILE_TYPE;
		StringBuilder output = new StringBuilder(NEW_EMPTY_STRING);
		try{
			Path path = Paths.get(f.getAbsolutePath());
			byte[] data = Files.readAllBytes(path);

			byte[] decrypted = Security.decrypt(data);

			for(int i = INDEX_ZERO;i<decrypted.length;i++){
				output.append(String.valueOf((char)decrypted[i]));
			}
			
		} catch (IOException e){
			e.printStackTrace();
		}
		return output.toString();
	}
	

	/**
	 * 
	 * Description: Check for expired file and delete its local data file and indexing entity
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	public synchronized void autoCheckForExpiredFile(){
		ArrayList<DataFileMetaData> allMetaData = this.getAllMetaData();
		for(int index = INDEX_ZERO; index<allMetaData.size();index++){
			if(TimeManager.getInstance().isExpired(allMetaData.get(index).getLastModified())){
				//System.out.println(allMetaData.get(index).getFilename());
				TextIndexer.getInstance().deleteByField(TextIndexer.FIELD_FILENAME, allMetaData.get(index).getFilename());
			}
		}
	}

	/**
	 * Description: Removing a local data file from from given data file filename
	 * @param A valid String filename of a local data file
	 * 
	 * @author GAO RISHENG A0101891L
	 * 
	 */
	public void removeDataFile(String filename){
		Path filePath = Paths.get(this.syncDirectory+PATH_SEPARATOR+filename+DEFAULT_FILE_EXTENSION);
		try {
			Files.deleteIfExists(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Description: read the source from the content of a data file
	 * @param f,a valid file
	 * @return the source URL of the text content within the file
	 * 
	 * @author GAO RISHENG A0101891L.
	 */
	private String getUrlFromFile(File f){
		assert(f.isFile()&&f.exists()&&!f.isDirectory()):ERR_INVALID_FILE_TYPE;
		String textContent = readFileContent(f);
		String[] paragraphs = textContent.split(NEWLINE);
		String result = NEW_EMPTY_STRING;
		if(paragraphs.length>INDEX_ZERO){
			result = paragraphs[INDEX_ZERO];
		}
		
		return result;

	}
	
	/**
	 * Description: read the file extension from the absolute path of the file
	 * @param f,a valid file
	 * @return the file extension of the file.
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	private static String getExtension(File f) {
		assert(f.isFile()&&f.exists()&&!f.isDirectory()):ERR_INVALID_FILE_TYPE;
        String ext = NEW_EMPTY_STRING;
        String s = f.getName();
        int i = s.lastIndexOf(FILE_EXTENSION_DELIMITER);

        if (i > INDEX_ZERO &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

	
	/**
	 * 
	 * Description: read all the useful meta data from all the data files in the system in the current
	 *			    synchronization directory
	 * @return      an arraylist of dataFileMetaData which stores all following meta data:
	 * 				Filename
	 * 				URL
	 * 				Host name of the source
	 * 				Length of file
	 * @author GAO RISHENG A0101891L
	 */
	public ArrayList<DataFileMetaData> getAllMetaData(){
		ArrayList<DataFileMetaData> result = new ArrayList<DataFileMetaData>();
		File dir = new File(this.syncDirectory); 
		Stack<File> s = new Stack<File>();
		s.push(dir);
		//iterate through the directory tree
		while (!s.isEmpty()){
			File f = s.pop();
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
	
	/**
	 * Description: This is to recreate the whole indexing system from the current sync directory
	 * Post-condition:construct the indexing system with all data read successfully from the sync directory
	 * 
	 * @author GAORISHENG A0101891L
	 */
	public synchronized void updateIndexingFromFiles(){
		ArrayList<DataFileMetaData> allMetaData = this.getAllMetaData();
		ArrayList<String> content = this.getContent();

		for(int index = INDEX_ZERO;index<allMetaData.size();index++){
			TextIndexer.getInstance().createIndexDocumentFromWeb(content.get(index), allMetaData.get(index).getUrl(), allMetaData.get(index).getFilename(),allMetaData.get(index).getLastModified());

		}
	}
}
