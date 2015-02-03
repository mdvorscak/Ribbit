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
        setContentView(R.layout.activity_sign_up);

        mUserNameField = (EditText) findViewById(R.id.userNameField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mEmailField = (EditText) findViewById(R.id.emailField);

        HashMap<String, TextView> fields = new HashMap<String, TextView>();
        fields.put("username", mUserNameField);
        fields.put("password", mPasswordField);
        fields.put("email", mEmailField);

        final InputForm form = new InputForm(fields);
        InputForm.EmptyCheck emptyCheck = InputForm.EmptyCheck.getInstance();
        form.addValidationCheck("username", emptyCheck);
        form.addValidationCheck("password", InputForm.ValidPasswordCheck.getInstance());
        form.addValidationCheck("password", emptyCheck);
        form.addValidationCheck("email", InputForm.ValidEmailCheck.getInstance());
        form.addValidationCheck("email", emptyCheck);

        Button button = (Button) findViewById(R.id.signUpButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(form.areFieldsValid()){
                    HashMap<String, String> formValues = form.extractFormValues();
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
