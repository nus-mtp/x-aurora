/**
 * This is the indexing component of the Xaurora software which is in charge of 
 * indexing the unprocessed text data extracted from the web pages and handling 
 * the CRUD queries to the indexing system 
 *  
 *   @author GAO RISHENG
 */

package xaurora.text;

/*
 * Basic Data Structure
 */
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
/*
 * Required I/O
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
/*
 * Indexing Library 
 */
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
/*
 *  Log Library
 */
import org.apache.log4j.Logger;
/*
 * Software Internal Import
 */
import xaurora.io.DataFileIO;
import xaurora.ui.Message;

public final class TextIndexer {

    private static final int INDEX_ONE = 1;
    /**
     * Constant used
     */
    private static final String ERR_MSG_UNABLE_TO_ACCESS_INDEXING_SYSTEM = "Unable to access local indexing system. Error Message: ";
    private static final String ERR_MSG_INVALID_UPDATE_DISPLAY_NUMBER = "Error occurs at updating non-positive display number .";
    private static final String ERR_MSG_UNABLE_PROCESS_DELETE = "Unable to process deletion at local file. Error Message : ";
    private static final String ERR_MSG_UNABLE_TO_PARSE_DELETE_QUERY = "Unable to process the deletion query with query string and error message {}.";
    private static final String MSG_DELETE_QUERY_CREATED = "A Delete Query is triggered with selection field %s and input %s.";
    private static final String ERR_MSG_FAIL_TO_EXTRACT_URL = "Unable to extract URL from input ";
    private static final String ERR_MSG_FAIL_TO_PARSE_INPUT = "Error occurs at parsing the raw Text Input with error message ";
    private static final String ERR_MSG_FAIL_TO_EXTRACT_KEY_TERMS = "Error occurs at extracting terms within raw text input. Error Message : ";
    private static final String ERR_MSG_FAIL_TO_CREATE_LUCENE_DOCUMENT = "Error occurs at attempting to create a lucene document with error message ";
    private static final String MSG_INIT_COMPLETE = "Initialization Complete.";
    private static final String ERR_MSG_FAIL_TO_OPEN_INDEXING_DIRECTORY = "Error raised at opening the local indexing directory";
    private static final String MSG_NEW_DOCUMENT_STATISTICS = "New Lucene document is created with %d paragraphs and %d sentences.";
    private static final String MSG_START = "An instance of Text Indexer is created. This message should appear only once at every software running flow.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    /**
     * Field Constant
     */
    public static final String FIELD_SEARCH_FILENAME = "s_filename";
    public static final String FIELD_FILENAME = "filename";
    private static final String SOURCE_UNKNOWN = "unknown";
    private static final String FIELD_SOURCE = "source";
    public static final String FIELD_URL = "url";
    public static final String FIELD_CONTENT = "content";
    private static final String FIELD_NUMBER = "number";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_TERMS = "term";
    private static final String FIELD_EXTENDED_DATA = "data";
    private static final String FIELD_LAST_MODIFIED = "last_modified";
    private static final String FIELD_INDEX = "index";
    /**
     * DATA file extension
     */
    private static final String DATAFILE_EXTENSION = ".txt";

    /**
     * Integer Constants
     */
    private static final int DEFAULT_DISPLAY_NUMBER = 5;
    private static final int INDEX_ZERO = 0;
    private static final int INDEX_SCORE = 0;
    private static final int INDEX_LAST_MODIFIED = 1;
    private static final int TYPE_EMAIL_SEARCH = 1;
    private static final int TYPE_NUMBER_SEARCH = 2;
    private static final int NUM_OF_SORT_RULES = 2;
    private static final int MINIMUM_NON_NEGATIVE = 0;
    /**
     * String Constants
     */
    private static final String NEWLINE = "\n";
    private static final String EMPTY_STRING = "";
    private static final String PATTERN_NON_ASCII_CHARACTERS = "[^\\x00-\\x7F]";
    private static final String PATTERN_SENTENCE_TERMINATOR = "[.|!|?]+ ";
    private static final String FILENAME_DELIMITER = "\n*filename*\n";
    private static final String INDEX_DELIMITER = "\n*index*\n";
    // Overwriting the FieldType for lucene indexing system
    // This will be significant to the sorting rule
    private static final FieldType LONG_FIELD_TYPE_STORED_SORTED = new FieldType();
    private Message message = new Message();
    static {
        LONG_FIELD_TYPE_STORED_SORTED.setTokenized(true);
        LONG_FIELD_TYPE_STORED_SORTED.setOmitNorms(true);
        LONG_FIELD_TYPE_STORED_SORTED.setIndexOptions(IndexOptions.DOCS);
        LONG_FIELD_TYPE_STORED_SORTED
                .setNumericType(FieldType.NumericType.LONG);
        LONG_FIELD_TYPE_STORED_SORTED.setStored(true);
        LONG_FIELD_TYPE_STORED_SORTED.setDocValuesType(DocValuesType.NUMERIC);
        LONG_FIELD_TYPE_STORED_SORTED.freeze();
    }

