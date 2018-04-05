package velix.id.mobile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.bouncycastle.util.encoders.Base64;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import velix.id.mobile.others.IntentController;
import velix.id.mobile.others.SettingSharedPreferences;
import velix.id.mobile.signup.SignUpPresenter;
import velix.id.mobile.signup.SignUpPresenterImpl;
import velix.id.mobile.signup.SignUpView;

/**
 * Created by User on 4/3/2018.
 */

public class SignUpActivity extends AppCompatActivity implements SignUpView {

    protected AppCompatEditText et_name, et_email;

    private PublicKey pubKey;
    private PrivateKey privKey;

    protected SettingSharedPreferences ssp;
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ssp = new SettingSharedPreferences(this);
        presenter = new SignUpPresenterImpl(this);

        initViews();
    }

    private void initViews() {
        et_name = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_useremail);
        findViewById(R.id.btn_generate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.validateEmptyFields(et_name.getText().toString(), et_email.getText().toString(), SignUpActivity.this);
            }
        });

        if (ssp.getPublicKey()== null && ssp.getPrivateKey()== null){
            generateKeys();
        }
    }

    private void generateKeys() {
        try {
            KeyPairGenerator generator;
            generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(256, new SecureRandom());
            KeyPair pair = generator.generateKeyPair();
            pubKey = pair.getPublic();
            privKey = pair.getPrivate();
            byte[] publicKeyBytes = pubKey.getEncoded();
            String pubKeyStr = new String(Base64.encode(publicKeyBytes));
            byte[] privKeyBytes = privKey.getEncoded();
            String privKeyStr = new String(Base64.encode(privKeyBytes));
            ssp.saveKeyPair(pubKeyStr, privKeyStr);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void userNameError() {
        et_name.setError(getString(R.string.username_error));
        et_name.requestFocus();
    }

    @Override
    public void emailError() {
        et_email.setError(getString(R.string.useremail_error));
        et_email.requestFocus();
    }

    @Override
    public void validEmailError() {
        et_email.setError(getString(R.string.validemail_error));
        et_email.requestFocus();
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateActivity() {
        IntentController.sendIntent(this, MainActivity.class);
        finish();
    }


    /*private void generateKeyPair() throws NoSuchAlgorithmException, UnsupportedEncodingException,
            InvalidKeyException, SignatureException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.genKeyPair();

        byte[] data = "test".getBytes("UTF8");

        Signature sig = Signature.getInstance("MD5WithRSA");
        sig.initSign(keyPair.getPrivate());
        sig.update(data);
        byte[] signatureBytes = sig.sign();
        //Log.e("Singature:" , String.valueOf(Base64.encode(signatureBytes, Base64.DEFAULT)));

        sig.initVerify(keyPair.getPublic());
        sig.update(data);

        Log.e("Verify_signature ", String.valueOf(sig.verify(signatureBytes)));
    }*/


    /*public PublicKey getPublicKey(){
        String pubKeyStr = SP.getString("PublicKey", "");
        byte[] sigBytes = Base64.decode(pubKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            Log.e("public", String.valueOf(keyFact.generatePublic(x509KeySpec)));
            return  keyFact.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey getPrivateKey(){
        String privKeyStr = SP.getString("PrivateKey", "");
        byte[] sigBytes = Base64.decode(privKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            Log.e("private", String.valueOf(keyFact.generatePrivate(x509KeySpec)));
            return  keyFact.generatePrivate(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }*/

}
