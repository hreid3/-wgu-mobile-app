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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static edu.wgu.hreid6.wgugo.FormHelper.*;
import static android.util.Log.*;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import edu.wgu.hreid6.wgugo.adapter.AssessmentListAdapter;
import edu.wgu.hreid6.wgugo.adapter.CoursesListAdapter;
import edu.wgu.hreid6.wgugo.data.model.Assessment;
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
                final Collection<Assessment> assessments = course.getAssessments();
                if (assessments != null) {
                    final AssessmentListAdapter assessmentListAdapter = new AssessmentListAdapter(this, R.layout.list_assessment_item, new ArrayList<Assessment>(assessments), R.layout.list_assessment_item);
                    final ListView listView = (ListView) findViewById(R.id.term_assessments_list);
                    listView.setAdapter(assessmentListAdapter);
                    // Set the header of the ListView
                    final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.list_assessment_header, listView, false);
                    listView.addHeaderView(rowView);


//                    listView.setClickable(false);
                    setListViewHeightBasedOnChildren(listView);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                Intent intent = new Intent(CourseDetailActivity.this, AssessmentActivity.class);
                                Assessment assessment = (new ArrayList<Assessment>(assessments)).get(position);
                                intent.putExtra(ASSESSMENT_ID, assessment.getId());
                                intent.putExtra(COURSE_ID, getIntent().getIntExtra(COURSE_ID, -1));
                                startActivity(intent);
                            } catch (Exception e) {
                                e(getLocalClassName(), "graduate is null", e);
                            }

                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this addsc items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_SAVE_COURSE, 100, R.string.save);
        menu.add(0, MENU_ITEM_ASSESSMENT, 90, "Assessment"); // Only add assessments for saved courses
        menu.findItem(MENU_ITEM_SAVE_COURSE).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.findItem(MENU_ITEM_ASSESSMENT).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        TextView anIdFld = (TextView)viewGroup.findViewById(R.id.id_course);
        switch (id) {
            case MENU_ITEM_ASSESSMENT:
                if ( anIdFld == null && anIdFld.getText() != null && anIdFld.getText().toString().length() > 0) {
                    Intent intent = new Intent(this, AssessmentActivity.class);
                    intent.putExtra(COURSE_ID, anIdFld.getText().toString().length());
                    startActivityForResult(intent, anIdFld.getText().toString().length());
                    return true;
                }
                // Fall through and let save do its thing because it is new Course
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
                            // Reset all courses, then activate the selected ones
                            if (course.getAssessments() != null) {
                                for (Assessment assessment : course.getAssessments()) {
                                    assessment.setCourse(null);
                                    assessmentDao.createOrUpdate(assessment);
                                }
                            }

                            final ListView listView = (ListView) findViewById(R.id.term_assessments_list);
                            HeaderViewListAdapter hvla = (HeaderViewListAdapter) listView.getAdapter();
                            ArrayAdapter<Assessment> la = (ArrayAdapter<Assessment>) hvla.getWrappedAdapter();
                            for(int i=0; i < la.getCount(); i++) {
                                Assessment assessment = (Assessment) la.getItem(i);
                                assessment = assessmentDao.getById(assessment.getId()); // reattach
                                assessment.setCourse(course);
                                assessmentDao.createOrUpdate(assessment);
                            }

                            i(getLocalClassName(), "create or update for course success:  " + course.getTitle());
                            saySomething("Course successfully saved.");
                            if(id == MENU_ITEM_SAVE_COURSE) {
                                startActivity(new Intent(this, CoursesLandingActivity.class));
                            } else {
                                Intent intent = new Intent(this, AssessmentActivity.class);
                                intent.putExtra(COURSE_ID, getIntent().getIntExtra(COURSE_ID, -1));
                                startActivity(intent);
                            }
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

    public void deleteAssessmentFromGrid(View v) {
        final ListView listView = (ListView) findViewById(R.id.term_assessments_list);
        ArrayAdapter<Assessment> la = (ArrayAdapter<Assessment>)listView.getAdapter();

        Object tag = v.getTag();
        if (tag != null) {
            Integer i = new Integer(tag.toString());
            try {
                Assessment c = assessmentDao.getById(i);
                la.remove(c);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
