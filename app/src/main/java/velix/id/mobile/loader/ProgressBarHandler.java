package velix.id.mobile.loader;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import velix.id.mobile.R;


/**
 * Created by User on 07-Jan-17.
 */

public class ProgressBarHandler {

    public static ProgressBarHandler mCShowProgress;
    public Dialog mDialog;

    public ProgressBarHandler() {
    }

    public static ProgressBarHandler getInstance() {
        if (mCShowProgress== null) {
            mCShowProgress = new ProgressBarHandler();
        }
        return mCShowProgress;
    }

    public void showProgress(Context mContext) {
        mDialog= new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_progress_layout);
        mDialog.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog!= null) {
            mDialog.dismiss();
            mDialog= null;
        }
    }
}
