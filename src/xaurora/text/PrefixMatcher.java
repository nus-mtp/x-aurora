package xaurora.text;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

/**
 * Description: This class handles the query construction of searching query of
 * a given text input from the user.
 * 
 * @author GAO RISHENG A10101891L
 */
public class PrefixMatcher {
    private static final String DEFAULT_INVALID_OUTPUT = "INVALID";
    private static final String FIELD_FILENAME = "s_filename";
    private static final String ERR_MSG_FAIL_TO_PARSE_QUERY = "Error occurs in parsing the search query, the error message is ";
    private static final String MSG_NEW_NORMAL_QUERY = "New Normal Term/Prefix Query is created with query string ";
    private static final String MSG_NEW_NUMBER_QUERY = "New Number Query is created with query string ";
    private static final String MSG_NEW_EMAIL_QUERY = "New Email Query is created with query string ";
    private static final String MSG_NEW_SEARCH_REQUEST_RECEIVED = "New search request received, the user input is {}";
    private static final int CAPITAL_DIFFERENCE = 55;
    private static final int CAPTICAL_BOUNDARY = 36;
    private static final int DITIT_BOUNDARY = 10;
    private static final int EMAIL_LEGAL_CHARACTERS = 62;
    private static final int INDEX_ONE = 1;
    private static final int INDEX_ZERO = 0;
    private static final int LOWER_DIFFERENCE = 61;
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_EMAIL = 1;
    private static final int TYPE_NUMBER = 2;
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String E_MAIL_ADDRESS_KEYWORD_EXPRESSION = "(e(-)?mail( address)?)|contact";
    private static final String FIELD_TO_SEARCH_KEYWORD = "content";
    private static final String FIELD_EMAIL = "email";
    private static final String QUOTATION_STRING = "\"";
    private static final String NUMBER_SEARCH_KEYWORD = "number";
    private static final String FIELD_NUMBER = NUMBER_SEARCH_KEYWORD;
    private static final String WILDCARD = "*";
    private static final String PRESET_NUMBER_SEARCH_INPUT = "number:";
    private static final String PRESET_EMAIL_SEARCH_INPUT = "email:";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String NEW_EMPTY_STRING = "";
    private static final String SPECIAL_CHARACTERS = "~[0-9,a-z]";
    private static Logger logger = Logger.getLogger(PrefixMatcher.class);

    /**
     * Description: The method that receive an user text input and returns the
     * search result.
     * 
     * @param userInput,
     *            the raw text input from the user
     * 
     * @return all the relevance search result in the indexing system in Strings
     * @author GAO RISHENG A0101891L
     */
    public static final ArrayList<String> getResult(String userInput,
            TextIndexer indexer) {
        ArrayList<String> suggestion = new ArrayList<String>();
        logger.info(MSG_NEW_SEARCH_REQUEST_RECEIVED + userInput);
        if (userInput.equals(PRESET_NUMBER_SEARCH_INPUT)) {
            suggestion = generateNumberQuery(suggestion, indexer);
        } else if (userInput.equals(PRESET_EMAIL_SEARCH_INPUT)) {
            suggestion = generateEmailQuery(suggestion, indexer);
        } else {
            suggestion = generatreNormalQuery(userInput, suggestion, indexer);
        }
        return suggestion;
    }

    /**
     * Description: retrieve the next paragraph content
     * 
     * @param filename,
     *            the filename of a document
     * @param index,
     *            the index of next paragraph
     * @param indexer,
     *            the text indexer instance
     * @return the content of the next paragraph
     * @author GAO RISHENG A0101891L
     */
    public static final String getResult(String filename, int index,
            TextIndexer indexer) {
        ArrayList<String> suggestion = new ArrayList<String>();
        suggestion = generateNextQuery(filename, index, suggestion, indexer);
        if(index>suggestion.size())
        return DEFAULT_INVALID_OUTPUT;
        else
            return suggestion.get(index-1);
    }

    /**
     * Description: Generate a query that search all e-mails that stores in the
     * indexing system
     * 
     * @param suggestion,
     *            the ArrayList that used to store the result in String
     * @return the search results in String
     * @author GAO RISHENG A0101891L
     */
    private static final ArrayList<String> generateEmailQuery(
            ArrayList<String> suggestion, TextIndexer indexer) {
        String[] queries = new String[EMAIL_LEGAL_CHARACTERS];
        String[] fields = new String[EMAIL_LEGAL_CHARACTERS];
        for (int i = INDEX_ZERO; i < EMAIL_LEGAL_CHARACTERS; i++) {
            fields[i] = FIELD_EMAIL;
            if (i < DITIT_BOUNDARY) {
                queries[i] = String.valueOf(i) + WILDCARD;
            } else if (i < CAPTICAL_BOUNDARY) {
                queries[i] = String.valueOf((char) (i + CAPITAL_DIFFERENCE))
                        + WILDCARD;
            } else {
                queries[i] = String.valueOf((char) (i + LOWER_DIFFERENCE))
                        + WILDCARD;
            }

        }
        try {

            Query searchQuery = MultiFieldQueryParser.parse(queries, fields,
                    new StandardAnalyzer());
            logger.debug(MSG_NEW_EMAIL_QUERY + searchQuery.toString());
            suggestion = indexer.searchDocument(searchQuery, TYPE_EMAIL);
        } catch (ParseException e) {

            logger.error(ERR_MSG_FAIL_TO_PARSE_QUERY, e);
        }
        return suggestion;
    }

