package mikedvorscak.com.ribbit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;


public class SignUpActivity extends ActionBarActivity {

    protected EditText mUserNameField;
    protected EditText mEmailField;
    protected EditText mPasswordField;
    private String mUserName;
    private String mPassword;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

        final String USERNAME_KEY = getString(R.string.username_key);
        final String PASSWORD_KEY = getString(R.string.password_key);
        final String EMAIL_KEY = getString(R.string.email_key);

        mUserNameField = (EditText) findViewById(R.id.userNameField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mEmailField = (EditText) findViewById(R.id.emailField);

        HashMap<String, TextView> fields = new HashMap<String, TextView>();
        fields.put(USERNAME_KEY, mUserNameField);
        fields.put(PASSWORD_KEY, mPasswordField);
        fields.put(EMAIL_KEY, mEmailField);

        final InputForm form = new InputForm(fields);
        InputForm.EmptyCheck emptyCheck = InputForm.EmptyCheck.getInstance();
        form.addValidationCheck(USERNAME_KEY, emptyCheck);
        form.addValidationCheck(PASSWORD_KEY, InputForm.ValidPasswordCheck.getInstance());
        form.addValidationCheck(PASSWORD_KEY, emptyCheck);
        form.addValidationCheck(EMAIL_KEY, InputForm.ValidEmailCheck.getInstance());
        form.addValidationCheck(EMAIL_KEY, emptyCheck);

        Button button = (Button) findViewById(R.id.signUpButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(form.areFieldsValid()){
                    HashMap<String, String> formValues = form.extractFormValues();
                    mUserName = formValues.get(USERNAME_KEY);
                    mPassword = formValues.get(PASSWORD_KEY);
                    mEmail = formValues.get(EMAIL_KEY);

                    ParseUser newUser = new ParseUser();
                    newUser.setPassword(mPassword);
                    newUser.setEmail(mEmail);
                    newUser.setUsername(mUserName);

                    setProgressBarIndeterminateVisibility(true);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if(e == null){
                                //Success!
                                Utils.switchActivity(SignUpActivity.this, MainActivity.class);
                            } else {
                                Utils.displayErrorDialog(e.getMessage(),
                                        getString(R.string.error_dialog_title),
                                        SignUpActivity.this);
                            }
                        }
                    });
                }
            }
        });
    }
}
