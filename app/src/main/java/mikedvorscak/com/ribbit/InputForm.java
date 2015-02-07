package mikedvorscak.com.ribbit;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mike on 2/1/15.
 * Used for form validation and value extraction
 */
public class InputForm {

    //TODO:Add validation order
    private HashMap<String, TextView> mFieldMap;
    private HashMap<String, String> mFieldValuesMap = new HashMap<String, String>();
    private HashMap<String, ArrayList<ValidationCheck>> mFieldValidations = new HashMap<String, ArrayList<ValidationCheck>>();

    public InputForm(HashMap<String, TextView> fields) {
        mFieldMap = fields;
    }

    public HashMap<String, String> extractFormValues() {
        return mFieldValuesMap;
    }

    public boolean areFieldsValid() {
        boolean valid = true;
        EditText focusText = null;
        for(Map.Entry<String, TextView> field : mFieldMap.entrySet()){
            String currentFieldKey = field.getKey();
            TextView currentField = field.getValue();
            String currentFieldValue = currentField.getText().toString();

            currentField.setError(null);
            mFieldValuesMap.put(currentFieldKey, currentFieldValue);

            //Run validations
            ArrayList<ValidationCheck> currentValidations = mFieldValidations.get(currentFieldKey);
            if(currentValidations != null){
                for (ValidationCheck currentValidation : currentValidations) {
                    if(!currentValidation.isValid(currentFieldValue)){
                        currentField.setError(currentValidation.getErrorMessage());
                        focusText = (EditText) currentField;
                        valid = false;
                    }
                }
            }
        }
        if(!valid){
            focusText.requestFocus();
        }
        return valid;
    }

    public void addValidationCheck(String fieldName, InputForm.ValidationCheck check){
        ArrayList<ValidationCheck> validations = mFieldValidations.get(fieldName);
        if(validations == null){
            validations = new ArrayList<ValidationCheck>();
        }
        validations.add(check);
        mFieldValidations.put(fieldName, validations);
    }

    //ValidationCheck stuff below

    public static interface ValidationCheck{
        public boolean isValid(String value);

        public String getErrorMessage();
    }

    //Some standard checks
    public static class EmptyCheck implements ValidationCheck{
        //Singleton stuff to prevent instantiation, THERE CAN ONLY BE 1!
        private static EmptyCheck instance = null;
        protected EmptyCheck() {
            // Exists only to defeat instantiation.
        }
        public static EmptyCheck getInstance() {
            if(instance == null) {
                instance = new EmptyCheck();
            }
            return instance;
        }
        @Override
        public boolean isValid(String value) {
            return !TextUtils.isEmpty(value);
        }
        @Override
        public String getErrorMessage(){
            //TODO:Extract to resource
            return "Field cannot be empty";
        }
    }

    public static class ValidEmailCheck implements ValidationCheck{
        //Singleton stuff to prevent instantiation, THERE CAN ONLY BE 1!
        private static ValidEmailCheck instance = null;
        protected ValidEmailCheck() {
            // Exists only to defeat instantiation.
        }
        public static ValidEmailCheck getInstance() {
            if(instance == null) {
                instance = new ValidEmailCheck();
            }
            return instance;
        }
        @Override
        public boolean isValid(String value) {
            //TODO: Replace this with your own logic
            return value.contains("@");
        }
        @Override
        public String getErrorMessage(){
            //TODO:Extract to resource
            return "Email address is not valid";
        }
    }

    public static class ValidPasswordCheck implements ValidationCheck{
        //Singleton stuff to prevent instantiation, THERE CAN ONLY BE 1!
        private static ValidPasswordCheck instance = null;
        protected ValidPasswordCheck() {
            // Exists only to defeat instantiation.
        }
        public static ValidPasswordCheck getInstance() {
            if(instance == null) {
                instance = new ValidPasswordCheck();
            }
            return instance;
        }
        @Override
        public boolean isValid(String value) {
            //TODO: Replace this with your own logic
            return true;
        }
        @Override
        public String getErrorMessage(){
            //TODO:Extract to resource
            return "Password is not valid";
        }
    }
}