    /**
     * 
     * Description: generate a query that search all numbers appeared in all
     * documents
     * 
     * @param suggestion,
     *            ArrayList that used to store all the search results
     * @return the search result
     * @author GAO RISHENG A0101891L
     */
    private static final ArrayList<String> generateNumberQuery(
            ArrayList<String> suggestion, TextIndexer indexer) {
        String[] queries = new String[DITIT_BOUNDARY];
        String[] fields = new String[DITIT_BOUNDARY];
        for (int i = INDEX_ZERO; i < DITIT_BOUNDARY; i++) {
            fields[i] = FIELD_NUMBER;
            queries[i] = String.valueOf(i) + WILDCARD;
        }
        try {

            Query searchQuery = MultiFieldQueryParser.parse(queries, fields,
                    new StandardAnalyzer());
            logger.debug(MSG_NEW_NUMBER_QUERY + searchQuery.toString());
            suggestion = indexer.searchDocument(searchQuery, TYPE_NUMBER);
        } catch (ParseException e) {

            logger.error(ERR_MSG_FAIL_TO_PARSE_QUERY, e);
        }
        return suggestion;
    }

    /**
     * Description: Search the results within the indexing system
     * 
     * @param userInput,
     *            raw text User Input
     * @param suggestion,
     *            the arrayList that used to store the search result
     * @return all the relevance suggestion obtains from the indexing system
     * @author GAO RISHENG A0101891L
     */
    private static final ArrayList<String> generatreNormalQuery(
            String userInput, ArrayList<String> suggestion,
            TextIndexer indexer) {
        ArrayList<String> keywords = indexer
                .extractKeyWordsInUserInput(userInput);

        String[] queries = constructQuery(userInput, keywords);
        String[] fields = new String[queries.length];
        for (int i = INDEX_ZERO; i < fields.length; i++) {
            fields[i] = FIELD_TO_SEARCH_KEYWORD;
        }
        try {

            Query searchQuery = MultiFieldQueryParser.parse(queries, fields,
                    new StandardAnalyzer());
            logger.debug(MSG_NEW_NORMAL_QUERY + searchQuery.toString());
            suggestion = indexer.searchDocument(searchQuery, TYPE_DEFAULT);
        } catch (ParseException e) {

            logger.error(ERR_MSG_FAIL_TO_PARSE_QUERY, e);
        }
        return suggestion;
    }

    /**
     * Description: search for next paragraph
     * 
     * @param filename,
     *            the filename of a document
     * @param index,
     *            the index of the next paragraph
     * @param suggestions,
     *            the search result
     * @param indexer,
     *            the text indexer instance
     * @return the content of the next paragraph
     * @author GAO RISHENG A0101891L
     */
    private static final ArrayList<String> generateNextQuery(String filename,
            int index, ArrayList<String> suggestions, TextIndexer indexer) {
        Query searchQuery;
        try {
            searchQuery = new QueryParser(FIELD_FILENAME,
                    new StandardAnalyzer()).parse(filename);
            logger.debug(MSG_NEW_NORMAL_QUERY + searchQuery.toString());
            suggestions = indexer.searchDocument(searchQuery, TYPE_DEFAULT);
        } catch (ParseException e) {
            logger.error(ERR_MSG_FAIL_TO_PARSE_QUERY, e);
        }

        return suggestions;
    }

    /**
     * @param userInput,
     *            the user query
     * @return true only if the user input contains searching keywords for
     *         numbers, false otherwise
     * @author GAO RISHENG A0101891L
     */
    private static final boolean isContainNumberKeyWords(String userInput) {
        return userInput.contains(NUMBER_SEARCH_KEYWORD);
    }

    /**
     * @param userInput,
     *            the user query
     * @return true only if it contains email keywords, false otherwise
     * @author GAO RISHENG A0101891L
     */
    private static final boolean isContainEmailKeyWords(String userInput) {
        Pattern p = Pattern.compile(E_MAIL_ADDRESS_KEYWORD_EXPRESSION);
        Matcher m = p.matcher(userInput);
        return m.find();

    }

    /**
     * Description: Generate the Array of Query Strings from the user Input
     * which supports both prefix search and fuzzy term search
     * 
     * @param userInput,
     *            the user input text received from text
     * @param keywords,
     *            arrayList of keywords extracted from the raw text, avoid
     *            searching words like 'the','i' etc in the term searching
     * @return The array of String queries
     * 
     * @author GAO RISHENG
     */
    private static final String[] constructQuery(String userInput,
            ArrayList<String> keywords) {

        int length = keywords.size();
        if (isContainEmailKeyWords(userInput)) {
            length++;
        }
        if (isContainNumberKeyWords(userInput)) {
            length++;
        }
        length++;
        String[] queries = new String[length];
        int currentIndex = INDEX_ONE;
        queries[INDEX_ZERO] = QUOTATION_STRING + userInput + QUOTATION_STRING;

        for (int i = INDEX_ZERO; i < keywords.size(); i++) {
            queries[i + currentIndex] = keywords.get(i)
                    .replace(SPECIAL_CHARACTERS, NEW_EMPTY_STRING) + WILDCARD;
        }
        return queries;
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
