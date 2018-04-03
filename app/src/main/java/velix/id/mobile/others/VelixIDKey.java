package velix.id.mobile.others;


import android.util.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Created by User on 4/2/2018.
 */

public class VelixIDKey {

    // Generate the new key
    public static VelixIDKey generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        return new VelixIDKey(privateKey, publicKey);
    }

    public static PublicKey loadPublicKeyFromString(String key) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        byte[] data = Base64.decode(key, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }

    public static PrivateKey loadPrivateKeyFromString(String key)throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        byte[] clear = Base64.decode(key, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    // Constuctor to load the key with public key only. Encrypt and VerifySignature functions will work only
    public VelixIDKey(PublicKey publicKey) {

    }

    // Constuctor to load the key with private key only.Decrypt and Sign functions will work in this case.
    public VelixIDKey(PrivateKey privateKey) {

    }

    // Constructor to load Private and Public Keys both
    public VelixIDKey(PrivateKey privateKey, PublicKey publicKey) {

    }

    // Function to encrypt the input string using loaded public key and returns the encrypted string.
   /* public String encrypt(String input) {

    }

    // Function to decrypt the input string using loaded private key and returns the decrypted string.
    public String decrypt(String input) {

    }

    // Function to sign the input string using loaded private key and returns the signature string.
    public String sign(String input) {

    }

    // Function to verify the input and signature using loaded public key and returns true if verified.
    public Boolean verifySignature(String input, String signature) {
        try{

            // Get private key from String
            PublicKey pk = loadPublicKey(publicKey);

            // text to bytes
            byte[] originalBytes = input.getBytes("UTF8");

            //signature to bytes
            //byte[] signatureBytes = signature.getBytes("UTF8");
            byte[] signatureBytes = Base64.decode(signature, Base64.DEFAULT);

            Signature sig = Signature.getInstance("MD5WithRSA");
            sig.initVerify(pk);
            sig.update(originalBytes);

            return sig.verify(signatureBytes);

        }catch(Exception e){
            e.printStackTrace();
           *//* Logger log = Logger.getLogger(RsaCipher.class);
            log.error("error for signature:" + e.getMessage());*//*
            return false;
        }
    }*/


}
