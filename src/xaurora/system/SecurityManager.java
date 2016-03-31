package xaurora.system;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.apache.log4j.Logger;
import xaurora.io.SystemIO;
import xaurora.security.Security;

/**
 * @author GAO RISHENG A0101891L Description: This class is mainly in charge of
 *         storing all the secret keys for all users in this local device and
 *         verify whether a user is new to this device. And it is also in charge
 *         of storing all the random salt for all users in this device
 *
 */
public final class SecurityManager {
    private static final String MSG_USER_UPDATE = "Change in current user.";
    private static final String MSG_REINIT = "Reinitialization occurs in this Security Manager instance.";
    private static final String MSG_USER_REQUEST = "A new User Existence Verification request is received.";
    private static final String MSG_INIT_COMPLETE = "Loading Local keys complete. Initialization Complete.";
    private static final String MSG_START = "An instance of Security Manager is created. This message should appear only once at every software running flow.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private HashMap<String, byte[]> keySet = new HashMap<String, byte[]>();
    private String currentHashUserName;
    private Logger logger;
    private static SecurityManager classInstance;

    /**
     * Description: Singleton class constructor
     * 
     * @param io,
     *            the instance of SystemIO that initialize the secure manager
     * @author Gao Risheng A0101891L
     */
    private SecurityManager(SystemIO io) {
        this.logger = Logger.getLogger(this.getClass());
        this.logger.info(MSG_START);
        keySet = io.retrieveKeys();
        this.logger.info(MSG_INIT_COMPLETE);
    }

    /**
     * Description: Singleton class instance getter
     * 
     * @param io,
     *            the instance of SystemIO that initialize the secure manager
     * @return the Security Manager class instance
     * @author Gao Risheng
     */
    public static final SecurityManager getClassInstance(SystemIO io) {
        if (classInstance == null) {
            classInstance = new SecurityManager(io);
        }
        return classInstance;
    }

    /**
     * Description: Verify whether this user is a new User to the local device
     * by checking the existence of key store in the local directory
     * 
     * @param name,
     *            String user name
     * @param email,
     *            user email
     * @return true if the local key set contains the key of this user, false
     *         otherwise
     * @author Gao Risheng A0101891L
     */
    public final boolean isNewUser(final String name, final String email) {
        this.logger.info(MSG_USER_REQUEST);
        this.logger.debug(name);
        this.logger.debug(email);
        return !this.keySet.containsKey(Security.hashUserName(name, email));
    }

    /**
     * Description: Reinitialize the Secure Manager by retrieving all the keys
     * in the local directory
     * 
     * @param io,
     *            the SystemIO instance that reads the key
     * @author GAO RISHENG A0101891L
     */
    public final void reInit(SystemIO io) {
        this.logger.info(MSG_REINIT);
        this.keySet = new HashMap<String, byte[]>();
        this.keySet = io.retrieveKeys();
    }

    /**
     * Description: setting the current user in this manager
     * 
     * @param hashName,
     *            the hashed user name
     * @author Gao Risheng A0101891L
     */
    public final void setCurrentHash(final String hashName) {
        this.logger.info(MSG_USER_UPDATE);
        this.logger.debug(hashName);
        this.currentHashUserName = hashName;
    }

    /**
     * Description getting the the hashed current user name
     * 
     * @return the hashed user name for current user
     * @author Gao Risheng A0101891L
     */
    public final String getCurrentHash() {
        return this.currentHashUserName;
    }

    /**
     * Description: return the generated random salt for an existing user
     * 
     * @param hashed
     *            user name
     * @return the random salt for this user
     * @author GAO RISHENG
     */
    public final byte[] getSalt(final String hash) {
        
        return this.keySet.get(hash);
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