    /**
     * 656 stop_word sets. Credits from http://www.ranks.nl/stopwords
     */
    private static final List<String> stopWords = Arrays.asList("a", "able",
            "about", "above", "abst", "accordance", "according", "accordingly",
            "across", "act", "actually", "added", "adj", "affected",
            "affecting", "affects", "after", "afterwards", "again", "against",
            "ah", "all", "almost", "alone", "along", "already", "also",
            "although", "always", "am", "among", "amongst", "an", "and",
            "announce", "another", "any", "anybody", "anyhow", "anymore",
            "anyone", "anything", "anyway", "anyways", "anywhere", "apparently",
            "approximately", "are", "aren", "arent", "arise", "around", "as",
            "aside", "ask", "asking", "at", "auth", "available", "away",
            "awfully", "b", "back", "be", "became", "because", "become",
            "becomes", "becoming", "been", "before", "beforehand", "begin",
            "beginning", "beginnings", "begins", "behind", "being", "believe",
            "below", "beside", "besides", "between", "beyond", "biol", "both",
            "brief", "briefly", "but", "by", "c", "ca", "came", "can", "cannot",
            "can't", "cause", "causes", "certain", "certainly", "co", "com",
            "come", "comes", "contain", "containing", "contains", "could",
            "couldnt", "d", "date", "did", "didn't", "different", "do", "does",
            "doesn't", "doing", "done", "don't", "down", "downwards", "due",
            "during", "e", "each", "ed", "edu", "effect", "eg", "eight",
            "eighty", "either", "else", "elsewhere", "end", "ending", "enough",
            "especially", "et", "et-al", "etc", "even", "ever", "every",
            "everybody", "everyone", "everything", "everywhere", "ex", "except",
            "f", "far", "few", "ff", "fifth", "first", "five", "fix",
            "followed", "following", "follows", "for", "former", "formerly",
            "forth", "found", "four", "from", "further", "furthermore", "g",
            "gave", "get", "gets", "getting", "give", "given", "gives",
            "giving", "go", "goes", "gone", "got", "gotten", "h", "had",
            "happens", "hardly", "has", "hasn't", "have", "haven't", "having",
            "he", "hed", "hence", "her", "here", "hereafter", "hereby",
            "herein", "heres", "hereupon", "hers", "herself", "hes", "hi",
            "hid", "him", "himself", "his", "hither", "home", "how", "howbeit",
            "however", "hundred", "i", "id", "ie", "if", "i'll", "im",
            "immediate", "immediately", "importance", "important", "in", "inc",
            "indeed", "index", "information", "instead", "into", "invention",
            "inward", "is", "isn't", "it", "itd", "it'll", "its", "itself",
            "i've", "j", "just", "k", "keep", "keeps", "kept", "kg", "km",
            "know", "known", "knows", "l", "largely", "last", "lately", "later",
            "latter", "latterly", "least", "less", "lest", "let", "lets",
            "like", "liked", "likely", "line", "little", "'ll", "look",
            "looking", "looks", "ltd", "m", "made", "mainly", "make", "makes",
            "many", "may", "maybe", "me", "mean", "means", "meantime",
            "meanwhile", "merely", "mg", "might", "million", "miss", "ml",
            "more", "moreover", "most", "mostly", "mr", "mrs", "much", "mug",
            "must", "my", "myself", "n", "na", "name", "namely", "nay", "nd",
            "near", "nearly", "necessarily", "necessary", "need", "needs",
            "neither", "never", "nevertheless", "new", "next", "nine", "ninety",
            "no", "nobody", "non", "none", "nonetheless", "noone", "nor",
            "normally", "nos", "not", "noted", "nothing", "now", "nowhere", "o",
            "obtain", "obtained", "obviously", "of", "off", "often", "oh", "ok",
            "okay", "old", "omitted", "on", "once", "one", "ones", "only",
            "onto", "or", "ord", "other", "others", "otherwise", "ought", "our",
            "ous", "ourselves", "ot", "outside", "over", "overall", "owing",
            "own", "p", "page", "pages", "part", "particular", "particularly",
            "past", "per", "perhaps", "placed", "please", "plus", "poorly",
            "possible", "possibly", "potentially", "pp", "predominantly",
            "present", "previously", "primarily", "probably", "promptly",
            "proud", "provides", "put", "q", "que", "quickly", "quite", "qv",
            "r", "ran", "rather", "rd", "re", "readily", "really", "recent",
            "recently", "ref", "refs", "regarding", "regardless", "regards",
            "related", "relatively", "research", "respectively", "resulted",
            "resulting", "results", "right", "run", "s", "said", "same", "saw",
            "say", "saying", "says", "sec", "section", "see", "seeing", "seem",
            "seemed", "seeming", "seems", "seen", "self", "selves", "sent",
            "seven", "several", "shall", "she", "shed", "she'll", "shes",
            "should", "shouldn't", "show", "showed", "shown", "showns", "shows",
            "significant", "significantly", "similar", "similarly", "since",
            "six", "slightly", "so", "some", "somebody", "somehow", "someone",
            "somethan", "something", "sometime", "sometimes", "somewhat",
            "somewhere", "soon", "sorry", "specifically", "specified",
            "specify", "specifying", "still", "stop", "strongly", "sub",
            "substantially", "successfully", "such", "sufficiently", "suggest",
            "sup", "sure", "t", "take", "taken", "taking", "tell", "tends",
            "th", "than", "thank", "thanks", "thanx", "that", "that'll",
            "thats", "that've", "the", "their", "theirs", "them", "themselves",
            "then", "thence", "there", "thereafter", "thereby", "thered",
            "therefore", "therein", "there'll", "thereof", "therere", "theres",
            "thereto", "thereupon", "there've", "these", "they", "theyd",
            "they'll", "theyre", "they've", "think", "this", "those", "thou",
            "though", "thoughh", "thousand", "throug", "through", "throughout",
            "thru", "thus", "til", "tip", "to", "together", "too", "took",
            "toward", "towards", "tried", "tries", "truly", "try", "trying",
            "ts", "twice", "two", "u", "un", "under", "unfortunately", "unless",
            "unlike", "unlikely", "until", "unto", "up", "upon", "ups", "us",
            "use", "used", "useful", "usefully", "usefulness", "uses", "using",
            "usually", "v", "value", "various", "'ve", "very", "via", "viz",
            "vol", "vols", "vs", "w", "want", "wants", "was", "wasnt", "way",
            "we", "wed", "welcome", "we'll", "went", "were", "werent", "we've",
            "what", "whatever", "what'll", "whats", "when", "whence",
            "whenever", "where", "whereafter", "whereas", "whereby", "wherein",
            "wheres", "whereupon", "wherever", "whether", "which", "while",
            "whim", "whither", "who", "whod", "whoever", "whole", "who'll",
            "whom", "whomever", "whos", "whose", "why", "widely", "willing",
            "wish", "with", "within", "without", "wont", "words", "world",
            "would", "wouldnt", "www", "x", "y", "yes", "yet", "you", "youd",
            "you'll", "your", "youre", "yours", "yourself", "yourselves",
            "you've", "z", "zero");
    private static final CharArraySet stopSet = new CharArraySet(stopWords,
            true);
    private static TextIndexer instance = null;
    /**
     * Directory that stores the indexing files
     */
    private Directory storeDirectory;
    /**
     * This is to analyze the text and mainly for indexing and searching
     * (over-writable)
     */
    private Analyzer analyzer;
    private IndexWriterConfig config;
    /**
     * Input to indexing system
     */
    private IndexWriter writer;
    /**
     * Read from the indexing system
     */
    private IndexReader reader;
    /**
     * Apply searching to the indexing system
     */
    private IndexSearcher searcher;
    /**
     * number of searching records to be displayed
     */
    private int displayNumber;
    /**
     * Parsing text input from raw text extracted from web-browser
     */
    private XauroraParser parser;
    private Logger logger;

