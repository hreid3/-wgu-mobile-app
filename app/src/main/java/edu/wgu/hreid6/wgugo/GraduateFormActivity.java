package edu.wgu.hreid6.wgugo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import edu.wgu.hreid6.wgugo.data.model.Graduate;

import static android.util.Log.e;
import static android.util.Log.i;
import static android.util.Log.v;
import static edu.wgu.hreid6.wgugo.FormHelper.isEmailValid;
import static edu.wgu.hreid6.wgugo.FormHelper.isEmpty;

public class GraduateFormActivity extends BaseAndroidActivity {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graduate_form);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_graduate_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            Graduate graduate = getGraduate();
            if (graduate != null) {
                ((EditText) viewGroup.findViewById(R.id.fld_first_name)).setText(graduate.getFirstName());
                ((EditText) viewGroup.findViewById(R.id.fld_last_name)).setText(graduate.getLastName());
                ((EditText) viewGroup.findViewById(R.id.fld_email)).setText(graduate.getEmail());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } catch (Exception ex) {
            e(getLocalClassName(), "Error fetching graduate data", ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this addsc items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_SAVE_PROFILE, 100, getString(R.string.save));
        menu.findItem(MENU_ITEM_SAVE_PROFILE).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_SAVE_PROFILE:
                // Validate form fields
                try {
                    EditText firstName = (EditText) viewGroup.findViewById(R.id.fld_first_name);
                    EditText lasttName = (EditText) viewGroup.findViewById(R.id.fld_last_name);
                    EditText email = (EditText) viewGroup.findViewById(R.id.fld_email);
                    if (isFormValid(firstName, lasttName, email)) {
                        // Save the data in Dao and go to main activity
                        Graduate graduate = graduateDao.getGraduate();
                        if (graduate == null) {
                            graduate = new Graduate();
                        }
                        graduate.setEmail(email.getText().toString());
                        graduate.setLastName(lasttName.getText().toString());
                        graduate.setFirstName(firstName.getText().toString());
                        if (graduateDao.createOrUpdate(graduate)) {
                            i(getLocalClassName(), "create or update worked");
                            Context context = getApplicationContext();
                            CharSequence text = "Profile settings successfully saved.";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        v(getLocalClassName(), "graduate validation errors.");
                    }
                } catch (Exception ex) {
                    e(getLocalClassName(), "Error saving profile", ex);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isFormValid(EditText firstName, EditText lasttName, EditText email) {
        boolean valid = true;

        if (!isEmpty(viewGroup, firstName, R.id.fld_msg_first_name)) {
            valid = false;
        }
        if (!isEmpty(viewGroup, lasttName, R.id.fld_msg_last_name)) {
            valid = false;
        }
        if (!isEmailValid(viewGroup, email, R.id.fld_msg_email)) {
            valid = false;
        }
        return valid;
    }

    @Override
    protected ViewGroup getViewGroup() {
        return this.viewGroup;
    }

}
