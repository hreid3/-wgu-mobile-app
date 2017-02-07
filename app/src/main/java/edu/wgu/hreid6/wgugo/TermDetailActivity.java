package edu.wgu.hreid6.wgugo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import edu.wgu.hreid6.wgugo.data.model.Graduate;
import edu.wgu.hreid6.wgugo.data.model.Term;

import static android.util.Log.e;
import static android.util.Log.i;
import static edu.wgu.hreid6.wgugo.FormHelper.getDateFromTextView;
import static edu.wgu.hreid6.wgugo.FormHelper.getDisplayDate;
import static edu.wgu.hreid6.wgugo.FormHelper.isEmailValid;
import static edu.wgu.hreid6.wgugo.FormHelper.isEmpty;

public class TermDetailActivity extends BaseAndroidActivity {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_term_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set the defaults...
        Spinner statuses = (Spinner) findViewById(R.id.fld_term_status);
        ArrayAdapter<Term.STATUS> adapter =new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Term.STATUS.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statuses.setAdapter(adapter);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        ((TextView)findViewById(R.id.ro_start_date)).setText(getDisplayDate(year, month, day));
        ((TextView)findViewById(R.id.ro_end_date)).setText(getDisplayDate(year, month, day));

        Integer termId = getIntent().getIntExtra(TERM_ID, -1);
        if (!(termId < 0)) {
            Term term = null;
            try {
                term = termDao.getById(termId);
            } catch (SQLException e) {
                e(getLocalClassName(), "no term with id " + termId, e);
            }
            if (term != null) {
                TextView textView = (TextView)findViewById(R.id.id_term);
                textView.setText(termId.toString());
                EditText title = (EditText) findViewById(R.id.fld_term_title);
                TextView startDate = (TextView) findViewById(R.id.ro_start_date);
                TextView endDate = (TextView) findViewById(R.id.ro_end_date);
//                Spinner status = (Spinner) findViewById(R.id.fld_term_status);

                title.setText(term.getTitle());

                if (term.getStartDate() != null) {
                    startDate.setText(getDisplayDate(term.getStartDate()));
                }

                if (term.getEndDate() != null) {
                    endDate.setText(getDisplayDate(term.getEndDate()));
                }

                for(int i=0; i < Term.STATUS.values().length; i++) {
                    if (term.getStatus() == Term.STATUS.values()[i]) {
                        statuses.setSelection(i, true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this addsc items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_SAVE_TERM, 100, R.string.save);
        menu.findItem(MENU_ITEM_SAVE_TERM).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_SAVE_TERM:
                Snackbar.make(getViewGroup(), "Saving...", Snackbar.LENGTH_SHORT).setAction("Action", null).show(); // Progress

                try {
                    // Title cannot be empty
                    // Start Date and End dates cannot be empty
                    // End must not be less than start date
                    EditText title = (EditText) viewGroup.findViewById(R.id.fld_term_title);
                    TextView startDate = (TextView) viewGroup.findViewById(R.id.ro_start_date);
                    TextView endDate = (TextView) viewGroup.findViewById(R.id.ro_end_date);
                    Spinner status = (Spinner) viewGroup.findViewById(R.id.fld_term_status);

                    if (isFormValid(title, startDate, endDate, status)) {
                        Graduate graduate = getGraduate();

                        Term term = null;
                        TextView anIdFld = (TextView)viewGroup.findViewById(R.id.id_term);
                        if ( anIdFld != null && anIdFld.getText() != null && anIdFld.getText().toString().length() > 0) {
                            term = termDao.getById(Integer.parseInt(anIdFld.getText().toString()));
                        } else {
                            term = new Term();
                        }
                        term.setTitle(title.getText().toString());
                        term.setStartDate(getDateFromTextView(startDate));
                        term.setEndDate(getDateFromTextView(endDate));
                        term.setStatus((Term.STATUS)status.getSelectedItem());
                        term.setGraduate(graduate);

                        if (termDao.createOrUpdate(term)) {
                            i(getLocalClassName(), "create or update for term success:  " + term.getTitle());
                            Context context = getApplicationContext();
                            CharSequence text = "Term successfully saved.";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            startActivity(new Intent(this, TermsLandingActivity.class));
                        }
                    } else {
                        return false;
                    }
                } catch (Exception ex) {
                    e(getLocalClassName(), "Could not create or update term", ex);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isFormValid(EditText title, TextView start, TextView end, Spinner status) throws Exception {
        boolean valid = true;
        if (!isEmpty(viewGroup, title, R.id.fld_msg_term_title)) {
            valid = false;
        }
        Date startDate = getDateFromTextView(start);
        Date endDate = getDateFromTextView(end);

        if (startDate.getTime() > endDate.getTime()) {
            ((TextView)viewGroup.findViewById(R.id.fld_msg_start_date)).setText("Start date cannot be greater than end date.");
            valid = false;
        }

        // TODO: need rules for status based on Term metadata
        return valid;

    }

    @Override
    protected ViewGroup getViewGroup() {
        return this.viewGroup;
    }
}