    /**
     * Singleton constructor
     */
    private TextIndexer(DataFileIO io) {
        this.logger = Logger.getLogger(this.getClass());
        this.logger.info(MSG_START);
        this.init(io);
    }

    private final void init(DataFileIO io) {
        try {
            this.analyzer = new StandardAnalyzer();
            this.storeDirectory = FSDirectory
                    .open(Paths.get(io.getIndexDirectory()));

        } catch (IOException e1) {
        	message.showError(ERR_MSG_FAIL_TO_OPEN_INDEXING_DIRECTORY);
            this.logger.error(ERR_MSG_FAIL_TO_OPEN_INDEXING_DIRECTORY
                    + io.getIndexDirectory(), e1);
        }
        this.displayNumber = DEFAULT_DISPLAY_NUMBER;
        this.logger.info(MSG_INIT_COMPLETE);
    }

    public static final TextIndexer getInstance(DataFileIO io) {
        if (instance == null) {
            instance = new TextIndexer(io);
        }
        instance.init(io);
        return instance;
    }

    // Description: After receiving text data from browser plugin, an entity
    // of the indexing system will be created. For ease of deleting and
    // retrieving
    // We identify an entity from its source URL and delete it by knowing its
    // actual
    // datafile filename
    // Pre-condition: A string typed text data, the source url in String and its
    // respective datafile
    // filename.
    // Post-condition: An entity of the indexing System will be created
    /**
     * Description: After receiving text data from browser plugin, an entity of
     * the indexing system will be created. For ease of deleting and retrieving
     * We identify an entity from its source URL and delete it by knowing its
     * actual datafile filename
     * 
     * @param rawData,
     *            A string that stores all text data extracted from a webpage
     * @param url,
     *            the URL of the respective webpage
     * @param filename,
     *            the file name of the data file (ALL CHARACTERS IN FILE NAME
     *            MUST BE IN LOWER CASE)
     * @param lastModified,
     *            the time when the file is created.
     * 
     * @author GAO RISHENG A0101891L
     */
    public synchronized final boolean createIndexDocumentFromWeb(
            final String rawData, final String url, final String filename,
            final long lastModified) {
        // Create analyzer for indexing system
        assert rawData != null && filename != null && url != null
                && lastModified > INDEX_ZERO;
        this.analyzer = new StandardAnalyzer();
        this.config = new IndexWriterConfig(this.analyzer);
        boolean isSuccessful = false;
        // split paragraphs
        String[] paragraphs = rawData.split(NEWLINE);
        // retrieving host name from URL
        String sourceHost = getHostFromURL(url);

        try {
            this.writer = new IndexWriter(this.storeDirectory, this.config);
            int counter = INDEX_ZERO;
            int paragraphID = INDEX_ONE;
            for (int i = INDEX_ONE; i < paragraphs.length; i++) {
                // Not storing pure special characters lines
                if (paragraphs[i]
                        .replaceAll(PATTERN_NON_ASCII_CHARACTERS, EMPTY_STRING)
                        .trim().equals(EMPTY_STRING))
                    continue;
                paragraphs[i] = paragraphs[i]
                        .replaceAll(PATTERN_NON_ASCII_CHARACTERS, EMPTY_STRING)
                        .trim();
                // split sentence
                String[] sentences = paragraphs[i]
                        .split(PATTERN_SENTENCE_TERMINATOR);
                for (int j = INDEX_ZERO; j < sentences.length; j++) {
                    // write data in a document object
                    Document data = new Document();
                    addURL(data, url);
                    addSource(data, sourceHost);
                    addFilename(data,
                            filename.replace(DATAFILE_EXTENSION, EMPTY_STRING));
                    addSearchFilename(data,
                            filename.replace(DATAFILE_EXTENSION, EMPTY_STRING));
                    insertSentencesIntoDocument(sentences, j, data);
                    extractNumbersAndEmails(sentences, j, data);
                    addLastModified(data, lastModified);
                    insertParagraphIntoDocument(paragraphs[i], data);
                    addIndex(data, paragraphID);
                    this.writer.addDocument(data);
                    
                    counter++;
                    isSuccessful = true;
                }
                paragraphID++;
            }
            // write the document into the indexing system
            // without this, searching will not be available
            this.writer.commit();
            this.writer.close();
            this.logger.debug(String.format(MSG_NEW_DOCUMENT_STATISTICS,
                    paragraphs.length - INDEX_ONE, counter));

        } catch (IOException e) {
        	message.showError(ERR_MSG_FAIL_TO_CREATE_LUCENE_DOCUMENT);
            this.logger.error(ERR_MSG_FAIL_TO_CREATE_LUCENE_DOCUMENT, e);
        }
        return isSuccessful;
    }

