package velix.id.mobile.signup;

import android.content.Context;

/**
 * Created by User on 3/4/2018.
 */

public class SignUpPresenterImpl implements SignUpPresenter, SignUpInteractor.OnSignUpListener {

  private SignUpView signUpView;
  private SignUpInteractor interactor;

  public SignUpPresenterImpl(SignUpView signUpView) {
    this.signUpView = signUpView;
    interactor = new SignUpInteractorImpl(signUpView);
  }

  @Override
  public void validateEmptyFields(String name, String email, Context context) {
    if (signUpView!= null){
      //do something
    }
    interactor.success(name, email, this, context);
  }

  @Override
  public void onDestroy() {

  }


  @Override
  public void onUserNameError() {
    signUpView.userNameError();
  }

  @Override
  public void onEmailError() {
    signUpView.emailError();
  }

  @Override
  public void onValidEmailError() {
    signUpView.validEmailError();
  }

}
