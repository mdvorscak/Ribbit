package mikedvorscak.com.ribbit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;


public class LoginActivity extends ActionBarActivity {

    protected TextView mSignUpTextView;
    protected EditText mUserNameField;
    protected EditText mPasswordField;
    private String mUserName;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView) findViewById(R.id.signUpText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.switchActivity(LoginActivity.this, SignUpActivity.class, false);
            }
        });

        final String USERNAME_KEY = getString(R.string.username_key);
        final String PASSWORD_KEY = getString(R.string.password_key);
        final String EMAIL_KEY = getString(R.string.email_key);

        mUserNameField = (EditText) findViewById(R.id.userNameField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);

        HashMap<String, TextView> fields = new HashMap<String, TextView>();
        fields.put(USERNAME_KEY, mUserNameField);
        fields.put(PASSWORD_KEY, mPasswordField);

        final InputForm form = new InputForm(fields);
        InputForm.EmptyCheck emptyCheck = InputForm.EmptyCheck.getInstance();
        form.addValidationCheck(USERNAME_KEY, emptyCheck);
        form.addValidationCheck(PASSWORD_KEY, InputForm.ValidPasswordCheck.getInstance());
        form.addValidationCheck(PASSWORD_KEY, emptyCheck);
        form.addValidationCheck(EMAIL_KEY, InputForm.ValidEmailCheck.getInstance());
        form.addValidationCheck(EMAIL_KEY, emptyCheck);

        Button button = (Button) findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(form.areFieldsValid()){
                    HashMap<String, String> formValues = form.extractFormValues();
                    mUserName = formValues.get(USERNAME_KEY);
                    mPassword = formValues.get(PASSWORD_KEY);

                    setProgressBarIndeterminateVisibility(true);
                    ParseUser.logInInBackground(mUserName, mPassword, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if(e == null){
                                Utils.switchActivity(LoginActivity.this, MainActivity.class);
                            } else {
                                Utils.displayErrorDialog(e.getMessage(),
                                        getString(R.string.error_dialog_title),
                                        LoginActivity.this);
                            }
                        }
                    });
                }
            }
        });
    }
}
