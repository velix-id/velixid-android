package velix.id.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import velix.id.mobile.loader.ProgressBarHandler;
import velix.id.mobile.others.IntentController;
import velix.id.mobile.others.SettingSharedPreferences;

/**
 * Created by User on 4/4/2018.
 */

public class WelcomeActivity extends BaseDemoActivity implements View.OnClickListener {

    private static final String TAG = "Google Drive Activity";

    private JSONObject jKeyObjectType;
    private ProgressBarHandler mProgressBarHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView restore = findViewById(R.id.tv_restore);
        SpannableString content = new SpannableString(getString(R.string.restore_velix));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        restore.setText(content);
        findViewById(R.id.btn_createnew).setOnClickListener(this);
        restore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_createnew:
                IntentController.sendIntent(this, SignUpActivity.class);
                finish();
                break;
            case R.id.tv_restore:
                signIn();
                break;
        }
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDriveClientReady() {
        importFromDrive();
    }

    private void importFromDrive() {
        pickTextFile()
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveId>() {
                            @Override
                            public void onSuccess(DriveId driveId) {
                                retrieveContents(driveId.asDriveFile());
                                //updateCustomProperty(driveId.asDriveFile());
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No file selected", e);
                        mProgressBarHandler.hideProgress();
                        showMessage(getString(R.string.file_not_selected));
                    }
                });
    }

    private void retrieveContents(DriveFile file) {
        // [START open_file]
        mProgressBarHandler = ProgressBarHandler.getInstance();
        mProgressBarHandler.showProgress(WelcomeActivity.this);
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents contents = task.getResult();
                        // Process contents...
                        // [START_EXCLUDE]
                        // [START read_as_string]
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(contents.getInputStream()))) {
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }
                            mProgressBarHandler.hideProgress();
                            showMessage(getString(R.string.content_loaded));
                            saveIntoStorage(builder.toString());
                        }
                        // [END read_as_string]
                        // [END_EXCLUDE]
                        // [START discard_contents]
                        Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                        // [END discard_contents]
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        // [START_EXCLUDE]
                        Log.e(TAG, "Unable to read contents", e);
                        showMessage(getString(R.string.read_failed));
                        // [END_EXCLUDE]
                    }
                });
        // [END read_contents]
    }

    private void saveIntoStorage(String data) {
        String velixId, priKey, pubKey, name, emailLevel = null, email = null, phLevel = null, phone = null;
        try {
            jKeyObjectType = new JSONObject(data);
            Log.e("bakup_string", jKeyObjectType.toString());
            //ssp = new SettingSharedPreferences(this);
            velixId = jKeyObjectType.getString("velixid");

            jKeyObjectType.getJSONObject("keys");
            priKey = jKeyObjectType.getJSONObject("keys").getString("private");
            pubKey = jKeyObjectType.getJSONObject("keys").getString("public");

            jKeyObjectType.getJSONObject("name");
            name = jKeyObjectType.getJSONObject("name").getString("value");
            Log.e("name", name);

            JSONArray jEmailArray = jKeyObjectType.getJSONArray("emails");
            for (int i=0; i<jEmailArray.length(); i++){
                emailLevel = jEmailArray.getJSONObject(i).getString("label");
                email = jEmailArray.getJSONObject(i).getString("value");
            }

            JSONArray jPhoneArray = jKeyObjectType.getJSONArray("phone");
            for (int j=0; j<jPhoneArray.length(); j++){
                phLevel = jPhoneArray.getJSONObject(j).getString("label");
                phone = jPhoneArray.getJSONObject(j).getString("value");
            }

            Log.e("Bakupdata", velixId +
                    "\n" + priKey + "\n" + pubKey +
                    "\n" + name + "\n" + emailLevel +
                    "\n" + email + "\n" + phLevel +
                    "\n" + phone);
            SettingSharedPreferences ssp = new SettingSharedPreferences(this);
            ssp.saveVelixId(velixId);
            ssp.saveKeyPair(pubKey, priKey);
            ssp.saveLoginPreferences(name, email, phone);
            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.topic)+velixId);
            IntentController.sendIntent(this, MainActivity.class);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
