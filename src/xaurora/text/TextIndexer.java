package xaurora.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class TextIndexer {
	private static final String SOURCE_UNKNOWN = "unknown";
	private static final String FIELD_SOURCE = "source";
	private static final String FIELD_URL = "url";
	private static final String FIELD_CONTENT = "content";
	private static final String FIELD_SENTENCE = "sentence";
	private static final String FIELD_NUMBER = "number";
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_FILENAME = "filename";
	private static final String FIELD_TERMS = "term";
	private static TextIndexer instance = null;
	private TextIndexer(){
		this.storeDirectory = new RAMDirectory();
	}
	public static TextIndexer getInstance(){
		if(instance == null){
			instance = new TextIndexer();
		}
		return instance;
	}
	private Directory storeDirectory;
	public void createIndexDocument(String rawData,String url,String filename)
	{
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		String[] paragraphs = rawData.split("\n");

		String sourceHost = getHostFromURL(url);
		try {

			IndexWriter writer = new IndexWriter(this.storeDirectory,config);
			for(int i = 0;i<paragraphs.length;i++)
			{
				Document data = new Document();
				String[] sentences = paragraphs[i].split("[.|!|?]+ ");
				addURL(data,url);
				addSource(data,sourceHost);
				addFilename(data,filename);
				addContent(data,paragraphs[i]);
				extractNumbersAndEmails(paragraphs, i, data);
			}


		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	private void extractNumbersAndEmails(String[] paragraphs, int i, Document data) {
		InputStream is = new ByteArrayInputStream(Charset.forName("UTF-8").encode(paragraphs[i]).array());
		XauroraParser paragraphParser = new XauroraParser(is);
		try {
			paragraphParser.parseEmailInParagraph(data);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public static void addURL(Document doc,String url)
	{
		addStringField(doc,FIELD_URL,url);
	}
	public static void addSource(Document doc,String source)
	{
		addStringField(doc,FIELD_SOURCE,source);
	}
	public static void addFilename(Document doc,String filename)
	{
		addStringField(doc,FIELD_FILENAME,filename);
	}
	public static void addContent(Document doc,String content)
	{
		addStringField(doc,FIELD_CONTENT,content);
	}

	public static void addNumber(Document doc,String number)
	{
		addStringField(doc,FIELD_NUMBER,number);
	}
	public static void addEmail(Document doc,String email)
	{
		addStringField(doc,FIELD_EMAIL,email);
	}

	private static void addTextField(Document doc,String field,String value)
	{
		doc.add(new TextField(field,value,Field.Store.YES));
	}

	private static void addStringField(Document doc,String field,String value)
	{
		doc.add(new StringField(field,value,Field.Store.YES));
	}
}
