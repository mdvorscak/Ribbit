package mikedvorscak.com.ribbit;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


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
        setContentView(R.layout.activity_sign_up);

        mUserNameField = (EditText) findViewById(R.id.userNameField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mEmailField = (EditText) findViewById(R.id.emailField);

        Button button = (Button) findViewById(R.id.signUpButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(areFieldsValid()){
                    ParseUser newUser = new ParseUser();
                    newUser.setPassword(mPassword);
                    newUser.setEmail(mEmail);
                    newUser.setUsername(mUserName);

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                //Success!
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                displayErrorDialog(e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void displayErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setMessage(message)
               .setTitle(getString(R.string.signup_error_dialog_title))
               .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean areFieldsValid() {
        // Reset errors.
        mUserNameField.setError(null);
        mPasswordField.setError(null);
        mEmailField.setError(null);

        // Store values at the time of the login attempt.
        mUserName = mUserNameField.getText().toString();
        mEmail = mEmailField.getText().toString();
        mPassword = mPasswordField.getText().toString();

        boolean valid = true;
        EditText focusText = null;

        //Check in reverse form order, so the first thing wrong will be focused on

        // Check for a valid email address.
        if (!isEmailValid(mEmail)) {
            mEmailField.setError(getString(R.string.error_invalid_email));
            focusText = mEmailField;
            valid = false;
        }

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(mPassword)){
            focusText = mPasswordField;
            mPasswordField.setError(getString(R.string.password_required_error));
            valid = false;
        } else if (!isPasswordValid(mPassword)) {
            mPasswordField.setError(getString(R.string.error_invalid_password));
            focusText = mPasswordField;
            valid = false;
        }

        if(TextUtils.isEmpty(mUserName)){
            focusText = mUserNameField;
            mUserNameField.setError(getString(R.string.username_required_error));
            valid = false;
        }

        if (!valid) {
            focusText.requestFocus();
        }
        return valid;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Implement password checking (length, characters, etc.)
        return true;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
