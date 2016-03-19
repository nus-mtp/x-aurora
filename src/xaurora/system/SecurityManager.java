package xaurora.system;

import java.util.HashMap;

import xaurora.io.SystemIO;
import xaurora.security.Security;

public final class SecurityManager {
    private HashMap<String,byte[]> keySet = new HashMap<String,byte[]>();
    private String currentHashUserName;
    
    private static SecurityManager classInstance;
    private SecurityManager(SystemIO io) {
        keySet = io.retrieveKeys();
    }
    public static SecurityManager getClassInstance(SystemIO io){
        if(classInstance == null){
            classInstance = new SecurityManager(io);
        }
        return classInstance;
    }
    public final boolean isNewUser(String name,String email){
        return !this.keySet.containsKey(Security.hashUserName(name, email));
    }
    public void reInit(SystemIO io){
        this.keySet = new HashMap<String,byte[]>();
        this.keySet = io.retrieveKeys();
    }
    public void setCurrentHash(String hashName){
        this.currentHashUserName = hashName;
    }
    
    public String getCurrentHash(){
        return this.currentHashUserName;
    }
    public byte[] getSalt(String hash){
        return this.keySet.get(hash);
    }
}
