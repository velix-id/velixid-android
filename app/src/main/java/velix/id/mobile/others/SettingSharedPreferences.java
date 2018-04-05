package velix.id.mobile.others;

import android.content.Context;
import android.content.SharedPreferences;


public class SettingSharedPreferences {

    SharedPreferences keyPairPref, userloginPreferences, rememberLoginPreferences, velexIdPref;
    SharedPreferences.Editor keyPairEditor, userEditorLogin, rememberEditorLogin, velexIdEditor;

    public SettingSharedPreferences(Context context){
        userloginPreferences=context.getSharedPreferences("User Login", Context.MODE_PRIVATE);
        userEditorLogin=userloginPreferences.edit();
        rememberLoginPreferences=context.getSharedPreferences("Remember Login", Context.MODE_PRIVATE);
        rememberEditorLogin=rememberLoginPreferences.edit();
        keyPairPref=context.getSharedPreferences("KeyPair", Context.MODE_PRIVATE);
        keyPairEditor=keyPairPref.edit();
        velexIdPref=context.getSharedPreferences("id", Context.MODE_PRIVATE);
        velexIdEditor=velexIdPref.edit();
    }

    public void saveKeyPair(String key1, String key2){
        keyPairEditor.putString("PublicKey", key1).commit();
        keyPairEditor.putString("PrivateKey", key2).commit();
    }

    public String getPublicKey(){
        return keyPairPref.getString("PublicKey", null);
    }

    public String getPrivateKey(){
        return keyPairPref.getString("PrivateKey", null);
    }

    public void saveVelixId(String id){
        velexIdEditor.putString("v_id", id).commit();
    }

    public String getVelixId(){
        return velexIdPref.getString("v_id", null);
    }

    public void saveLoginPreferences(String userName, String email, String phone){
        userEditorLogin.putString("username",userName).commit();
        userEditorLogin.putString("email",email).commit();
        userEditorLogin.putString("phone",phone).commit();
    }


    public void saveRememberLoginPreferences(String loginValue, String password){
        rememberEditorLogin.putString("loginValue",loginValue).commit();
        rememberEditorLogin.putString("loginPassword",password).commit();
    }


    public String getRememberLoginValue(){
        return rememberLoginPreferences.getString("loginValue",null);
    }

    public String getRememberLoginPassword(){
        return rememberLoginPreferences.getString("loginPassword",null);
    }

    public String getUserIdLoginValue(){
        return userloginPreferences.getString("user_id",null);
    }

    public String getUserNameLoginValue(){
        return userloginPreferences.getString("username",null);
    }

    public String getEmailLoginValue(){
        return userloginPreferences.getString("email",null);
    }

    public String getContactLoginValue(){
        return userloginPreferences.getString("phone",null);
    }


    public boolean logoutFunction(){
        userEditorLogin.clear().commit();
        keyPairEditor.clear().commit();
        velexIdEditor.clear().commit();
        AppData.username=null;
        AppData.email=null;
        AppData.phone=null;

        if(getUserIdLoginValue()==null){
            return true;
        }else{
            return false;
        }
    }

}
