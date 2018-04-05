package velix.id.mobile.signup;

import android.content.Context;

/**
 * Created by User on 3/4/2018.
 */

public interface SignUpPresenter {
  void validateEmptyFields(String name, String email, Context context);
  void onDestroy();
}