    /**
     * Description: Filtering out all the unnecessary Stop Words from the
     * current raw Text input Mainly for filtering the stop words in the
     * userInput to provider a efficient term searching
     * 
     * @param input,
     *            the raw user input String
     * @return An ArrayList<String> that stores all keywords inside the user
     *         input
     * 
     * @author GAO RISHENG A0101891L
     */
    public final ArrayList<String> extractKeyWordsInUserInput(
            final String input) {
        ArrayList<String> result = new ArrayList<String>();
        // Load the stop words sets
        TokenStream stream = this.analyzer.tokenStream(FIELD_TERMS,
                new StringReader(input.toLowerCase().trim()));
        stream = new StopFilter(stream, stopSet);
        CharTermAttribute charTermAttribute = stream
                .addAttribute(CharTermAttribute.class);
        try {
            stream.reset();
            while (stream.incrementToken()) {
                result.add(charTermAttribute.toString());
            }
            stream.close();
        } catch (IOException e) {
        	message.showError(ERR_MSG_FAIL_TO_EXTRACT_KEY_TERMS);
            this.logger.error(ERR_MSG_FAIL_TO_EXTRACT_KEY_TERMS, e);
        }
        return result;
    }

    /**
     * Description: Inserting a sentence into the respective lucene document
     * object
     * 
     * @param sentences:
     *            An array storing the all sentences within a paragraph
     * @param i,
     *            the index of the sentence to be stored
     * @param data,
     *            a lucene document object
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void insertSentencesIntoDocument(final String[] sentences,
            final int i, Document data) {
        addContent(data, sentences[i]);
    }

    /**
     * Description: Inserting a sentence into the respective lucene document
     * object
     * 
     * @param input,
     *            the String storing the text in a paragraph
     * @param data,
     *            A lucene Document object
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void insertParagraphIntoDocument(final String input,
            Document data) {

        addData(data, input);
    }

    /**
     * Description: Extracting Numbers and E-mails from a sentence, and store
     * that into the corresponding lucene document object.
     * 
     * @param paragraphs,An
     *            array of String storing all sentence within a paragraph.
     * @param i,
     *            The index of the sentence to be stored
     * @param data,A
     *            lucene Document object
     * 
     * @author GAO RISHENG A0101891L
     */
    private void extractNumbersAndEmails(final String[] paragraphs, final int i,
            Document data) {
        InputStream is = new ByteArrayInputStream((paragraphs[i])
                .replaceAll(PATTERN_NON_ASCII_CHARACTERS, EMPTY_STRING)
                .getBytes());
        // convert the string into input stream and pass that into the JavaCC
        // parser
        if (this.parser == null) {
            this.parser = new XauroraParser(is);
        } else {
            XauroraParser.ReInit(is);
        }
        try {
            XauroraParser.parseEmailInSentence(data);
        } catch (xaurora.text.ParseException e) {
        	message.showError(ERR_MSG_FAIL_TO_PARSE_INPUT);
            this.logger.error(ERR_MSG_FAIL_TO_PARSE_INPUT, e);
        }
    }

