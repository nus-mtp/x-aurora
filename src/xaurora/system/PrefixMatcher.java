package xaurora.system;

import java.nio.charset.Charset;
import java.util.*;
import xaurora.text.TextIndexer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class PrefixMatcher {
	private static final String FIELD_TO_SEARCH_KEYWORD = "content";
	private static final String FIELD_TO_SEARCH_STRING = "data";
	private static final double FUZZY_ALLOWANCE = 1.5;
	private static final String UNICODE_LEFT_DOUBLE_QUOTE = "\u201C";
	private static final String UNICODE_RIGHT_DOUBLE_QUOTE = "\u201D";
	private static final String WILDCARD = "*";
	public static ArrayList<String> getResult(String userInput){
		ArrayList<String> suggestion = new ArrayList<String>();
		String[] words = extractKeyWords(userInput);

		String[] queries = new String[2];
		String[] fields = new String[2];
		queries[0] = constructFuzzyQuery(words);
		queries[1] = userInput+calcFuzzy(userInput);
		fields[0] = FIELD_TO_SEARCH_KEYWORD;
		fields[1] = FIELD_TO_SEARCH_STRING;
		try {
			Query searchQuery = MultiFieldQueryParser.parse(queries, fields, new StandardAnalyzer());
			suggestion = TextIndexer.getInstance().searchDocument(searchQuery);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return suggestion;
	}
	private static String constructFuzzyQuery(String[] keywords){
		StringBuilder builder = new StringBuilder();
		
		for(int index = 0;index<keywords.length;index++){
			String word = keywords[index].trim();
			word = removeQuotationCharacter(word);
			String fuzziness = calcFuzzy(word);
			word = QueryParser.escape(word);
			if(word.equals("")){
				continue;
			}
			builder.append(word);
			builder.append(fuzziness);
			builder.append(" ");
		}
		builder.append(WILDCARD);
		return builder.toString();
	}
	private static String[] extractKeyWords(String input){
		return input.toLowerCase().trim().split(" ");
	}
	private static String removeQuotationCharacter(String input){
		input = input.replaceAll(unicodeToUTF8(UNICODE_LEFT_DOUBLE_QUOTE), "\"");
		input = input.replaceAll(unicodeToUTF8(UNICODE_RIGHT_DOUBLE_QUOTE), "\"");
		return input;
	}
	private static String unicodeToUTF8(String str){
		return new String(str.getBytes(Charset.defaultCharset()), Charset.defaultCharset());
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
}
