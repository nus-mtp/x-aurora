package xaurora.text;

import java.util.Arrays;
import java.util.List;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

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
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import xaurora.io.DataFileIO;

public class TextIndexer {
	private static final String SOURCE_UNKNOWN = "unknown";
	private static final String FIELD_SOURCE = "source";
	private static final String FIELD_URL = "url";
	private static final String FIELD_CONTENT = "content";
	private static final String FIELD_NUMBER = "number";
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_FILENAME = "filename";
	private static final String FIELD_TERMS = "term";
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
	private TextIndexer(){
		init();
	}
	
	private void init(){
		this.storeDirectory = new RAMDirectory();
		this.analyzer = new StandardAnalyzer();
		this.config = new IndexWriterConfig(analyzer);
		try {
			this.writer = new IndexWriter(this.storeDirectory,config);
			this.reader = DirectoryReader.open(this.storeDirectory);
			this.searcher = new IndexSearcher(this.reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void terminate(){
		try {
			this.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static TextIndexer getInstance(){
		if(instance == null){
			instance = new TextIndexer();
		}
		return instance;
	}
	
	public void createIndexDocumentFromWeb(String rawData,String url,String filename)
	{
		String[] paragraphs = rawData.split("\n");

		String sourceHost = getHostFromURL(url);
		try {
			
			for(int i = 0;i<paragraphs.length;i++)
			{
				String[] sentences = paragraphs[i].split("[.|!|?]+ ");
				for(int j = 0; j<sentences.length;j++){
					Document data = new Document();
					addURL(data,url);
					addSource(data,sourceHost);
					addFilename(data,filename);
					extractEntityContent(sentences,j,data);
					extractNumbersAndEmails(sentences, j, data);
					extractTerms(sentences[j],data);
					this.writer.addDocument(data);
				}
			}
			this.writer.commit();

		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
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
	private void extractEntityContent(String[] sentences,int i, Document data){
		String input = "";
		for(int index = i;index<sentences.length;index++){
			input+=sentences[i]+". ";
		}
		addContent(data,input);
	}
	private void extractNumbersAndEmails(String[] paragraphs, int i, Document data) {
		InputStream is = new ByteArrayInputStream(Charset.forName("UTF-8").encode(paragraphs[i]).array());
		XauroraParser paragraphParser = new XauroraParser(is);
		try {
			XauroraParser.parseEmailInParagraph(data);
		} catch (xaurora.text.ParseException e) {

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
	public static void addTerms(Document doc,String term)
	{
		addTextField(doc,FIELD_NUMBER,term);
	}
	private static void addTextField(Document doc,String field,String value)
	{
		doc.add(new TextField(field,value,Field.Store.YES));
	}

	private static void addStringField(Document doc,String field,String value)
	{
		doc.add(new StringField(field,value,Field.Store.YES));
	}
	
	public void deleteBySource(String field,String inputQuery){
		Analyzer analyzer = new StandardAnalyzer();
		
		try {
			Query deleteQuery = new QueryParser(field,analyzer).parse(inputQuery);
			TopDocs docs = this.searcher.search(deleteQuery, this.searcher.count(deleteQuery));
			for(int i = 0;i<docs.totalHits;i++){
				int id = docs.scoreDocs[i].doc;
				String filename = this.searcher.doc(id).get(FIELD_FILENAME);
				DataFileIO.instanceOf().removeDataFile(filename);
			}
			this.writer.deleteDocuments(deleteQuery);
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
}
