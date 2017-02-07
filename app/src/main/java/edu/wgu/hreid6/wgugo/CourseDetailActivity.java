package edu.wgu.hreid6.wgugo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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

import static edu.wgu.hreid6.wgugo.FormHelper.*;
import static android.util.Log.*;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.Graduate;

public class CourseDetailActivity extends BaseAndroidActivity {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // TODO: disable if someone changes the value.

        // Set the defaults...
        Spinner statuses = (Spinner) findViewById(R.id.fld_course_status);
        ArrayAdapter<Course.STATUS> adapter =new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Course.STATUS.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statuses.setAdapter(adapter);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        ((TextView)findViewById(R.id.ro_start_date)).setText(getDisplayDate(year, month, day));
        ((TextView)findViewById(R.id.ro_end_date)).setText(getDisplayDate(year, month, day));

        Integer courseId = getIntent().getIntExtra(COURSE_ID, -1);
        if (!(courseId < 0)) {
            Course course = null;
            try {
                course = courseDao.getById(courseId);
            } catch (SQLException e) {
                e(getLocalClassName(), "no course with id " + courseId, e);
            }
            if (course != null) {
                TextView textView = (TextView)findViewById(R.id.id_course);
                textView.setText(courseId.toString());
                EditText title = (EditText) findViewById(R.id.fld_course_title);
                EditText mentor = (EditText) findViewById(R.id.fld_mentor);
                TextView startDate = (TextView) findViewById(R.id.ro_start_date);
                TextView endDate = (TextView) findViewById(R.id.ro_end_date);
//                Spinner status = (Spinner) findViewById(R.id.fld_course_status);
                TextView mentorPhone = (EditText) findViewById(R.id.fld_mentor_phone);
                TextView mentorEmail = (EditText) findViewById(R.id.fld_mentor_email);
                TextView courseNotes = (EditText) findViewById(R.id.fld_notes);

                title.setText(course.getTitle());
                mentor.setText(course.getCourseMentorName());
                mentorPhone.setText(course.getCourseMentorPhone());
                mentorEmail.setText(course.getCourseMentorEmail());
                courseNotes.setText(course.getCourseNotes() != null ? course.getCourseNotes() : "");

                if (course.getStartDate() != null) {
                    startDate.setText(getDisplayDate(course.getStartDate()));
                }

                if (course.getEndDate() != null) {
                    endDate.setText(getDisplayDate(course.getEndDate()));
                }

                for(int i=0; i < Course.STATUS.values().length; i++) {
                    if (course.getStatus() == Course.STATUS.values()[i]) {
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

                // Validate form
                try {
                    // Title cannot be empty
                    // Start Date and End dates cannot be empty
                    // End must not be less than start date
                    // Course mentor cannot be null
                    EditText title = (EditText) viewGroup.findViewById(R.id.fld_course_title);
                    EditText mentor = (EditText) viewGroup.findViewById(R.id.fld_mentor);
                    TextView startDate = (TextView) viewGroup.findViewById(R.id.ro_start_date);
                    TextView endDate = (TextView) viewGroup.findViewById(R.id.ro_end_date);
                    Spinner status = (Spinner) viewGroup.findViewById(R.id.fld_course_status);
                    EditText mentorPhone = (EditText) viewGroup.findViewById(R.id.fld_mentor_phone);
                    EditText mentorEmail = (EditText) viewGroup.findViewById(R.id.fld_mentor_email);
                    EditText courseNotes = (EditText) viewGroup.findViewById(R.id.fld_notes);

                    if (isFormValid(title, mentor, startDate, endDate, status, mentorPhone, mentorEmail)) {
                        Graduate graduate = getGraduate();

                        Course course = null;
                        TextView anIdFld = (TextView)viewGroup.findViewById(R.id.id_course);
                        if ( anIdFld != null && anIdFld.getText() != null && anIdFld.getText().toString().length() > 0) {
                            course = courseDao.getById(Integer.parseInt(anIdFld.getText().toString()));
                        } else {
                            course = new Course();
                        }
                        course.setCourseMentorName(mentor.getText().toString());
                        course.setTitle(title.getText().toString());
                        course.setStartDate(getDateFromTextView(startDate));
                        course.setEndDate(getDateFromTextView(endDate));
                        course.setCourseMentorPhone(mentorPhone.getText().toString());
                        course.setCourseMentorEmail(mentorEmail.getText().toString());
                        if (courseNotes.getText() != null && courseNotes.getText().length() > 0) {
                            course.setCourseNotes(courseNotes.getText().toString());
                        }

                        course.setStatus((Course.STATUS)status.getSelectedItem());
                        course.setGraduate(graduate);

                        if (courseDao.createOrUpdate(course)) {
                            i(getLocalClassName(), "create or update for course success:  " + course.getTitle());
                            Context context = getApplicationContext();
                            CharSequence text = "Course successfully saved.";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            startActivity(new Intent(this, CoursesLandingActivity.class));
                        }
                    } else {
                        return false;
                    }
                } catch (Exception ex) {
                    e(getLocalClassName(), "Could not create or update course", ex);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isFormValid(EditText title, EditText mentor, TextView start, TextView end, Spinner status, EditText mentorPhone, EditText mentorEmail) throws Exception {
        boolean valid = true;
        if (!isEmpty(viewGroup, title, R.id.fld_msg_course_title)) {
            valid = false;
        }
        if (!isEmpty(viewGroup, mentor, R.id.fld_msg_mentor)) {
            valid = false;
        }
        if (!isEmpty(viewGroup, mentorPhone, R.id.fld_msg_mentor_phone)) {
            valid = false;
        }
        if (!isEmailValid(viewGroup, mentorEmail, R.id.fld_msg_mentor_email)) {
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
    public ViewGroup getViewGroup() {
        return this.viewGroup;
    }


}
