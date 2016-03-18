package xaurora.text;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

/**
 * Description: This class handles the query construction of searching query of
 * a given text input from the user.
 * 
 * @author GAO RISHENG A10101891L
 */
public class PrefixMatcher {

    private static final double FUZZY_ALLOWANCE = 1.5;
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
    public static ArrayList<String> getResult(String userInput) {
        ArrayList<String> suggestion = new ArrayList<String>();
        if (userInput.equals(PRESET_NUMBER_SEARCH_INPUT)) {
            suggestion = generateNumberQuery(suggestion);
        } else if (userInput.equals(PRESET_EMAIL_SEARCH_INPUT)) {
            suggestion = generateEmailQuery(suggestion);
        } else {
            suggestion = generatreNormalQuery(userInput, suggestion);
        }
        return suggestion;
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
    private static ArrayList<String> generateEmailQuery(
            ArrayList<String> suggestion) {
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

            suggestion = TextIndexer.getInstance().searchDocument(searchQuery,
                    TYPE_EMAIL);
        } catch (ParseException e) {

            e.printStackTrace();
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
    private static ArrayList<String> generateNumberQuery(
            ArrayList<String> suggestion) {
        String[] queries = new String[DITIT_BOUNDARY];
        String[] fields = new String[DITIT_BOUNDARY];
        for (int i = INDEX_ZERO; i < DITIT_BOUNDARY; i++) {
            fields[i] = FIELD_NUMBER;
            queries[i] = String.valueOf(i) + WILDCARD;
        }
        try {

            Query searchQuery = MultiFieldQueryParser.parse(queries, fields,
                    new StandardAnalyzer());

            suggestion = TextIndexer.getInstance().searchDocument(searchQuery,
                    TYPE_NUMBER);
        } catch (ParseException e) {

            e.printStackTrace();
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
    private static ArrayList<String> generatreNormalQuery(String userInput,
            ArrayList<String> suggestion) {
        ArrayList<String> keywords = TextIndexer.getInstance()
                .extractKeyWordsInUserInput(userInput);

        String[] queries = constructQuery(userInput, keywords);
        String[] fields = new String[queries.length];
        for (int i = INDEX_ZERO; i < fields.length; i++) {
            fields[i] = FIELD_TO_SEARCH_KEYWORD;
        }
        try {

            Query searchQuery = MultiFieldQueryParser.parse(queries, fields,
                    new StandardAnalyzer());

            suggestion = TextIndexer.getInstance().searchDocument(searchQuery,
                    TYPE_DEFAULT);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return suggestion;
    }

    /**
     * Inherited from the previous iteration Calculate fuzziness value of a
     * string using the length of the string.
     *
     * @param word
     *            String object.
     * @return Fuzzy value of the the string.
     */
    private static String calcFuzzy(String word) {
        double wordLength = word.length();

        if (wordLength == 1) // off fuzziness for one letter word
            return "";
        else if (FUZZY_ALLOWANCE > wordLength)
            return "~0.0";
        else
            return "~" + (wordLength - FUZZY_ALLOWANCE);
    }

    private static boolean isContainNumberKeyWords(String userInput) {
        return userInput.contains(NUMBER_SEARCH_KEYWORD);
    }

    private static boolean isContainEmailKeyWords(String userInput) {
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
    private static String[] constructQuery(String userInput,
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
                    .replace(SPECIAL_CHARACTERS, NEW_EMPTY_STRING)
                    + calcFuzzy(keywords.get(i).replace(SPECIAL_CHARACTERS,
                            NEW_EMPTY_STRING));
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
