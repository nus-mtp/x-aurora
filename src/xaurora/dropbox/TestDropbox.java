package xaurora.dropbox;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDropbox {
	

	String url1 = "https://www.dropbox.com/home#access_token=_csCIJFXenAAAAAAAAAAGjTdSlPpI-c1wkcZEswPT15DnZgP7XStbDIlodn6awWu&token_type=bearer&uid=462746053";
	String url2 = "https://www.dropbox.com/home#access_token=IlGls336TtEAAAAAAAAg4lVI_kI3zz4fFRVNZ2yjGjGksPWo36z8X_SK7cxMhQlL&token_type=bearer&uid=29232429";
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

}
