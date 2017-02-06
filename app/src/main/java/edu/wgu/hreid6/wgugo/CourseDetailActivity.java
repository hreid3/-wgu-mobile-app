package edu.wgu.hreid6.wgugo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.Spinner;
import android.widget.TextView;
import static edu.wgu.hreid6.wgugo.FormHelper.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.wgu.hreid6.wgugo.data.model.Course;

public class CourseDetailActivity extends BaseAndroidActivity  implements DatePickerDialog.OnDateSetListener{

    private ViewGroup viewGroup;
    private int openedDateDialogId; // shucks we have to keep more state.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // TODO: disable if someone changes the value.

        // Set the defaults...
        Spinner statuses = (Spinner) findViewById(R.id.fld_statuses);
        ArrayAdapter<Course.STATUS> adapter =new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Course.STATUS.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statuses.setAdapter(adapter);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        ((TextView)findViewById(R.id.ro_start_date)).setText(getDisplayDate(year, month, day));
        ((TextView)findViewById(R.id.ro_end_date)).setText(getDisplayDate(year, month, day));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this addsc items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_SAVE_COURSE, 100, R.string.save);
        menu.findItem(MENU_ITEM_SAVE_COURSE).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_SAVE_COURSE:
                Snackbar.make(getViewGroup(), "Saving...", Snackbar.LENGTH_LONG).setAction("Action", null).show(); // Progress
                startActivity(new Intent(this, CoursesLandingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.activity = this;
        newFragment.onDateSetListener = this;
        openedDateDialogId = v.getId();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public ViewGroup getViewGroup() {
        return this.viewGroup;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        String dateString = getDisplayDate(year, month, day);
        switch (openedDateDialogId) {
            case R.id.btn_start_date_button:
                ((TextView)findViewById(R.id.ro_start_date)).setText(dateString);
                break;
            case R.id.btn_end_date_button:
                ((TextView)findViewById(R.id.ro_end_date)).setText(dateString);
                break;

        }
        view = view;
        openedDateDialogId = 0;
    }

}
