package xaurora.io;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import xaurora.dropbox.*;
import xaurora.security.*;
import java.util.*;
public class DataFileIO {
	private String syncDirectory = "";//"\\local_data\\";
	private static DataFileIO instance = null;
	private DataFileIO(){
		
	}
	public static DataFileIO instanceOf(){
		if(instance == null){
			instance = new DataFileIO();
		}
		
		return instance;
	}
	public boolean setDirectory(String path){
		if(!new File(path).exists()){
			return false;
		}
		this.syncDirectory = path;
		return true;
	}
	public String getDirectory(){
		return this.syncDirectory;
	}
	public void createDataFile(String id,byte[] content){
		String dstpath = this.syncDirectory+new String(id)+".txt";
		System.out.println(dstpath);
		File dstFile = new File(dstpath);
		if(dstFile.exists()){
			System.err.println("ERROR MESSAGE: MD5 COLLISION");
			System.exit(1);
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
	public ArrayList<String> getContent(){
		ArrayList<String> content = new ArrayList<String>();
		
		extractFolder(content);
		
		return content;
	}
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
					for(int i = 0;i<subDir.length;i++){
						s.push(subDir[i]);
					}
					
				} else {
					
					if(getExtension(f).equals("txt")) {
						readFileContent(content, f);
						
						
					}
				}
			}
		}
	}
	private void readFileContent(ArrayList<String> content, File f) {
		try{
			Path path = Paths.get(f.getAbsolutePath());
			byte[] data = Files.readAllBytes(path);
			
			Security c = Security.getInstance();
			byte[] decrypted = c.decrypt(data);
			for(int i = 0;i<decrypted.length;i++){
				System.out.println((char) decrypted[i]);
			}
			String output = "";
			for(int i = 0;i<decrypted.length;i++){
				output+=String.valueOf((char)decrypted[i]);
			}
			content.add(output);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private static String getExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
