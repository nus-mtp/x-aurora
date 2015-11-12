package xaurora.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SecurityTestInputGenerator {
	private static ArrayList<String> legalLiterals = new ArrayList<String>();
	private static ArrayList<String> legalCharacters = new ArrayList<String>();
	private static ArrayList<String> numberLiterals = new ArrayList<String>();
	private static final int MAX_STRING_LENGTH = 10000;
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		init();
		int input = sc.nextInt();
		//ArrayList<String> inputs = generateCreateVariable(input);
		//ArrayList<String> inputs = generateAssignment(input);
		//ArrayList<String> inputs = generatePurePrefix(input);
		//ArrayList<String> inputs = generatePurePostfix(input);
		//ArrayList<String> inputs = generateCompound(input);
		ArrayList<String> inputs = new ArrayList<String>();
		Random ran = new Random();
		for(int i = 0;i<input;i++){
			inputs.add(randomStringLiteral(Math.abs(ran.nextInt()%MAX_STRING_LENGTH)));
		}
		outputToFile(inputs);
	}
	private static void init() {
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
					//legalCharacters.add("\\");
				}
				else{
					legalCharacters.add(String.valueOf((char)k));
				}
			}
		}

		legalCharacters.add("");
	}
	private static String randomStringLiteral(int length){
		String output = "";//"\"";
		Random ran = new Random();
		for(int i = 0;i<length;i++){
			String temp = legalCharacters.get(Math.abs(ran.nextInt()%legalCharacters.size()));
			while (temp.equals("\\")||temp.equals("")){
				temp=legalCharacters.get(Math.abs(ran.nextInt()%legalCharacters.size()));
			}
			output+=temp;
		}
		//output+="\"";
		return output;
	}
	private static void outputToFile(ArrayList<String> input){
		try{
			File inputFile = new File("Securityinput.txt");
			//File inputFile = new File("Compoundinput.txt");
			//File inputFile = new File("Postfixinput.txt");
			//File inputFile = new File("Prefixinput.txt");
			//File inputFile = new File("input.txt");
			if(!inputFile.exists()){
				inputFile.createNewFile();
			}
			System.out.println(inputFile.getAbsolutePath());
			FileWriter fw = new FileWriter(inputFile.getAbsoluteFile());
			
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i = 0;i<input.size();i++){
				bw.write(input.get(i)+"\n");
			}
			bw.close();
			System.out.println("FINISHED!");
		} catch(IOException e){
			e.printStackTrace();
		}
			
	}
}
