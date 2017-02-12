package edu.wgu.hreid6.wgugo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Calendar;

import edu.wgu.hreid6.wgugo.data.dao.AssessmentDao;
import edu.wgu.hreid6.wgugo.data.dao.CourseDao;
import edu.wgu.hreid6.wgugo.data.dao.GraduateDao;
import edu.wgu.hreid6.wgugo.data.dao.TermDao;
import edu.wgu.hreid6.wgugo.data.dao.WguEventDao;
import edu.wgu.hreid6.wgugo.data.model.Graduate;
import edu.wgu.hreid6.wgugo.data.model.WguEvent;

import static edu.wgu.hreid6.wgugo.FormHelper.getDisplayDate;

/**
 * Created by hreid on 2/3/17.
 */

abstract class BaseAndroidActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    protected static final int MENU_ITEM_LOGOUT = 0x1020;
    protected static final int MENU_ITEM_PROFILE = 0x1005;

    protected static final int MENU_ITEM_COURSES_LIST = 0x1100;
    protected static final int MENU_ITEM_TERMS_LIST = 0x1200;
    protected static final int MENU_ITEM_ADD_COURSE = 0x2000;
    protected static final int MENU_ITEM_ADD_TERM = 0x3000;

    protected static final int MENU_ITEM_SAVE_COURSE = 0x2010;
    protected static final int MENU_ITEM_SAVE_TERM = 0x3010;

    protected static final int MENU_ITEM_SAVE_PROFILE = 0x4000;
    protected static final int MENU_ITEM_SAVE_ASSESSMENT = 0x5000;
    protected static final int MENU_ITEM_ASSESSMENT = 0x5010;

    protected static final int MENU_ITEM_HOME = 0x0199;
    protected static final int MENU_ITEM_SCHEDULE = 0x6000;

    protected static final int MENU_ITEM_SHARE = 0x7000;

    public static final String COURSE_ID = "courseId";
    public static final String TERM_ID = "termId";
    public static final String ASSESSMENT_ID = "assessmentId";

    protected int openedDateDialogId; // shucks we have to keep more state.

    CourseDao courseDao; // I do not like each instance getting a copy of an dao

    TermDao termDao; // I do not like this

    GraduateDao graduateDao;

    AssessmentDao assessmentDao;

    public BaseAndroidActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (courseDao == null) {
            courseDao = new CourseDao(this);
        }
        if (termDao == null) {
            termDao = new TermDao(this);
        }
        if (graduateDao == null) {
            graduateDao = new GraduateDao(this);
        }
        if (assessmentDao == null) {
            assessmentDao = new AssessmentDao(this);
        }
        super.onCreate(savedInstanceState);
    }

    protected Graduate getGraduate() throws SQLException {
        Graduate graduate = null;
        if ( graduateDao != null) {
            graduate = graduateDao.getGraduate();
        }
        return graduate;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_HOME, 1, "Home");
        menu.add(1, MENU_ITEM_PROFILE, 300, R.string.profile);
        menu.add(2, MENU_ITEM_LOGOUT, 9999, R.string.logout);
        if (this instanceof Schedulable && ((Schedulable)this).isScheduleable()) {
            menu.add(3, MENU_ITEM_SCHEDULE, 500, "Schedule Notifications");
        }
        if (this instanceof Sharable && ((Sharable)this).isScheduleable()) {
            menu.add(3, MENU_ITEM_SHARE, 600, "Send Notes");
        }
        return true;
    }

    protected void addEvent() {
        if (this instanceof Schedulable) {
            WguEventDao wguEventDao = new WguEventDao();
            wguEventDao.addEvent(this, ((Schedulable) this).getWguEvent());
        }
    }

    protected void share() {
        if (this instanceof Sharable) {
            WguEvent wguEvent = ((Sharable)this).getWguEvent();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing notes for " + wguEvent.getTitle());
            i.putExtra(Intent.EXTRA_TEXT   , wguEvent.getNotes());
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case MENU_ITEM_HOME:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case MENU_ITEM_PROFILE:
                startActivity(new Intent(this, GraduateFormActivity.class));
                return true;
            case MENU_ITEM_LOGOUT:
                Snackbar.make(getViewGroup(), "You are logged out, bye.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                System.exit(0);
                return true;
            case MENU_ITEM_SCHEDULE:
                addEvent();
                return true;
            case MENU_ITEM_SHARE:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    abstract protected ViewGroup getViewGroup();

    @Override
    protected void onDestroy() {
        if (courseDao != null) {
            courseDao.releaseResources();
            courseDao = null;
        }
        if (termDao != null) {
            termDao.releaseResources();;
            termDao = null;
        }
        if (graduateDao != null) {
            graduateDao.releaseResources();
            graduateDao = null;
        }
        super.onDestroy();
    }

    public static class DatePickerFragment extends DialogFragment {

        Activity activity;
        DatePickerDialog.OnDateSetListener onDateSetListener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(activity, onDateSetListener, year, month, day);
        }
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.activity = this;
        newFragment.onDateSetListener = this;
        openedDateDialogId = v.getId();
        newFragment.show(getFragmentManager(), "datePicker");
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
            case R.id.btn_due_date_button:
                ((TextView)findViewById(R.id.ro_due_date)).setText(dateString);
                break;
        }
        openedDateDialogId = 0;
    }

    public void saySomething(String message) {
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();

    }

}
