package velix.id.mobile.signup;

import android.content.Context;

/**
 * Created by User on 3/4/2018.
 */

public interface SignUpInteractor {
  interface OnSignUpListener {
    void onUserNameError();
    void onEmailError();
    void onValidEmailError();

  }
  void success(String name, String email, OnSignUpListener listener, Context context);
}
