package xaurora.dropbox;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.Test;

import xaurora.util.UserProfile;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;

import static org.junit.Assert.*;

public class TestDropbox {

	String url1 = "https://www.dropbox.com/home#access_token=_csCIJFXenAAAAAAAAAAGjTdSlPpI-c1wkcZEswPT15DnZgP7XStbDIlodn6awWu&token_type=bearer&uid=462746053";
	String url2 = "https://www.dropbox.com/home#access_token=IlGls336TtEAAAAAAAAg4lVI_kI3zz4fFRVNZ2yjGjGksPWo36z8X_SK7cxMhQlL&token_type=bearer&uid=29232429";
	
	ArrayList<UserProfile> testUser = new ArrayList<UserProfile>();
	
	String id1 = "462746053";
	String userID1 = DropboxAuth.parseUserID(url1);
	String id2 = "29232429";
	String userID2 = DropboxAuth.parseUserID(url2);
	@Test
	public void testParseUserID() {
		System.out.println("@Test parseUserID(): " + userID1 + " = " + id1);
		assertEquals(userID1, id1);
		System.out.println("@Test parseUserID(): " + userID2 + " = " + id2);
		assertEquals(userID2, id2);
	}

	String token1 = "_csCIJFXenAAAAAAAAAAGjTdSlPpI-c1wkcZEswPT15DnZgP7XStbDIlodn6awWu";
	String access_token1 = DropboxAuth.parseAccessToken(url1);
	String token2 = "IlGls336TtEAAAAAAAAg4lVI_kI3zz4fFRVNZ2yjGjGksPWo36z8X_SK7cxMhQlL";
	String access_token2 = DropboxAuth.parseAccessToken(url2);
	
	@Test
	public void testParseAccessToken() {
		System.out.println("@Test parseAccessToken(): " + access_token1 + " = " + token1);
		assertEquals(access_token1, token1);
		System.out.println("@Test parseAccessToken(): " + access_token2 + " = " + token2);
		assertEquals(access_token2, token2);
	}
	
	long normal1 = 736465L;
	long normal2 = 12537927757L;
	long quota1 = 2147483648L;
	long quota2 = 36373004288L;
	String testGB1 = "0";
	String testGB2 = "11.68";
	String testGB3 = "2";
	String testGB4 = "33.88";
	String GB1 = DropboxAuth.bytesToGB(normal1);
	String GB2 = DropboxAuth.bytesToGB(normal2);
	String GB3 = DropboxAuth.bytesToGB(quota1);
	String GB4 = DropboxAuth.bytesToGB(quota2);
	
	@Test
	public void testBytesToGB() {
		System.out.println("@Test bytesToGB(): " + GB1 + " = " + testGB1);
		assertEquals(GB1, testGB1);
		System.out.println("@Test bytesToGB(): " + GB2 + " = " + testGB2);
		assertEquals(GB2, testGB2);
		System.out.println("@Test bytesToGB(): " + GB3 + " = " + testGB3);
		assertEquals(GB3, testGB3);
		System.out.println("@Test bytesToGB(): " + GB4 + " = " + testGB4);
		assertEquals(GB4, testGB4);
	}
	
	String testStorage1 = "0GB is used out of 2GB";
	String testStorage2 = "11.68GB is used out of 33.88GB";
	String storage1 = DropboxAuth.formulateStorage(GB1, GB3);
	String storage2 = DropboxAuth.formulateStorage(GB2, GB4);
	@Test
	public void testFormulateStorage() {
		System.out.println("@Test formulateStorage(): " + storage1 + " = " + testStorage1);
		assertEquals(storage1, testStorage1);
		System.out.println("@Test formulateStorage(): " + storage2 + " = " + testStorage2);
		assertEquals(storage2, testStorage2);
	}
	
	String userName1 = "Fan Lang";
	String userName2 = "Fan Lang";
	String email1 = "eang0322@gmail.com";
	String email2 = "langfan03@gmail.com";

	String path1 = "/462746053/";
	String path2 = "/29232429/";
	
	UserProfile user1 = new UserProfile(token1, userName1, email1, testStorage1, path1, id1);
	UserProfile user2 = new UserProfile(token2, userName2, email2, testStorage2, path2, id2);
	
	int testIndex = 1;
	
	@Test
	public void testUserDetails() {
		DropboxAuth.setAccessToken(url1);
		DropboxAuth.setAccessToken(url2);
		testUser.add(user1);
		testUser.add(user2);

		System.out.println("@Test AccessToken: " + DropboxMain.user.get(0).getAccessToken()+ " is equal to " + testUser.get(0).getAccessToken());
		assertEquals(DropboxMain.user.get(0).getAccessToken(), testUser.get(0).getAccessToken());
		System.out.println("@Test Email: " + DropboxMain.user.get(0).getEmail()+ " is equal to " + testUser.get(0).getEmail());
		assertEquals(DropboxMain.user.get(0).getEmail(), testUser.get(0).getEmail());
		System.out.println("@Test Path: " + DropboxMain.user.get(0).getPath()+ " is equal to " + testUser.get(0).getPath());
		assertEquals(DropboxMain.user.get(0).getPath(), testUser.get(0).getPath());
		System.out.println("@Test Storage: " + DropboxMain.user.get(0).getStorage()+ " is equal to " + testUser.get(0).getStorage());
		assertEquals(DropboxMain.user.get(0).getStorage(), testUser.get(0).getStorage());
		System.out.println("@Test UserID: " + DropboxMain.user.get(0).getUserID()+ "  is equal to " + testUser.get(0).getUserID());
		assertEquals(DropboxMain.user.get(0).getUserID(), testUser.get(0).getUserID());
		System.out.println("@Test UserName: " + DropboxMain.user.get(0).getUserName()+ " is equal to " + testUser.get(0).getUserName());
		assertEquals(DropboxMain.user.get(0).getUserName(), testUser.get(0).getUserName());
		System.out.println("@Test CurrentUserIndex: " + DropboxMain.currentUserIndex + " is equal to " + testIndex);
		assertEquals(DropboxMain.currentUserIndex, testIndex);
	}
	
}
