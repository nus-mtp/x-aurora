package xaurora.system;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import xaurora.io.SystemIO;
import xaurora.security.Security;
import xaurora.ui.Message;

/**
 * @author GAO RISHENG A0101891L Description: This class is mainly in charge of
 *         storing all the secret keys for all users in this local device and
 *         verify whether a user is new to this device. And it is also in charge
 *         of storing all the random salt for all users in this device
 *
 */
public final class SecurityManager {
    private static final String MSG_INFO_MASTER_KEY_INIT_COMPLETE = "Master key initialization complete.";
    private static final String ERR_MSG_INVALID_ENCRYPT_KEY_FORMAT = "Encryption failed due to invalid user key format";
    private static final String ERR_MSG_INVALID_DECRYPT_KEY_FORMAT = "Decryption failed due to invalid user key format";
    private static final String ERR_MSG_UNKNOWN_ENCRYPTION_ERROR = "Unable to encrypt user key due to unknow problem.";
    private static final String ERR_MSG_UNKNOWN_DECRYPTION_ERROR = "Unable to decrypt user key due to unknow problem.";
    private static final String ERR_MSG_UNABLE_TO_GENERATE_MASTERKEY = "Unable to generate system master key.";
    private static final int BYTE_MASK = 0xff;
    private static final String MSG_USER_UPDATE = "Change in current user.";
    private static final String MSG_REINIT = "Reinitialization occurs in this Security Manager instance.";
    private static final String MSG_USER_REQUEST = "A new User Existence Verification request is received.";
    private static final String MSG_INIT_COMPLETE = "Loading Local keys complete. Initialization Complete.";
    private static final String MSG_START = "An instance of Security Manager is created. This message should appear only once at every software running flow.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private HashMap<String, byte[]> keySet = new HashMap<String, byte[]>();
    private String currentHashUserName;
    private byte[] masterKey;
    private Logger logger;
    private boolean isInitialize = false;
    private static SecurityManager classInstance;
    private static final int[] masterkey = { 0x71, 0x33, 0x64, 0x57, 0xcf, 0xb0,
            0xed, 0x61, 0xbc, 0xa1, 0x20, 0x11, 0xb9, 0xdd, 0xbf, 0xb2 };
    private static final int[] mastersalt = { 0xc1, 0xdf, 0x9e, 0x61, 0xba,
            0x21, 0x87, 0x29, 0x45, 0x83, 0x6b, 0xee, 0x60, 0xfc, 0xf9, 0xd7 };
    private static final byte[] masteriv = { 0x00, 0x01, 0x02, 0x04, 0x08, 0x10,
            0x20, 0x40, 0x7F, 0x3F, 0x1F, 0x0F, 0x07, 0x03, 0x01, 0x00 };
    private static final int INDEX_ZERO = 0;
    private static final String HASH_TYPE_SHA_1 = "SHA-1";
    private static final int KEY_LENGTH_IN_BYTES = 16;
    private static final String ENCRYPT_ALGORITHM = "AES";
    private static final String ENCRYPT_METHOD = "AES/CBC/PKCS5PADDING";
    private Message message = new Message();

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
        this.initMasterKey();
        keySet = io.retrieveKeys(this);
        this.logger.info(MSG_INIT_COMPLETE);

    }

    /**
     * Description: initialize the master key. This must be done before it can
     * handle other queries
     */
    private void initMasterKey() {
        byte[] masterTempKey = new byte[masterkey.length + mastersalt.length];
        for (int i = INDEX_ZERO; i < masterkey.length; i++) {
            masterTempKey[i] = (byte) (masterkey[i] & BYTE_MASK);
            masterTempKey[masterkey.length
                    + i] = (byte) (mastersalt[i] & BYTE_MASK);
        }
        try {
            MessageDigest sha = MessageDigest.getInstance(HASH_TYPE_SHA_1);
            this.masterKey = sha.digest(masterTempKey);
            this.masterKey = Arrays.copyOf(this.masterKey, KEY_LENGTH_IN_BYTES);
            this.isInitialize = true;
            this.logger.info(MSG_INFO_MASTER_KEY_INIT_COMPLETE);
        } catch (NoSuchAlgorithmException e) {
            this.logger.error(ERR_MSG_UNABLE_TO_GENERATE_MASTERKEY);
            message.showError(ERR_MSG_UNABLE_TO_GENERATE_MASTERKEY);
        }
    }

    /**
     * Description: AES-encrypt all the normal user key and salt using master
     * key
     * 
     * @param entry,
     *            the combination of normal user key and the salt of the user
     * @return the encrypted cipher text of the input using master key
     * @author GAO RISHENG A0101891L
     */
    public byte[] encryptUserkeys(final byte[] entry) {
        assert this.isInitialize;
        SecretKeySpec skeySpec = new SecretKeySpec(this.masterKey,
                ENCRYPT_ALGORITHM);
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_METHOD);
            IvParameterSpec ivspec = new IvParameterSpec(masteriv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
            return cipher.doFinal(entry);
            // these 3 should not be happened
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException e) {
            this.logger.error(ERR_MSG_UNKNOWN_ENCRYPTION_ERROR);
            message.showError(ERR_MSG_UNKNOWN_ENCRYPTION_ERROR);
            return entry;
        } catch (IllegalBlockSizeException e) {
            this.logger
                    .error(ERR_MSG_INVALID_ENCRYPT_KEY_FORMAT + e.getMessage());
            message.showError(ERR_MSG_INVALID_ENCRYPT_KEY_FORMAT);
            return entry;
        } catch (BadPaddingException e) {
            this.logger
                    .error(ERR_MSG_INVALID_ENCRYPT_KEY_FORMAT + e.getMessage());
            message.showError(ERR_MSG_INVALID_ENCRYPT_KEY_FORMAT);
            return entry;
        }
    }

    /**
     * Description: Use master key to decrypt the secure salt and user key
     * 
     * @param entry,the
     *            cipher text of the content of salt and user key
     * @return the plain text of the content of salt and user key in byte array
     * @author GAO RISHENG A0101891L
     */
    public byte[] decryptUserkeys(final byte[] entry) {
        assert this.isInitialize;
        SecretKeySpec skeySpec = new SecretKeySpec(this.masterKey,
                ENCRYPT_ALGORITHM);
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_METHOD);
            IvParameterSpec ivspec = new IvParameterSpec(masteriv);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);
            byte[] result = cipher.doFinal(entry);
            return result;
            // these 3 should not be happened
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException e) {
            this.logger
                    .error(ERR_MSG_UNKNOWN_DECRYPTION_ERROR + e.getMessage());
            message.showError(ERR_MSG_UNKNOWN_DECRYPTION_ERROR);
            return entry;
        } catch (IllegalBlockSizeException e) {
            this.logger
                    .error(ERR_MSG_INVALID_DECRYPT_KEY_FORMAT + e.getMessage());
            message.showError(ERR_MSG_INVALID_DECRYPT_KEY_FORMAT);
            return entry;
        } catch (BadPaddingException e) {
            this.logger
                    .error(ERR_MSG_INVALID_DECRYPT_KEY_FORMAT + e.getMessage());
            message.showError(ERR_MSG_INVALID_DECRYPT_KEY_FORMAT);
            return entry;
        }
    }

    /**
     * Description: Singleton class instance getter
     * 
     * @param io,
     *            the instance of SystemIO that initialize the secure manager
     * @return the Security Manager class instance
     * @author Gao Risheng A0101891L
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
        this.keySet = io.retrieveKeys(this);
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
