/*
 * This is the indexing component of the Xaurora software which is in charge of 
 * indexing the unprocessed text data extracted from the web pages and handling 
 * the CRUD queries to the indexing system 
 *  
 *   @author GAO RISHENG
 */




package xaurora.text;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import xaurora.io.DataFileIO;

public class TextIndexer {
	private static final String SOURCE_UNKNOWN = "unknown";
	private static final String FIELD_SOURCE = "source";
	private static final String FIELD_URL = "url";
	private static final String FIELD_CONTENT = "content";
	private static final String FIELD_NUMBER = "number";
	private static final String FIELD_EMAIL = "email";
	public static final String FIELD_FILENAME = "filename";
	private static final String FIELD_TERMS = "term";
	private static final String FIELD_EXTENDED_DATA = "data";
	private static final String DATAFILE_EXTENSION = ".txt";
	private static final int DEFAULT_DISPLAY_NUMBER = 5;
	//656 stop_word sets. Credits from http://www.ranks.nl/stopwords 
	private static final List<String> stopWords = Arrays.asList("a","able","about","above","abst","accordance",
											"according","accordingly","across","act","actually","added","adj","affected","affecting",
											"affects","after","afterwards","again","against","ah","all","almost","alone","along","already","also",
											"although","always","am","among","amongst","an","and","announce","another","any","anybody","anyhow",
											"anymore","anyone","anything","anyway","anyways","anywhere","apparently","approximately","are",
											"aren","arent","arise","around","as","aside","ask","asking","at","auth","available","away",
											"awfully","b","back","be","became","because","become","becomes","becoming","been","before",
											"beforehand","begin","beginning","beginnings","begins","behind","being","believe","below","beside",
											"besides","between","beyond","biol","both","brief","briefly","but","by","c","ca","came","can","cannot","can't","cause",
											"causes","certain","certainly","co","com","come","comes",
											"contain","containing","contains","could","couldnt","d","date","did","didn't","different","do","does",
											"doesn't","doing","done","don't","down","downwards","due","during","e","each",
											"ed","edu","effect","eg","eight","eighty","either","else","elsewhere","end","ending",
											"enough","especially","et","et-al","etc","even","ever","every","everybody","everyone",
											"everything","everywhere","ex","except","f","far","few","ff","fifth","first","five","fix",
											"followed","following","follows","for","former","formerly","forth","found","four","from","further",
											"furthermore","g","gave","get","gets","getting","give","given","gives","giving","go","goes",
											"gone","got","gotten","h","had","happens","hardly","has","hasn't","have","haven't",
											"having","he","hed","hence","her","here","hereafter","hereby","herein","heres","hereupon",
											"hers","herself","hes","hi","hid","him","himself","his","hither","home","how","howbeit","however",
											"hundred","i","id","ie","if","i'll","im","immediate","immediately","importance","important","in","inc","indeed","index","information",
											"instead","into","invention","inward","is","isn't","it","itd","it'll","its","itself",
											"i've","j","just","k","keep","keeps","kept","kg","km","know","known","knows","l","largely",
											"last","lately","later","latter","latterly","least","less","lest","let","lets","like","liked",
											"likely","line","little","'ll","look","looking","looks","ltd","m","made","mainly","make","makes","many",
											"may","maybe","me","mean","means","meantime","meanwhile","merely","mg","might","million","miss","ml","more",
											"moreover","most","mostly","mr","mrs","much","mug","must","my","myself","n","na","name","namely",
											"nay","nd","near","nearly","necessarily","necessary","need","needs","neither","never","nevertheless",
											"new","next","nine","ninety","no","nobody","non","none","nonetheless","noone","nor","normally","nos","not",
											"noted","nothing","now","nowhere","o","obtain","obtained","obviously","of","off","often","oh","ok","okay","old","omitted",
											"on","once","one","ones","only","onto","or","ord","other","others","otherwise","ought","our","ous","ourselves",
											"ot","outside","over","overall","owing","own","p","page","pages","part","particular","particularly",
											"past","per","perhaps","placed","please","plus","poorly","possible","possibly","potentially",
											"pp","predominantly","present","previously","primarily","probably","promptly","proud","provides",
											"put","q","que","quickly","quite","qv","r","ran","rather","rd","re","readily","really","recent",
											"recently","ref","refs","regarding","regardless","regards","related","relatively","research",
											"respectively","resulted","resulting","results","right","run","s","said","same","saw","say",
											"saying","says","sec","section","see","seeing","seem","seemed","seeming","seems","seen","self",
											"selves","sent","seven","several","shall","she","shed","she'll","shes","should","shouldn't",
											"show","showed","shown","showns","shows","significant","significantly","similar","similarly",
											"since","six","slightly","so","some","somebody","somehow","someone","somethan","something",
											"sometime","sometimes","somewhat","somewhere","soon","sorry","specifically","specified","specify",
											"specifying","still","stop","strongly","sub","substantially","successfully","such","sufficiently","suggest",
											"sup","sure","t","take","taken","taking","tell","tends","th","than","thank","thanks","thanx","that",
											"that'll","thats","that've","the","their","theirs","them","themselves","then","thence","there",
											"thereafter","thereby","thered","therefore","therein","there'll","thereof","therere","theres",
											"thereto","thereupon","there've","these","they","theyd","they'll","theyre","they've","think",
											"this","those","thou","though","thoughh","thousand","throug","through","throughout","thru","thus",
											"til","tip","to","together","too","took","toward","towards","tried","tries","truly","try","trying",
											"ts","twice","two","u","un","under","unfortunately","unless","unlike","unlikely","until","unto","up",
											"upon","ups","us","use","used","useful","usefully","usefulness","uses","using","usually","v","value",
											"various","'ve","very","via","viz","vol","vols","vs","w","want","wants","was","wasnt","way","we","wed",
											"welcome","we'll","went","were","werent","we've","what","whatever","what'll","whats","when","whence",
											"whenever","where","whereafter","whereas","whereby","wherein","wheres","whereupon","wherever","whether",
											"which","while","whim","whither","who","whod","whoever","whole","who'll","whom","whomever","whos","whose",
											"why","widely","willing","wish","with","within","without","wont","words","world","would","wouldnt","www",
											"x","y","yes","yet","you","youd","you'll","your","youre","yours","yourself","yourselves","you've","z","zero");
	private static final CharArraySet stopSet = new CharArraySet(stopWords, true);
	private static TextIndexer instance = null;
	private Directory storeDirectory;
	private Analyzer analyzer;
	private IndexWriterConfig config;
	private IndexReader reader;
	private IndexSearcher searcher;
	private IndexWriter writer;
	private int displayNumber;
	private XauroraParser parser;
	//Singleton constructor
	private TextIndexer(){
		this.init();
	}
	