    /**
     * Description: Helper method that retrieve a host name from the URL
     * 
     * @param url:A
     *            string typed extracted from the browser plug-in
     * @return: A String storing the host name if the URL is valid, throws an
     *          exception otherwise
     * 
     * @author GAO RISHENG A0101891L
     */
    private final String getHostFromURL(final String url) {
        assert url != null;
        String host = SOURCE_UNKNOWN;
        try {
            URL sourceURL = new URL(url);
            host = sourceURL.getHost();
        } catch (MalformedURLException e) {
        	message.showError(ERR_MSG_FAIL_TO_EXTRACT_URL);
            this.logger.error(ERR_MSG_FAIL_TO_EXTRACT_URL + url, e);
        }
        return host;
    }

    /**
     * Description: add an URL to a lucene document object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param url,
     *            A string typed url
     * 
     * @author GAO RISHENG A0101891L
     */

    public final static void addURL(Document doc, final String url) {
        assert url != null;
        addTextField(doc, FIELD_URL, url);
    }

    /**
     * Description: add an source host name to a lucene document object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param source,
     *            A string storing the hostname of the source
     * 
     * @author GAO RISHENG A0101891L
     */

    public final static void addSource(Document doc, final String source) {
        assert source != null;
        addStringField(doc, FIELD_SOURCE, source);
    }

