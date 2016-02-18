package xaurora.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import xaurora.security.Security;



public class SecurityInputProcessor {
	private static Security c = Security.getInstance();
	public static void main(String[] args){
		parse("Securityinput.txt","Encryptedoutput.txt");
	}
	public SecurityInputProcessor(){
		
	}
	protected static void parse(String inputPath,String outputPath){
		try {
			InputStream in = new FileInputStream(new File(inputPath));
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in));
			
			File outputFile = new File(outputPath);
			if(!outputFile.exists()){
				outputFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(outputFile.getAbsoluteFile());
			ArrayList<byte[]> result = new ArrayList<byte[]>();
			
			try {
				int i = 0;
				while(i<10000){
					byte[] encrypted = Security.encrypt(br1.readLine().getBytes("UTF-8"));
					result.add(encrypted);
					i++;
				}
				System.out.println(result.size());
				for(int j = 0;j<result.size();j++){
					fos.write(result.get(j));
				}
				fos.flush();
				fos.close();
				System.out.println("FINISH");
			} catch (Exception ex){
				ex.printStackTrace();
			}
		} catch (IOException e){
			e.printStackTrace();
		} 
	}
}
