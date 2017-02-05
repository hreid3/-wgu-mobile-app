package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;

import static edu.wgu.hreid6.wgugo.FormValidationHelper.*;

public class GraduateFormActivity extends BaseAndroidActivity {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graduate_form);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_graduate_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_SAVE_PROFILE:
                // Validate form fields
                if (isFormValid()) {
                    // Save the data in Dao and go to main activity
                    startActivity(new Intent(this, MainActivity.class));
                } else {

                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isFormValid() {
        boolean valid = true;
        EditText firstName = (EditText) viewGroup.findViewById(R.id.fld_first_name);
        EditText lasttName = (EditText) viewGroup.findViewById(R.id.fld_last_name);
        EditText email = (EditText) viewGroup.findViewById(R.id.fld_email);

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