    /**
     * Description: add the filename of the datafile to a lucene document
     * object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param filename,
     *            A string storing the filename of the text datafile
     * 
     * @author GAO RISHENG A0101891L
     */

    public final static void addFilename(Document doc, final String filename) {
        assert filename != null && !filename.trim().equals(EMPTY_STRING);
        addStringField(doc, FIELD_FILENAME, filename);
    }

    /**
     * Description: add the tokenized filename of the datafile to a lucene
     * document object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param filename,
     *            A string storing the filename of the text datafile
     * 
     * @author GAO RISHENG A0101891L
     */
    public final static void addSearchFilename(Document doc,
            final String filename) {
        assert filename != null && !filename.trim().equals(EMPTY_STRING);
        addTextField(doc, FIELD_SEARCH_FILENAME, filename);
    }

    /**
     * Description: add the text content of a sentence to a lucene document
     * object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param content,
     *            A string storing the text content
     * 
     * @author GAO RISHENG A0101891L
     */

    public final static void addContent(Document doc, final String content) {
        assert content != null;
        addTextField(doc, FIELD_CONTENT, content);
    }

    /**
     * Description: add the text content of a paragraph to a lucene document
     * object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param content,
     *            A string storing the text content
     * 
     * @author GAO RISHENG A0101891L
     */

    public final static void addData(Document doc, final String content) {
        assert content != null;
        addStringField(doc, FIELD_EXTENDED_DATA, content);
    }

    /**
     * Description: add a number to a lucene document object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param number,A
     *            string storing the number
     * 
     * @author GAO RISHENG A0101891L
     */
    public final static void addNumber(Document doc, final String number) {
        addStringField(doc, FIELD_NUMBER, number);
    }

    /**
     * Description: add the index of paragraph to a lucene document object
     * 
     * @param doc,
     *            A lucene Document object
     * @param index,
     *            the paragraph index of a sentence
     */
    public final static void addIndex(Document doc, final int index) {
        addIntField(doc, FIELD_INDEX, index);
    }

    /**
     * Description: add an E-mail address to a lucene document object.
     * 
     * @param doc,
     *            A lucene Document object
     * @param email,A
     *            string storing the e-mail address
     * 
     * @author GAO RISHENG A0101891L
     */
    public final static void addEmail(Document doc, final String email) {
        assert email != null;
        addStringField(doc, FIELD_EMAIL, email);
    }

    /**
     * Description: add a keyword extracted from the text to a lucene document
     * object.
     * 
     * @param doc,
     *            A lucene Document object
     * @param term,
     *            A String keyword
     * 
     * @author GAO RISHENG A0101891L
     */
    public final static void addTerms(Document doc, final String term) {
        assert term != null && !term.trim().equals(EMPTY_STRING);
        addTextField(doc, FIELD_TERMS, term);
    }

    /**
     * Description: Adding the last-modified information to the document
     * 
     * @param doc,
     *            A lucene Document object
     * @param value,
     *            A long variable storing the value of last-modified field of
     *            the data file(must >0)
     * 
     * @author GAO RISHENG A0101891L
     */
    public final static void addLastModified(Document doc, final long value) {
        assert value > MINIMUM_NON_NEGATIVE;
        addLongField(doc, FIELD_LAST_MODIFIED, value);
    }

    /**
     * Description: add a text field to a lucene document object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param field,
     *            A String storing the name of the field
     * @param value,A
     *            string storing the text content
     * 
     * @author GAO RISHENG A0101891L
     */
    private final static void addTextField(Document doc, final String field,
            final String value) {
        assert value != null && !value.trim().equals(EMPTY_STRING);
        doc.add(new TextField(field, value, Field.Store.YES));
    }

