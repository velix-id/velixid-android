package velix.id.mobile.signup;

/**
 * Created by User on 3/4/2018.
 */

public interface SignUpView {
  void userNameError();
  void emailError();
  void validEmailError();
  void showToastMessage(String message);
  void navigateActivity();
}
