package mikedvorscak.com.ribbit.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;

import mikedvorscak.com.ribbit.utils.InputForm;
import mikedvorscak.com.ribbit.R;
import mikedvorscak.com.ribbit.utils.Utils;


public class LoginActivity extends ActionBarActivity {

    protected TextView mSignUpTextView;
    protected EditText mUserNameField;
    protected EditText mPasswordField;
    protected ProgressBar mProgressBar;

    private String mUserName;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hide the action bar
        getSupportActionBar().hide();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

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

                    toggleProgressBar(true);
                    ParseUser.logInInBackground(mUserName, mPassword, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            toggleProgressBar(false);
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

    private void toggleProgressBar(boolean on) {
        if(on){
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
