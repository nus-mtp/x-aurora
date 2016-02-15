package xaurora.text;
import java.util.ArrayList;
public class ProcessedData {
	private ArrayList<String> emailList;
	private ArrayList<String> numberList;
	
	public ProcessedData(){
		this.emailList = new ArrayList<String>();
		this.numberList = new ArrayList<String>();
	}
	public void addEmail(String email){
		this.emailList.add(email);
	}
	public void addNumber(String number){
		this.numberList.add(number);
	}
	public ArrayList<String> getEmailList(){
		return this.emailList;
	}
	public ArrayList<String> getNumberList(){
		return this.numberList;
	}
 }
