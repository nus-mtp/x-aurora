package xaurora.util;

import javafx.scene.input.KeyCode;

/**
 * 
 * @author Lee
 *
 */
public class Hotkeys {

	String[] names = new String[]{
		"EXTEND_WORD", "REDUCE_WORD", 
		"EXTEND_SENTENCE", "REDUCE_SENTENCE", 
		"EXTEND_PARAGRAPH", "REDUCE_PARAGRAPH"
	};
	KeyCode[][] codes = new KeyCode[][]{
		{KeyCode.CONTROL, KeyCode.ALT, KeyCode.Z},
		{KeyCode.CONTROL, KeyCode.ALT, KeyCode.X},
		{KeyCode.CONTROL, KeyCode.ALT, KeyCode.C},
		{KeyCode.CONTROL, KeyCode.ALT, KeyCode.V},
		{KeyCode.CONTROL, KeyCode.ALT, KeyCode.B},
		{KeyCode.CONTROL, KeyCode.ALT, KeyCode.N}
	};
	Hotkey[] hotkeys;
	private static final int numHotkeys = 6;

	public Hotkeys(){
		//initialize all hotkeys
		hotkeys = new Hotkey[numHotkeys];
		for (int i=0; i < numHotkeys; i++){
			hotkeys[i] = new Hotkey(names[i], codes[i]);
		}
	}

	public Hotkey getHotkey(int index){
		return hotkeys[index];
	}
	
	public String getHotkeyName(int index){
		return hotkeys[index].getName();
	}
	
	public KeyCode[] getHotkeyCodes(int index){
		return hotkeys[index].getCodes();
	}
	
	public KeyCode[] getDefaultHotkeyCodes(int index){
		return codes[index];
	}
	public KeyCode[][] getCodes(){
	    return this.codes;
	}
	
	public void setHotkey(int index, Hotkey hotkey){
		hotkeys[index] = hotkey;
	}
	
	public void setHotkeyCodes(int index, KeyCode[] codes){
		hotkeys[index].setCodes(codes);
	}
}

class Hotkey {

	private String name;
	private KeyCode[] codes;
	
	public Hotkey(){
		name = null;
		codes = new KeyCode[0];
	}
	
	public Hotkey(String name, KeyCode[] codes){
		this.name = name;
		this.codes = codes;
	}
	
	public String getName(){
		return this.name;
	}
	
	public KeyCode[] getCodes(){
		return codes;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setCodes(KeyCode[] codes){
		this.codes = codes;
	}
}
