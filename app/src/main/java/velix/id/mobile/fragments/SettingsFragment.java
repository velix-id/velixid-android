package velix.id.mobile.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import velix.id.mobile.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "Google Drive Activity";

    private static final int REQUEST_CODE_OPENER = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    //variable for decide if i need to do a backup or a restore.
    //True stands for backup, False for restore
    private boolean bckORrst = true;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        rootView.findViewById(R.id.btn_backup).setOnClickListener(this);
        rootView.findViewById(R.id.btn_restore).setOnClickListener(this);
        rootView.findViewById(R.id.btn_logout).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_backup:
                bckORrst = true;
                if (mGoogleApiClient != null)
                    mGoogleApiClient.disconnect();
                mGoogleApiClient = gApiCLient(mGoogleApiClient);
                mGoogleApiClient.connect();
                break;
            case R.id.btn_restore:
                bckORrst = false;
                if (mGoogleApiClient != null)
                    mGoogleApiClient.disconnect();
                mGoogleApiClient = gApiCLient(mGoogleApiClient);
                mGoogleApiClient.connect();
                break;
            case R.id.btn_logout:
                break;
        }
    }

    //Connect the Client
    private GoogleApiClient gApiCLient(GoogleApiClient mGoogleApiClient) {

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "API client connected.");
        Toast.makeText(getActivity(), "connected", Toast.LENGTH_SHORT).show();

        //when the client is connected i have two possibility: backup (bckORrst -> true) or restore (bckORrst -> false)
        if (bckORrst)
            saveFileToDrive();
        else {
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[]{"application/db"})
                    .build(mGoogleApiClient);
           /* try {
                startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
                Log.i(TAG, "Open File Intent send");
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "Unable to send Open File Intent", e);
            }*/
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }

        try {
            result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Backup successfully saved.");
                    Toast.makeText(getActivity(), "Backup successfully loaded!", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    //Toast.makeText(this, driveId.toString(), Toast.LENGTH_SHORT).show();
                    DriveFile file = driveId.asDriveFile();
                    importFromDrive(file);
                }
        }
    }

    private void saveFileToDrive() {

        //text_file path on the device
        final String inFileName = getActivity().getDatabasePath("Velix").toString();;

        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new Drive backup.");
                    Toast.makeText(getActivity(), "Error on loading Google Drive. Retry", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Backup to drive started.");

                try {

                    File dbFile = new File(inFileName);
                    FileInputStream fis = new FileInputStream(dbFile);
                    OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();

                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    //drive file metadata
                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setTitle("Backup.txt")
                            .setMimeType("text/plain")
                            .build();

                    // Create an intent for the file chooser, and start it.
                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            .setInitialDriveContents(driveContentsResult.getDriveContents())
                            .build(mGoogleApiClient);

                    //startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void importFromDrive(DriveFile dbFile) {

        //text_file path on the device
        final String inFileName = getActivity().getDatabasePath("Velix").toString();

        dbFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to open Drive backup.");
                    Toast.makeText(getActivity(), "Error on loading from Google Drive. Retry", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Backup to drive started.");

                // DriveContents object contains pointers to the actual byte stream
                DriveContents contents = driveContentsResult.getDriveContents();

                try {

                    ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                    FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());

                    // Open the empty db as the output stream
                    OutputStream output = new FileOutputStream(inFileName);

                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }

                    // Close the streams
                    output.flush();
                    output.close();
                    fileInputStream.close();

                    Toast.makeText(getActivity(), "Import Completed", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error on loading", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


}
