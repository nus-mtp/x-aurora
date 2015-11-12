package xaurora.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import xaurora.security.Security;

public class DecryptTest {
	private static final String PATH_INPUT = "Securityinput.txt";

	private Security c = Security.getInstance();
	@Test
	public void test() {

		try{
		
		File inputFile = new File(PATH_INPUT);
		Path Inputpath = Paths.get(inputFile.getAbsolutePath());
		byte[] expecteddata = Files.readAllBytes(Inputpath);
		byte[] actualData = c.decrypt(c.encrypt(expecteddata));
		System.out.println(expecteddata.length);
		System.out.println(actualData.length);
		
		assertTrue(expecteddata.length==actualData.length);
		for(int i = 0;i<expecteddata.length;i++){
			assertEquals(expecteddata[i],actualData[i]);
		}
		} catch(IOException e){
			e.printStackTrace();
		}
	}

}