	private void init(){
		try {
			this.analyzer = new StandardAnalyzer();
			this.storeDirectory = FSDirectory.open(Paths.get(DataFileIO.instanceOf().getIndexDirectory()));
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.displayNumber = DEFAULT_DISPLAY_NUMBER;
		
	}
	public static TextIndexer getInstance(){
		if(instance == null){
			instance = new TextIndexer();
		}
		return instance;
	}
	//Description: After receiving text data from browser plugin, an entity
	//of the indexing system will be created. For ease of deleting and retrieving
	//We identify an entity from its source URL and delete it by knowing its actual 
	//datafile filename
	//Pre-condition: A string typed text data, the source url in String and its respective datafile
	//filename.
	//Post-condition: An entity of the indexing System will be created
	public synchronized void createIndexDocumentFromWeb(String rawData,String url,String filename)
	{
		//System.out.println("Creation Start!\n");
		this.analyzer = new StandardAnalyzer();
		this.config = new IndexWriterConfig(this.analyzer);
		
		long lStartTime = System.currentTimeMillis();
		String[] paragraphs = rawData.split("\n");
		
		String sourceHost = getHostFromURL(url);
		try {
			this.writer = new IndexWriter(this.storeDirectory,this.config);

			for(int i = 1;i<paragraphs.length;i++)
			{
				if(paragraphs[i].replaceAll("[^\\x00-\\x7F]","").trim().equals(""))
					continue;
				paragraphs[i] = paragraphs[i].replaceAll("[^\\x00-\\x7F]","").trim();
				String[] sentences = paragraphs[i].split("[.|!|?]+ ");
				for(int j = 0; j<sentences.length;j++){
					Document data = new Document();
					addURL(data,url);
					addSource(data,sourceHost);
					addFilename(data,filename.replace(DATAFILE_EXTENSION, ""));
					insertSentencesIntoDocument(sentences,j,data);
					extractNumbersAndEmails(sentences, j, data);
					extractTerms(sentences[j],data);
					insertParagraphIntoDocument(paragraphs[i],data);
					this.writer.addDocument(data);
				}
			}
			this.writer.commit();
			long lEndTime = System.currentTimeMillis();
			long difference = lEndTime - lStartTime;

			System.out.println("Elapsed milliseconds: " + difference);
			this.writer.close();
			//printAll();

		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	//Description: Extracting different terms and stores terms into a Lucene document
	//Pre-condition: The String text and the lucene document that stores all the data
	//Post-condition: the data stores inside the document object
	private void extractTerms(String input,Document data){
		TokenStream stream = this.analyzer.tokenStream(FIELD_TERMS, new StringReader(input.toLowerCase().trim()));
		stream = new StopFilter(stream, stopSet);
	    CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
	    try {
			stream.reset();
			while(stream.incrementToken()) {
				addTerms(data,charTermAttribute.toString());
			}
			stream.close();
		} catch (IOException e) {
			// Log here
			e.printStackTrace();
		}
	    
	}
	public ArrayList<String> extractKeyWordsInUserInput(String input){
		ArrayList<String> result = new ArrayList<String>();
		TokenStream stream = this.analyzer.tokenStream(FIELD_TERMS, new StringReader(input.toLowerCase().trim()));
		stream = new StopFilter(stream, stopSet);
	    CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
	    try {
			stream.reset();
			while(stream.incrementToken()) {
				result.add(charTermAttribute.toString());
			}
			stream.close();
		} catch (IOException e) {
			// Log here
			e.printStackTrace();
		}
	    return result;
	}
	//Description: Inserting a sentence into the respective lucene document object
	//Pre-condition: An array of String that storing all sentences within 1 paragraph
	//				and the index of sentence that to be stored, and the respective lucene document
	//Post-condition: The lucene document object will contain the sentence as one of its text fields
	private void insertSentencesIntoDocument(String[] sentences,int i, Document data){
		String input = "";
		input+=sentences[i]+". ";
		addContent(data,input);
	}
	
	//Description: Inserting a sentence into the respective lucene document object
	//Pre-condition: An array of String that storing all sentences within 1 paragraph
	//				and the index of sentence that to be stored, and the respective lucene document
	//Post-condition: The lucene document object will contain the sentence as one of its text fields
	private void insertParagraphIntoDocument(String input, Document data){

		addData(data,input);
	}
	
	//Description: Extracting Numbers and E-mails from a sentence, and store that into
	//				the corresponding lucene document object
	//Pre-condition: An array of String storing all sentence within a paragraph.
	//				 The index of the sentence to be stored
	//				 The respective lucene document object
	//Post-condition: All number/e-mails (if exists) will be stored into the document object.
	private void extractNumbersAndEmails(String[] paragraphs, int i, Document data) {
		//InputStream is = new ByteArrayInputStream(Charset.forName("UTF-8").encode((paragraphs[i]+". ").replaceAll("[^\\x00-\\x7F]", "")).array());
		InputStream is = new ByteArrayInputStream((paragraphs[i]+". ").replaceAll("[^\\x00-\\x7F]", "").getBytes());
		//convert the string into input stream and pass that into the JavaCC parser
		if(this.parser == null){
			this.parser = new XauroraParser(is);
		} else {
			XauroraParser.ReInit(is);
		}
		try {
			XauroraParser.parseEmailInSentence(data);
		} catch (xaurora.text.ParseException e) {

			e.printStackTrace();
		}
	}
	//Description: Helper method that retrieve a hostname from the URL
	//Pre-condition: A string typed extracted from the browser plug-in
	//Post-condition: return a String storing the hostname if the URL is valid, throws an exception otherwise
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
	//Description: add an URL to a lucene document object.
	//Pre-condition�� A string typed url and the lucene document
	//Post-condition: the url will be added as a text field to the document
	public static void addURL(Document doc,String url)
	{
		addTextField(doc,FIELD_URL,url);
	}
	//Description: add an source host name to a lucene document object.
	//Pre-condition�� A string typed storing the hostname of the source and the lucene document
	//Post-condition: the hostname will be added as a text field to the document
	public static void addSource(Document doc,String source)
	{
		addTextField(doc,FIELD_SOURCE,source);
	}
	//Description: add the filename of the datafile to a lucene document object.
	//Pre-condition�� A string storing the filename of the text datafile and the lucene document
	//Post-condition: the filename will be added as a text field to the document
	public static void addFilename(Document doc,String filename)
	{
		addTextField(doc,FIELD_FILENAME,filename);
	}
	//Description: add the text content of a sentence to a lucene document object.
	//Pre-condition�� A string storing the text content and the lucene document
	//Post-condition: the text will be added as a String field to the document
	public static void addContent(Document doc,String content)
	{
		addStringField(doc,FIELD_CONTENT,content);
	}
	//Description: add the text content of a paragraph to a lucene document object.
	//Pre-condition�� A string storing the text content and the lucene document
	//Post-condition: the text will be added as a String field to the document
	public static void addData(Document doc,String content)
	{
		addStringField(doc,FIELD_EXTENDED_DATA,content);
	}
	//Description: add a number to a lucene document object.
	//Pre-condition�� A string storing the number and the lucene document
	//Post-condition: the number will be added as a text field to the document
	public static void addNumber(Document doc,String number)
	{
		addTextField(doc,FIELD_NUMBER,number);
	}
	//Description: add an E-mail address to a lucene document object.
	//Pre-condition�� A string storing the e-mail address and the lucene document
	//Post-condition: the e-mail address will be added as a text field to the document
	public static void addEmail(Document doc,String email)
	{
		addTextField(doc,FIELD_EMAIL,email);
	}
	//Description: add a keyword extracted from the text to a lucene document object.
	//Pre-condition�� A string keyword and the lucene document
	//Post-condition: the keyword will be added as a String field to the document
	public static void addTerms(Document doc,String term)
	{
		addTextField(doc,FIELD_TERMS,term);
	}
	//Description: add a text field to a lucene document object.
	//Pre-condition�� A string storing the data and the lucene document
	//Post-condition: the text field will be added as a String field to the document
	private static void addTextField(Document doc,String field,String value)
	{
		doc.add(new TextField(field,value,Field.Store.YES));
	}
	//Description: add a String field to a lucene document object.
	//Pre-condition�� A string storing the text content and the lucene document
	//Post-condition: the text will be added as a String field to the document
	private static void addStringField(Document doc,String field,String value)
	{
		doc.add(new StringField(field,value,Field.Store.YES));
	}
	//Description: delete an entity in the lucene indexing system and removing its
	//				actual datafile
	//pre-condition: The field needs to search and the input query in String
	//post-condition: The entity and its corresponding datafile will be deleted
	//				  if found in the index system.
	public synchronized void deleteByField(String field,String inputQuery){
		
		this.analyzer = new StandardAnalyzer();
		
		try {
			this.reader  = DirectoryReader.open(this.storeDirectory);
			this.searcher = new IndexSearcher(this.reader);
			
			Query deleteQuery = new QueryParser(field,this.analyzer).parse(inputQuery.replace(DATAFILE_EXTENSION,""));
			
			TopDocs docs = this.searcher.search(deleteQuery, this.searcher.count(deleteQuery));
			for(int i = 0;i<docs.totalHits;i++){
				int id = docs.scoreDocs[i].doc;
				String filename = this.searcher.doc(id).get(FIELD_FILENAME);
				DataFileIO.instanceOf().removeDataFile(filename);
			}
			this.reader.close();
			this.analyzer = new StandardAnalyzer();
			this.config = new IndexWriterConfig(this.analyzer);
			this.writer = new IndexWriter(this.storeDirectory,this.config);
			this.writer.deleteDocuments(deleteQuery);
			this.writer.close();
			System.out.println("DELETE COMPLETE");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	//Description: Setting the number of result in the search (should be called by UI)
	//Pre-condition: an int number 0<number< 10?
	//Post-condition: the number of result to be return will be changed
	public void setDisplayNumber(int number){
		this.displayNumber = number;
	}
	//Description: Searching the indexing System to retrieve the text content
	//Pre-condition: a valid Lucene Search Query
	//Post-condition: returning an ArrayList of String which stores the content of all matching documents
	public synchronized ArrayList<String> searchDocument(Query q){
		System.out.println(q.toString());
		ArrayList<String> result = new ArrayList<String>();
		try {
			this.reader  = DirectoryReader.open(this.storeDirectory);
			this.searcher = new IndexSearcher(this.reader); 
			
			TopDocs docs = this.searcher.search(q, DEFAULT_DISPLAY_NUMBER);
			System.out.println(docs.totalHits);
			for(int i = 0;i<DEFAULT_DISPLAY_NUMBER;i++){
				int id = docs.scoreDocs[i].doc;
				String content = this.searcher.doc(id).get(FIELD_EXTENDED_DATA);
				result.add(content);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public synchronized void printAll(){
		try {
			this.reader  = DirectoryReader.open(this.storeDirectory);
			for (int i=0; i<reader.maxDoc(); i++) {
			    

			    Document doc = reader.document(i);
			    String docId = doc.get("content");
			    System.out.println(docId);
			    // do something with docId here...
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
