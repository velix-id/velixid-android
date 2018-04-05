package velix.id.mobile.signup;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import velix.id.mobile.R;
import velix.id.mobile.loader.JSONFunctions;
import velix.id.mobile.loader.ProgressBarHandler;
import velix.id.mobile.others.AppData;
import velix.id.mobile.others.SettingSharedPreferences;

/**
 * Created by User on 3/4/2018.
 */

public class SignUpInteractorImpl implements SignUpInteractor, JSONFunctions.OnJSONResponseListener {

  private SignUpView signUpView;
  private Context context;
  private ProgressBarHandler cShowProgress;
  private SettingSharedPreferences ssp;
  private String userName, userEmail;
  private static final int SIGN_UP = 1;

  public SignUpInteractorImpl(SignUpView signUpView) {
    this.signUpView = signUpView;
  }

  @Override
  public void success(String name, String email, OnSignUpListener listener, Context context) {
    this.context = context;
    if (TextUtils.isEmpty(name)){
      listener.onUserNameError();
    } else if (TextUtils.isEmpty(email)){
      listener.onEmailError();
    } else if (!isEmailValid(email)){
      listener.onValidEmailError();
    } else {
      userName = name;
      userEmail = email;
      doSignUp();
    }
  }

  private void doSignUp() {
    if (JSONFunctions.isInternetOn(context)){
      ssp = new SettingSharedPreferences(context);
      JSONFunctions json = new JSONFunctions(this);
      cShowProgress = ProgressBarHandler.getInstance();
      String url = AppData.commonUrl+context.getString(R.string.generates);
      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put("public_key", ssp.getPublicKey());
      Log.e("public_key", ssp.getPublicKey());
      json.makeRawHttpRequest(url, "POST", hashMap, SIGN_UP);
      cShowProgress.showProgress(context);
    }
  }

  private boolean isEmailValid(String email) {
    String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
      +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
      +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
      +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
      +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
      +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

    CharSequence inputStr = email;

    Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(inputStr);

    if (matcher.matches())
      return true;
    else
      return false;
  }

  @Override
  public void getJSONResponseResult(String result, int url_no) {
    if (url_no == SIGN_UP){
      if (result!= null){
        cShowProgress.hideProgress();
        getSignUpResult(result);
      }
    }
  }

  private void getSignUpResult(String status) {
    Log.e("response", status);
    try {
      JSONObject jsonObject = new JSONObject(status);
      if (jsonObject.getBoolean("success")){
        String velixId = jsonObject.getJSONObject("data").getString("velixid");
        ssp.saveVelixId(velixId);
        ssp.saveLoginPreferences(userName, userEmail, "");
        FirebaseMessaging.getInstance().subscribeToTopic(context.getString(R.string.topic)+velixId);
        signUpView.navigateActivity();
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

}
