package xaurora.security;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import xaurora.system.SecurityManager;
import xaurora.ui.Message;

/**
 * Description: This Class is mainly in charge of maintaining the
 * confidentiality of the extracted text data by applying AES Encryption
 * algorithm to the plain text content. Also, it will be in charge of data
 * decryption;
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public final class Security {
    private static final String ERR_MSG_INVALID_HASH_ALGORITHM = "Error, unable to generate the hashed ID due to invalid Hash Algorithm. Error Message: ";
    private static final String ERR_MSG_INVALID_GENERATED = "Invalid Key is generated due to errors.";
    private static final String ERR_MSG_UNABLE_TO_GENERATE_KEY = "Error occurs in secret key generation with error message ";
    private static final String ERR_MSG_ENCRYPTION_FAIL = "Encryption fail. Error occurs at encrypting process with error Message ";
    private static final String HASH_TYPE_SHA_1 = "SHA-1";
    private static final int CASE_DIFFERENCE = 32;
    private static final int SUM_OF_DIGITS = 105;
    private static final String PADDING_ZERO = "0";
    private static final int ID_LENGTH = 32;
    private static final int STRING_LENGTH = 16;
    private static final String HASH_TYPE_MD5 = "MD5";
    private static final String ENCRYPT_ALGORITHM = "AES";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String ENCRYPT_METHOD = "AES/CBC/PKCS5PADDING";
    private static final String EMPTY_STRING = "";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final int INDEX_ZERO = 0;
    private static final int IV_LENGTH = 16;
    private static final int SALT_LENGTH = 32;
    private static final int KEY_LENGTH_IN_BYTES = 16;
    private static Logger logger = Logger.getLogger(Security.class);
    private static Message message = new Message();

    /**
     * Description: encrypt the plain text content with given IV
     * 
     * @param content,
     *            the plain text in bytes
     * @param initVector
     *            the String IV
     * @return Cipher text data in bytes
     * @author GAO RISHENG A0101891L
     */
    public static final byte[] encrypt(final byte[] content,
            final String initVector, final SecurityManager secure) {
        try {
            String hash = secure.getCurrentHash();
            byte[] currentSalt = secure.getSalt(hash);
            byte[] secretKey = generateKey(hash, currentSalt);
            SecretKeySpec skeySpec = new SecretKeySpec(secretKey,
                    ENCRYPT_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPT_METHOD);
            byte[] iv = createIV(initVector);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
            return cipher.doFinal(content);
        } catch (Exception e) {
            logger.error(ERR_MSG_ENCRYPTION_FAIL, e);
            message.showError(ERR_MSG_ENCRYPTION_FAIL);
        }
        return content;
    }

    /**
     * Description: decrypt the encrypted text content with given IV
     * 
     * @param content,
     *            the encrypted cipher text in bytes
     * @param initVector
     *            the String IV
     * @return Plain text data in bytes
     * @author GAO RISHENG A0101891L
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static final byte[] decrypt(final byte[] content,
            final String initVector, final SecurityManager secure)
                    throws InvalidKeyException,
                    InvalidAlgorithmParameterException,
                    NoSuchAlgorithmException, NoSuchPaddingException,
                    IllegalBlockSizeException, BadPaddingException {

        String hash = secure.getCurrentHash();
        byte[] currentSalt = secure.getSalt(hash);
        byte[] secretKey = generateKey(hash, currentSalt);
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey,
                ENCRYPT_ALGORITHM);
        Cipher cipher = Cipher.getInstance(ENCRYPT_METHOD);
        byte[] iv = createIV(initVector);
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);
        return cipher.doFinal(content);

    }

    /**
     * Description: generate the IV for AES Encryption by doing modifications to
     * the filename
     * 
     * @param initVector,
     *            is usually the file name of a text file
     * @return the IV of AES Encryption in byte array
     * @author GAO RISHENG A0101891L
     */
    private static final byte[] createIV(final String initVector) {
        char[] characters = initVector.toCharArray();
        byte[] output = new byte[IV_LENGTH];
        for (int index = INDEX_ZERO; index < characters.length; index++) {

            if (characters[index] >= 'A' && characters[index] <= 'Z') {
                output[index % IV_LENGTH] = (byte) ((char) characters[index]
                        + CASE_DIFFERENCE);
            }
            if (characters[index] >= 'a' && characters[index] <= 'z') {
                output[index % IV_LENGTH] = (byte) ((char) characters[index]
                        - CASE_DIFFERENCE);
            }
            if (characters[index] >= '0' && characters[index] <= '9') {
                // reversing the digit
                output[index % IV_LENGTH] = (byte) (SUM_OF_DIGITS
                        - (char) characters[index]);

            }

        }
        return output;

    }

    /**
     * Description: generate a secure random 32 byte salt value
     * 
     * @return a 32 byte secure random salt
     * @author GAO RISHENG A0101891L
     */
    private static final byte[] generateSalt() {
        final Random r = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        r.nextBytes(salt);
        return salt;
    }

    /**
     * Description: generate an encryption key for given user hash ID and salt
     * 
     * @param hashName,
     *            the MD5 hashed user ID
     * @param salt,
     *            the 32 bytes secure random salt @return, the unique encryption
     *            key for this user
     * @author GAO RISHENG A0101891L
     */
    private static final byte[] generateKey(final String hashName,
            final byte[] salt) {
        try {
            byte[] temp = hashName.getBytes(DEFAULT_CHARSET);
            // input contains hashed userName + salt
            // 64 byte temporary key
            byte[] input = new byte[temp.length + salt.length];
            for (int index = INDEX_ZERO; index < salt.length; index++) {
                input[index] = salt[index];
            }
            for (int index = INDEX_ZERO; index < temp.length; index++) {
                input[index + salt.length] = temp[index];
            }
            // generate the 16 byte actual key by applying SHA_1 hash at
            // the 64 byte temporary key
            MessageDigest sha = MessageDigest.getInstance(HASH_TYPE_SHA_1);
            input = sha.digest(input);
            input = Arrays.copyOf(input, KEY_LENGTH_IN_BYTES);
            return input;
        } catch (Exception e) {
            logger.error(ERR_MSG_UNABLE_TO_GENERATE_KEY, e);
        }
        logger.info(ERR_MSG_INVALID_GENERATED);
        message.showError(ERR_MSG_INVALID_GENERATED);
        return new byte[KEY_LENGTH_IN_BYTES];
    }

    /**
     * Description: create the unique encryption key for the un-sync local user
     * in the local device (characteristic of local key, default user name + no
     * email)
     * 
     * @param userName,
     *            the default user name in the local device
     * @return the local encryption key
     * @author GAO RISHENG A0101891L
     */
    public static final byte[] generateLocalKey(final String userName) {
        assert userName != null;
        return generateUserKey(userName, EMPTY_STRING);
    }

    /**
     * Description: generate the user encryption key from given user name and
     * user email
     * 
     * @param userName,
     *            the user name, a non-empty and not null string
     * @param userEmail,
     *            the user email, a not null string
     * @return the unique encryption key for this user
     * @author Gao Risheng A0101891L
     */
    public static final byte[] generateUserKey(final String userName,
            final String userEmail) {
        assert userName != null && !userName.trim().equals(EMPTY_STRING);
        assert userEmail != null;
        String hash = hashUserName(userName, userEmail);
        assert hash.length() == ID_LENGTH;
        byte[] hashes = hash.getBytes();
        byte[] salt = generateSalt();
        // 64 bytes entry
        byte[] entry = new byte[ID_LENGTH + SALT_LENGTH];
        for (int index = INDEX_ZERO; index < ID_LENGTH; index++) {
            entry[index] = hashes[index];
            entry[ID_LENGTH + index] = salt[index];
        }
        return entry;
    }

    /**
     * Description: this is to generate a MD5 hashed user ID from given user
     * name and user email
     * 
     * @param userName,
     *            the userName of the user
     * @param email,
     *            the user email of the user @return, the MD5 hashed user ID
     * @author GAO RISHENG A0101891L
     */
    public static final String hashUserName(final String userName,
            final String email) {
        byte[] id = (userName + email).getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_TYPE_MD5);
            md.reset();
            md.update(userName.getBytes());
            byte[] newID = md.digest(id);
            BigInteger bigInt = new BigInteger(1, newID);
            String hashID = bigInt.toString(STRING_LENGTH);
            while (hashID.length() < ID_LENGTH) {
                hashID = PADDING_ZERO + hashID;
            }
            return hashID;
            // return id;
        } catch (NoSuchAlgorithmException e) {
            // SHOW ERROR LOG MESSAGE
            logger.error(ERR_MSG_INVALID_HASH_ALGORITHM, e);
            message.showError(ERR_MSG_INVALID_HASH_ALGORITHM);
            return id.toString();
        }
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
