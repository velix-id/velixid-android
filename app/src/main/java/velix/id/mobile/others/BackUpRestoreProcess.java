package velix.id.mobile.others;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import velix.id.mobile.BaseDemoActivity;
import velix.id.mobile.R;

/**
 * Created by User on 4/5/2018.
 */

public class BackUpRestoreProcess {

    public String TAG = BackUpRestoreProcess.class.getSimpleName();
    BackUpRestoreView upRestoreView;


    public BackUpRestoreProcess(BackUpRestoreView upRestoreView) {
       this.upRestoreView = upRestoreView;
    }

//    public void retrieveContents(DriveFile file) {
//        // [START open_file]
//        Task<DriveContents> openFileTask =
//                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
//        // [END open_file]
//        // [START read_contents]
//        openFileTask
//                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
//                    @Override
//                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
//                        DriveContents contents = task.getResult();
//                        // Process contents...
//                        // [START_EXCLUDE]
//                        // [START read_as_string]
//                        try (BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(contents.getInputStream()))) {
//                            StringBuilder builder = new StringBuilder();
//                            String line;
//                            while ((line = reader.readLine()) != null) {
//                                builder.append(line).append("\n");
//                            }
//                            showMessage(getString(R.string.content_loaded));
//                            saveIntoStorage(builder.toString());
//                        }
//                        // [END read_as_string]
//                        // [END_EXCLUDE]
//                        // [START discard_contents]
//                        Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
//                        // [END discard_contents]
//                        return discardTask;
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Handle failure
//                        // [START_EXCLUDE]
//                        Log.e(TAG, "Unable to read contents", e);
//                        showMessage(getString(R.string.read_failed));
//                        // [END_EXCLUDE]
//                    }
//                });
//        // [END read_contents]
//    }
//
//    private void saveIntoStorage(String data) {
//        String velixId, priKey, pubKey, name, emailLevel = null, email = null, phLevel = null, phone = null;
//        try {
//            jKeyObjectType = new JSONObject(data);
//            Log.e("bakup_string", jKeyObjectType.toString());
//            //ssp = new SettingSharedPreferences(this);
//            velixId = jKeyObjectType.getString("velixid");
//
//            jKeyObjectType.getJSONObject("keys");
//            priKey = jKeyObjectType.getJSONObject("keys").getString("private");
//            pubKey = jKeyObjectType.getJSONObject("keys").getString("public");
//            Log.e("prikey", priKey);
//            Log.e("pubKey", pubKey);
//
//            jKeyObjectType.getJSONObject("name");
//
//            name = jKeyObjectType.getJSONObject("name").getString("value");
//            Log.e("name", name);
//
//            JSONArray jEmailArray = jKeyObjectType.getJSONArray("emails");
//            for (int i=0; i<jEmailArray.length(); i++){
//                emailLevel = jEmailArray.getJSONObject(i).getString("label");
//                email = jEmailArray.getJSONObject(i).getString("value");
//            }
//
//            JSONArray jPhoneArray = jKeyObjectType.getJSONArray("phone");
//            for (int j=0; j<jPhoneArray.length(); j++){
//                phLevel = jPhoneArray.getJSONObject(j).getString("label");
//                phone = jPhoneArray.getJSONObject(j).getString("value");
//            }
//
//            Log.e("Bakupdata", velixId +
//                    "\n" + priKey + "\n" + pubKey +
//                    "\n" + name + "\n" + emailLevel +
//                    "\n" + email + "\n" + phLevel +
//                    "\n" + phone);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

}
