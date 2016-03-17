package xaurora.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;



/**
 * Description: A random test case generator that generates random raw text file.
 * @author GAO RISHENG A0101891L
 *
 */
public final class SecurityTestInputGenerator {
	static final String RANDOM_INPUT_DIRECTORY = "/unit_testing/Security_Test/Input/Random_Input/";
	static final String EXPECTED_OUTPUT_DIRECTORY = "/unit_testing/Security_Test/Output/Expected_Output/";
	static final String ACTUAL_OUTPUT_DIRECTORY = "/unit_testing/Security_Test/Output/Actual_Output/";
	static final String RANDOM_INPUT_FILENAME = "Random_Input";
	static final String RANDOM_OUTPUT_FILENAME = "Random_Output";
	static final String DEFAULT_FILE_EXTENSION = ".txt";
	static final String PATH_SEPARATOR = "\\";
	static final String NEW_EMPTY_STRING = "";
	static final String NEW_LINE = "\n";
	private static ArrayList<String> legalLiterals = new ArrayList<String>();
	private static ArrayList<String> legalCharacters = new ArrayList<String>();
	private static ArrayList<String> numberLiterals = new ArrayList<String>();
	private static final int MAX_STRING_LENGTH = 10000;
	private static final int INDEX_ZERO = 0;
	private static String inputDirectory;
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		init();
		int numberOfTestCases = sc.nextInt();
		int LengthPerLine = sc.nextInt();
		generateSingleInput(numberOfTestCases, LengthPerLine);
		sc.close();
	}
	/**
	 * Description: generates the given number of text files (test cases) with given number of lines
	 * @param number, the number of test cases
	 * @param input, the number of lines per test case
	 * 
	 * @author GAO RISHENG A0101891L
	 */
	private static void generateSingleInput(int number, int input) {
		for(int count = INDEX_ZERO;count<number;count++){
			ArrayList<String> inputs = new ArrayList<String>();
			Random ran = new Random();
			for(int i = INDEX_ZERO;i<input;i++){
				inputs.add(randomStringLiteral(Math.abs(ran.nextInt()%MAX_STRING_LENGTH)));
			}
			outputToFile(inputs,count+1);
		}
	}
	/**
	 * Description: initialize the directory of the generated input and initialize the 'Legal' Literal List
	 * @author GAO RISHENG A0101891L
	 */
	private static void init() {
		File temp = new File(SecurityTestInputGenerator.NEW_EMPTY_STRING);
		File storeDir = new File(temp.getAbsolutePath()+SecurityTestInputGenerator.RANDOM_INPUT_DIRECTORY);
		if(storeDir.exists()){
			inputDirectory = storeDir.getAbsolutePath();
		}
		readLegalLiterals();
	}
	/**
	 * Description: Generate the ArrayList that stores all the 'legal' characters
	 * @author GAO RISHENG A0101891L
	 */
	private static void readLegalLiterals() {
		for(int i = 0;i<26;i++){
			legalLiterals.add(String.valueOf((char)(65+i)));
			legalLiterals.add(String.valueOf((char)(97+i)));
			
		}
		legalLiterals.add("_");
		for(int j = 0;j<10;j++){
			numberLiterals.add(String.valueOf(j));
			
		}
		for(int k = 32;k<=126;k++){
			if(k!=34&&k!=39&&k!=63){
				if(k == 92){
				}
				else{
					legalCharacters.add(String.valueOf((char)k));
				}
			}
		}

		legalCharacters.add(NEW_EMPTY_STRING);
	}
	/**
	 * @param length, the length of String to be generated
	 * @return a String which is generated by pseudo-random selection from the Legal Characters with given size
	 * @author GAO RISHENG A0101891L
	 */
	private static String randomStringLiteral(int length){
		StringBuilder output = new StringBuilder(NEW_EMPTY_STRING);
		Random ran = new Random();
		for(int i = INDEX_ZERO;i<length;i++){
			String temp = legalCharacters.get(Math.abs(ran.nextInt()%legalCharacters.size()));
			while (temp.equals(PATH_SEPARATOR)||temp.equals(NEW_EMPTY_STRING)){
				temp=legalCharacters.get(Math.abs(ran.nextInt()%legalCharacters.size()));
			}
			output.append(temp);
		}
		return output.toString();
	}
	/**
	 * @param input, an ArrayList<String> that stores the generated text file in lines
	 * @param index, the test case index
	 * @author GAO RISHENG A0101891L
	 */
	private static void outputToFile(ArrayList<String> input,int index){
		try{
			File inputFile = new File(inputDirectory
					+SecurityTestInputGenerator.PATH_SEPARATOR+
					SecurityTestInputGenerator.RANDOM_INPUT_FILENAME+index+SecurityTestInputGenerator.DEFAULT_FILE_EXTENSION);
			if(!inputFile.exists()){
				inputFile.createNewFile();
			}
			FileWriter fw = new FileWriter(inputFile.getAbsoluteFile());
			
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i = INDEX_ZERO;i<input.size();i++){
				bw.write(input.get(i)+NEW_LINE);
			}
			bw.close();
		} catch(IOException e){
			e.printStackTrace();
		}
			
	}
	/**
	 * Secure Programming. Making this Object not-clonable. Object.clone() allows cloning the data of an object without initialize it
	 * which may leak the chances for attacker to access the data internally
	 * @Author GAO RISHENG A0101891L
	 */
	public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
	}
	
	/**
	 * Secure Programming. Disable the serialize option of the object which avoid attacker to print the object in serialize manner
	 * and inspect the internal status of the object
	 * @author GAO RISHENG A0101891L
	 */
	private final void writeObject(ObjectOutputStream out)
			throws java.io.IOException {
			        throw new java.io.IOException("Object cannot be serialized");
			}
}