    /**
     * Description: add a String field to a lucene document object.
     * 
     * @param doc,A
     *            lucene Document object
     * @param field,
     *            A String storing the name of the field
     * @param value,A
     *            string storing the text content
     * 
     * @author GAO RISHENG A0101891L
     */

    private final static void addStringField(Document doc, final String field,
            final String value) {
        assert value != null && !value.trim().equals(EMPTY_STRING);
        doc.add(new StringField(field, value, Field.Store.YES));
    }

    /**
     * Description: Add a long field to a lucene document object with
     * overwritten field setting This is mainly for storing the time when the
     * data was retrieved and use that for sorting (more received result will
     * rank higher)
     * 
     * @param doc,
     *            A lucene Document object
     * @param field,
     *            A String storing the name of the field
     * @param value,
     *            A long typed variable that stores the millisecond number
     *            between the current time and 1970.X.X.XX.XX.XX and the lucene
     *            document
     *
     * @author GAO RISHENG A0101891L
     */
    private final static void addLongField(Document doc, final String field,
            final long value) {
        assert value > MINIMUM_NON_NEGATIVE;
        doc.add(new LongField(field, value, LONG_FIELD_TYPE_STORED_SORTED));
    }

    /**
     * @param doc,
     *            A lucene Document object
     * @param field,
     *            A String storing the name of the field
     * @param value,
     *            The value of the respective field
     * 
     */
    private final static void addIntField(Document doc, final String field,
            final int value) {
        assert value >= MINIMUM_NON_NEGATIVE;
        doc.add(new IntField(field, value, Field.Store.YES));
    }

    /**
     * Description: delete an entity in the lucene indexing system and removing
     * its actual data file
     * 
     * @param The
     *            field needs to search
     * @param Tthe
     *            input query in String Post-condition: The entity and its
     *            corresponding data file will be deleted if found in the index
     *            system.
     * 
     * @author GAO RISHENG A0101891L
     */
    public final synchronized void deleteByField(final String field,
            final String inputQuery) {
        assert inputQuery != null && !inputQuery.trim().equals(EMPTY_STRING);
        assert field != null && !field.trim().equals(EMPTY_STRING);
        this.analyzer = new StandardAnalyzer();
        this.logger.debug(
                String.format(MSG_DELETE_QUERY_CREATED, field, inputQuery));
        try {
            // open the current indexing directory
            // this.reader = DirectoryReader.open(this.storeDirectory);
            // this.searcher = new IndexSearcher(this.reader);

            // Generate the delete query from removing the file extension from
            // the input data file name
            Query deleteQuery = new QueryParser(field, this.analyzer).parse(
                    inputQuery.replace(DATAFILE_EXTENSION, EMPTY_STRING));

            this.logger.debug(String.format(MSG_DELETE_QUERY_CREATED, field,
                    deleteQuery));
            // Remove the respective entities from the indexing system
            // this.reader.close();
            this.analyzer = new StandardAnalyzer();
            this.config = new IndexWriterConfig(this.analyzer);
            this.writer = new IndexWriter(this.storeDirectory, this.config);
            this.writer.deleteDocuments(deleteQuery);
            this.writer.close();
            // change to log
        } catch (ParseException e) {
        	message.showError(ERR_MSG_UNABLE_TO_PARSE_DELETE_QUERY);
            this.logger.error(ERR_MSG_UNABLE_TO_PARSE_DELETE_QUERY, e);
        } catch (IOException e) {
        	message.showError(ERR_MSG_UNABLE_PROCESS_DELETE);
            this.logger.error(ERR_MSG_UNABLE_PROCESS_DELETE, e);
        }
    }

    /**
     * Description: Setting the number of result in the search (should be called
     * by UI)
     * 
     * @param the
     *            number to be set to the display number, an integer number
     *            0<number< 10? Post-condition: the number of result to be
     *            return will be changed
     * @author GAO RISHENG A0101891L
     */
    public final void setDisplayNumber(final int number) {
        if (number <= MINIMUM_NON_NEGATIVE) {
        	message.showError(ERR_MSG_INVALID_UPDATE_DISPLAY_NUMBER);
            this.logger.error(ERR_MSG_INVALID_UPDATE_DISPLAY_NUMBER + number);
        } else
            this.displayNumber = number;
    }

