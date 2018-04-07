package velix.id.mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import velix.id.mobile.others.SettingSharedPreferences;

/**
 * Created by User on 4/2/2018.
 */

public class SettingsActivity extends BaseDemoActivity implements View.OnClickListener {

    private static final String TAG = "Google Drive Activity";

    //variable for decide if i need to do a backup or a restore.
    //True stands for backup, False for restore
    private boolean bckORrst;
    private String data;
    private JSONObject jKeyObjectType;
    private SettingSharedPreferences ssp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btn_backup).setOnClickListener(this);
        findViewById(R.id.btn_restore).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_backup:
                bckORrst = true;
                signIn();
                break;
            case R.id.btn_restore:
                bckORrst = false;
                signIn();
                break;
            case R.id.btn_logout:
                ssp = new SettingSharedPreferences(this);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(getString(R.string.topic)+ssp.getVelixId());
                ssp.logoutFunction();
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onDriveClientReady() {
        if (bckORrst){
            writeFile();
        } else {
            importFromDrive();
        }
    }

    private void importFromDrive() {
        pickFolder()
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveId>() {
                            @Override
                            public void onSuccess(DriveId driveId) {
                                listFilesInFolder(driveId.asDriveFolder());
                                //retrieveContents(driveId.asDriveFile());
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No file selected", e);
                        showMessage(getString(R.string.file_not_selected));
                    }
                });
    }

    /**
     * Retrieves results for the next page. For the first run,
     * it retrieves results for the first page.
     */
    private void listFilesInFolder(DriveFolder folder) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                .build();
        // [START query_children]
        Task<MetadataBuffer> queryTask = getDriveResourceClient().queryChildren(folder, query);
        // END query_children]
        queryTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                               // mResultsAdapter.append(metadataBuffer);
                                //Log.e("Query Result ", metadataBuffer.toString());
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving files", e);
                        showMessage(getString(R.string.query_failed));
                        finish();
                    }
                });
    }

    private void retrieveContents(DriveFile file) {
        // [START open_file]
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
            Log.e("prikey", priKey);
            Log.e("pubKey", pubKey);

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateCustomProperty(DriveFile file) {
        // [START update_custom_property]
        CustomPropertyKey approvalPropertyKey =
                new CustomPropertyKey("approved", CustomPropertyKey.PUBLIC);
        CustomPropertyKey submitPropertyKey =
                new CustomPropertyKey("submitted", CustomPropertyKey.PUBLIC);
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setCustomProperty(approvalPropertyKey, "yes")
                .setCustomProperty(submitPropertyKey, "no")
                .build();
        Task<Metadata> updateMetadataTask =
                getDriveResourceClient().updateMetadata(file, changeSet);
        updateMetadataTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<Metadata>() {
                            @Override
                            public void onSuccess(Metadata metadata) {
                                showMessage(getString(R.string.custom_property_updated));
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to update metadata", e);
                        showMessage(getString(R.string.update_failed));
                    }
                });
        // [END update_custom_property]
    }

    private void writeFile() {
        ssp = new SettingSharedPreferences(this);
        try {
            jKeyObjectType = new JSONObject();
            jKeyObjectType.put("velixid", ssp.getVelixId());

            /*Keys*/
            JSONObject jKeyObj = new JSONObject();
            jKeyObj.put("private", ssp.getPrivateKey());
            jKeyObj.put("public", ssp.getPublicKey());
            jKeyObjectType.put("keys", jKeyObj);

            /*Name*/
            JSONObject jNameObjectType = new JSONObject();
            jNameObjectType.put("value", ssp.getUserNameLoginValue());
            jNameObjectType.put("valueHash", md5(ssp.getUserNameLoginValue()+ssp.getVelixId()));
            jKeyObjectType.put("name", jNameObjectType);

            /*Emails*/
            JSONArray jEmailArray = new JSONArray();
            JSONObject jEmailObj = new JSONObject();
            jEmailObj.put("label", "Work");
            jEmailObj.put("value", ssp.getEmailLoginValue());
            jEmailObj.put("valueHash", md5(ssp.getEmailLoginValue()+ssp.getVelixId()));
            jEmailArray.put(jEmailObj);
            jKeyObjectType.put("emails", jEmailArray);

            /*Emails*/
            JSONArray jPhoneArray = new JSONArray();
            JSONObject jPhoneObj = new JSONObject();
            jPhoneObj.put("label", "Work");
            jPhoneObj.put("value", ssp.getContactLoginValue());
            jPhoneObj.put("valueHash", md5(ssp.getContactLoginValue()+ssp.getVelixId()));
            jPhoneArray.put(jPhoneObj);
            jKeyObjectType.put("phone", jPhoneArray);

            Log.e("Format", jKeyObjectType.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data = jKeyObjectType.toString();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(getString(R.string.FILE_NAME), Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            String yourFilePath = getFilesDir()+"/"+getString(R.string.FILE_NAME);
            File yourFile = new File(yourFilePath);
            Log.e("Filename", yourFile.getName());
            FileInputStream fin = openFileInput(getString(R.string.FILE_NAME));
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            Log.e("File_Data", temp);
            fin.close();
            createFileInAppFolder();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    private String md5(String string) {
        Log.e("md5_str", string);
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(string.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void createFileInAppFolder() {
        final Task<DriveFolder> appFolderTask = getDriveResourceClient().getAppFolder();
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = appFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();
                        OutputStream outputStream = contents.getOutputStream();
                        try (Writer writer = new OutputStreamWriter(outputStream)) {
                            writer.write(data);
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(getString(R.string.FILE_NAME))
                                .setMimeType("text/plain")
                                .setStarred(true)
                                .build();

                        return getDriveResourceClient().createFile(parent, changeSet, contents);
                    }
                })
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveFile>() {
                            @SuppressLint("StringFormatInvalid")
                            @Override
                            public void onSuccess(DriveFile driveFile) {
                                showMessage(getString(R.string.file_created,
                                        driveFile.getDriveId().encodeToString()));
                                Toast.makeText(getApplicationContext(), driveFile.getDriveId().encodeToString(), Toast.LENGTH_SHORT).show();
                                //finish();
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create file", e);
                        showMessage(getString(R.string.file_create_error));
                        //finish();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

}
