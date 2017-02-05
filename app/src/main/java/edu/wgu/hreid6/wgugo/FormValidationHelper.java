package edu.wgu.hreid6.wgugo;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hreid on 2/5/17.
 */

public class FormValidationHelper {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isEmpty(ViewGroup viewGroup, EditText e, int messageId) {
        TextView msgFld = (TextView)viewGroup.findViewById(messageId);
        if (!(e != null && e.getText() != null && e.getText().length() > 0)) {
            if (msgFld != null) {
                msgFld.setText("Cannot be empty");
            }
            return false;
        } else {
            if (msgFld != null) {
                msgFld.setText("");
            }
            return true;
        }
    }

    public static boolean isEmailValid(ViewGroup viewGroup, EditText e, int messageId) {
       String email = e.getText().toString();
        boolean isValid = true;
        if (email == null || e.getText() == null || e.getText().length() == 0) {
            ((TextView)viewGroup.findViewById(messageId)).setText("Cannot be empty");
            isValid = false;
        } else {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            isValid = matcher.matches();
            if (!isValid) {
                ((TextView)viewGroup.findViewById(messageId)).setText("Please enter a valid email.");
            }
        }
        if (isValid) {
            ((TextView)viewGroup.findViewById(messageId)).setText("");
        }
        return isValid;
    }
}