    /**
     * Description: Searching the indexing System to retrieve the text content
     * 
     * @param An
     *            valid lucene search query, an integer identifies the type of
     *            searching
     * @return an ArrayList of String which stores the content of all matching
     *         documents
     * @author GAO RISHENG A0101891L
     */
    public final synchronized ArrayList<String> searchDocument(Query q,
            final int type) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            this.reader = DirectoryReader.open(this.storeDirectory);
            this.searcher = new IndexSearcher(this.reader);
            // configure the sorting rules of the search
            SortField[] filters = setSortingRules();
            Sort sort = new Sort(filters);
            TopDocs docs = this.searcher.search(q, this.displayNumber, sort);
            this.logger.debug(q.toString());
            int searchNumber = Math.min(docs.totalHits, this.displayNumber);
            for (int i = INDEX_ZERO; i < searchNumber; i++) {
                int id = docs.scoreDocs[i].doc;
                String content;
                switch (type) {
                case TYPE_EMAIL_SEARCH: {
                    content = this.searcher.doc(id).get(FIELD_EMAIL);
                    break;
                }
                case TYPE_NUMBER_SEARCH: {
                    content = this.searcher.doc(id).get(FIELD_NUMBER);
                    break;
                }
                default: {
                    content = this.searcher.doc(id).get(FIELD_EXTENDED_DATA);
                    break;
                }
                }
                result.add(content + FILENAME_DELIMITER
                        + this.searcher.doc(id).get(FIELD_FILENAME)
                        + INDEX_DELIMITER
                        + this.searcher.doc(id).get(FIELD_INDEX));
            }

        } catch (IOException e) {
        	message.showError(ERR_MSG_UNABLE_TO_ACCESS_INDEXING_SYSTEM);
            this.logger.error(ERR_MSG_UNABLE_TO_ACCESS_INDEXING_SYSTEM, e);
        }
        return result;
    }

    /**
     * Description: Setting the sorting rules for search results, pre-condition:
     * All search field defined must be indexable and not tokenized
     * 
     * @return an array of SortFields which define the sorting rule of the
     *         search
     * 
     * @author GAO RISHENG A0101891L
     */
    private final SortField[] setSortingRules() {
        SortField[] filters = new SortField[NUM_OF_SORT_RULES];
        filters[INDEX_SCORE] = SortField.FIELD_SCORE;
        filters[INDEX_LAST_MODIFIED] = new SortField(FIELD_LAST_MODIFIED,
                SortField.Type.LONG, false);
        return filters;
    }

    /**
     * Description: Testing Helper Methods that prints all content of all
     * entries in the indexing system
     * 
     * @author GAO RISHENG A0101891L
     * 
     */
    public final synchronized void printAll() {
        try {
            this.reader = DirectoryReader.open(this.storeDirectory);
            for (int i = INDEX_ZERO; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                String docId = doc.get(FIELD_CONTENT);
                System.out.println(docId);
                // do something with docId here...
            }
        } catch (IOException e) {
        	message.showError(ERR_MSG_UNABLE_TO_ACCESS_INDEXING_SYSTEM);
            this.logger.error(ERR_MSG_UNABLE_TO_ACCESS_INDEXING_SYSTEM, e);
        }
    }

    /**
     * Description: normal getter function to allow access to the index reader
     * instance
     * 
     * @return the index reader instance that is stored locally
     */
    public IndexReader getReader() {
        try {
            this.reader = DirectoryReader.open(this.storeDirectory);
        } catch (IOException e) {
            // Testing method may not require log
            e.printStackTrace();
        }
        return this.reader;
    }

    /**
     * Secure Programming. Making this Object not-clonable. Object.clone()
     * allows cloning the data of an object without initialize it which may leak
     * the chances for attacker to access the data internally
     * 
     * @Author GAO RISHENG A0101891L
     */
    public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
    }

    /**
     * Secure Programming. Disable the serialize option of the object which
     * avoid attacker to print the object in serialize manner and inspect the
     * internal status of the object
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void writeObject(ObjectOutputStream out)
            throws java.io.IOException {
        throw new java.io.IOException(SECURITY_MSG_DISABLE_SERIALIZE);
    }

    /**
     * Secure Programming. Disable the de-serialize option of the object which
     * avoid attacker to de-serialize the object stores in the file system and
     * inspect the internal status of the object
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void readObject(ObjectInputStream in)
            throws java.io.IOException {
        throw new java.io.IOException(CLASS_CANNOT_BE_DESERIALIZED);
    }
}
