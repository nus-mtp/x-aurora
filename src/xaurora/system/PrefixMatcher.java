package xaurora.system;

import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xaurora.text.TextIndexer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;

public class PrefixMatcher {
	private static final String FIELD_TO_SEARCH_KEYWORD = "content";
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_NUMBER = "number";
	private static final int TYPE_DEFAULT = 0;
	private static final int TYPE_EMAIL = 1;
	private static final int TYPE_NUMBER = 2;
	private static final double FUZZY_ALLOWANCE = 1.5;
	public static ArrayList<String> getResult(String userInput){
		ArrayList<String> suggestion = new ArrayList<String>();
		if(userInput.equals("number:")){
			suggestion = generateNumberQuery(suggestion);
		} else if(userInput.equals("email:")){
			suggestion = generateEmailQuery(suggestion);
		} else {
			suggestion = generatreNormalQuery(userInput, suggestion);
		}
		return suggestion;
	}
	
	private static ArrayList<String> generateEmailQuery(ArrayList<String> suggestion){
		String[] queries = new String[62];
		String[] fields = new String[62];
		for(int i = 0;i<62;i++){
			fields[i] = FIELD_EMAIL;
			if(i<10){
				queries[i] = String.valueOf(i)+"*";
			} else if(i<36){
				queries[i] = String.valueOf((char)(i+55))+"*";
			} else {
				queries[i] = String.valueOf((char)(i+61))+"*";
			}
			
		}
		try {
			
			Query searchQuery = MultiFieldQueryParser.parse(queries, fields, new StandardAnalyzer());
			
			suggestion = TextIndexer.getInstance().searchDocument(searchQuery,TYPE_EMAIL);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return suggestion;
	}
	private static ArrayList<String> generateNumberQuery(ArrayList<String> suggestion){
		String[] queries = new String[10];
		String[] fields = new String[10];
		for(int i = 0;i<10;i++){
			fields[i] = FIELD_NUMBER;
			queries[i] = String.valueOf(i)+"*";	
		}
		try {
			
			Query searchQuery = MultiFieldQueryParser.parse(queries, fields, new StandardAnalyzer());
			
			suggestion = TextIndexer.getInstance().searchDocument(searchQuery,TYPE_NUMBER);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return suggestion;
	}


	private static ArrayList<String> generatreNormalQuery(String userInput, ArrayList<String> suggestion) {
		ArrayList<String> keywords = TextIndexer.getInstance().extractKeyWordsInUserInput(userInput);

		String[] queries = constructQuery(userInput,keywords);
		String[] fields = new String[queries.length];
		for(int i = 0; i<fields.length;i++){
			fields[i] = FIELD_TO_SEARCH_KEYWORD;
		}
		try {
			
			Query searchQuery = MultiFieldQueryParser.parse(queries, fields, new StandardAnalyzer());
			
			suggestion = TextIndexer.getInstance().searchDocument(searchQuery,TYPE_DEFAULT);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return suggestion;
	}




	/**
	 * Calculate fuzziness value of a string using the length of the string.   
	 *
	 * @param  word  	String object.
	 * @return			Fuzzy value of the the string.
	 */
	private static String calcFuzzy(String word){
		double wordLength = word.length();
		
		if(wordLength == 1) // off fuzziness for one letter word
			return ""; 
		else if(FUZZY_ALLOWANCE > wordLength)
			return "~0.0";
		else
			return "~"+(wordLength-FUZZY_ALLOWANCE)/wordLength;
	}
	private static boolean isContainNumberKeyWords(String userInput){
		return userInput.contains("number");
	}
	private static boolean isContainEmailKeyWords(String userInput){
		Pattern p = Pattern.compile("(e(-)?mail( address)?)|contact");
		Matcher m = p.matcher(userInput);
		return m.find();
		
	}
	private static String[] constructQuery(String userInput,ArrayList<String> keywords){
		
		int length = keywords.size();
		if(isContainEmailKeyWords(userInput)){
			length++;
		}
		if(isContainNumberKeyWords(userInput)){
			length++;
		}
		length++;
		String[] queries = new String[length];
		int currentIndex = 1;
		queries[0] = "\""+userInput+"\"";

		for(int i = 0;i<keywords.size();i++){
			queries[i+currentIndex] = keywords.get(i).replace("~[0-9,a-z]","")+calcFuzzy(keywords.get(i).replace("~[0-9,a-z]","")); 
		}
		return queries;
	}
}
