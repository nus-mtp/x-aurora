package xaurora.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;

import xaurora.security.Security;

public class EncryptTest {
	private static final String PATH_INPUT = "Securityinput.txt";
	private static final String PATH_OUTPUT = "EncryptTestOutput.txt";
	private static final String PATH_EXPECTED_OUTPUT = "Encryptedoutput.txt";
	@Test
	public void test() {
		Security c = Security.getInstance();
		try{
			InputStream in = new FileInputStream(new File(PATH_INPUT));
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in));
			File outputFile = new File(PATH_OUTPUT);
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
				//System.out.println("FINISH");
			} catch (Exception ex){
				ex.printStackTrace();
			}
			Path path = Paths.get(outputFile.getAbsolutePath());
			byte[] data = Files.readAllBytes(path);
			Path expectedPath = Paths.get(new File(PATH_EXPECTED_OUTPUT).getAbsolutePath());
			byte[] expectedData = Files.readAllBytes(expectedPath);
			for(int i = 0;i<data.length;i++){
				assertEquals(expectedData[i],data[i]);
			}
			
		} catch (IOException e){
			e.printStackTrace();
			
		}
	}

}
