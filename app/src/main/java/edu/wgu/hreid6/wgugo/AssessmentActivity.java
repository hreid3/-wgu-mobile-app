package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Calendar;

import edu.wgu.hreid6.wgugo.data.model.Assessment;
import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.Graduate;

import static android.util.Log.e;
import static android.util.Log.i;
import static edu.wgu.hreid6.wgugo.FormHelper.getDateFromTextView;
import static edu.wgu.hreid6.wgugo.FormHelper.getDisplayDate;

public class AssessmentActivity extends BaseAndroidActivity {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_content_assessment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner statuses = (Spinner) findViewById(R.id.fld_assessment_type);
        ArrayAdapter<Assessment.TYPE> adapter =new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Assessment.TYPE.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statuses.setAdapter(adapter);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        ((TextView)findViewById(R.id.ro_due_date)).setText(getDisplayDate(year, month, day));

        Integer assessmentId = getIntent().getIntExtra(ASSESSMENT_ID, -1);
        if (!(assessmentId < 0)) {
            Assessment assessment = null;
            try {
                assessment = assessmentDao.getById(assessmentId);
            } catch (SQLException e) {
                e(getLocalClassName(), "no assessment with id " + assessmentId, e);
            }
            if (assessment != null) {
                TextView textView = (TextView)findViewById(R.id.id_assessment);
                textView.setText(assessmentId.toString());
                TextView dueDate = (TextView) findViewById(R.id.ro_due_date);
                TextView assessmentsNotes = (EditText) findViewById(R.id.fld_notes);

                assessmentsNotes.setText(assessment.getNotes() != null ? assessment.getNotes() : "");

                if (assessment.getDueDate() != null) {
                    dueDate.setText(getDisplayDate(assessment.getDueDate()));
                }
                for(int i=0; i < Assessment.TYPE.values().length; i++) {
                    if (assessment.getType() == Assessment.TYPE.values()[i]) {
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
        menu.add(0, MENU_ITEM_SAVE_ASSESSMENT, 100, R.string.save);
        menu.findItem(MENU_ITEM_SAVE_ASSESSMENT).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        // your code.
        super.onBackPressed();
        Intent intent = new Intent(this, CourseDetailActivity.class);
        Integer i = getIntent().getIntExtra(COURSE_ID, -1);
        intent.putExtra(COURSE_ID, i);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        Intent intent = new Intent(this, CourseDetailActivity.class);
        switch (id) {
            case android.R.id.home:
                Integer i = getIntent().getIntExtra(COURSE_ID, -1);
                intent.putExtra(COURSE_ID, i);
                startActivity(intent);
                return true;
            case MENU_ITEM_SAVE_ASSESSMENT:
                Snackbar.make(getViewGroup(), "Saving...", Snackbar.LENGTH_LONG).setAction("Action", null).show(); // Progress
                // Validate form
                try {
                    // Title cannot be empty
                    // Start Date and End dates cannot be empty
                    // End must not be less than start date
                    // Course mentor cannot be null
                    TextView dueDate = (TextView) viewGroup.findViewById(R.id.ro_due_date);
                    Spinner type = (Spinner) viewGroup.findViewById(R.id.fld_assessment_type);
                    EditText notes = (EditText) viewGroup.findViewById(R.id.fld_notes);

                    if (true) {
//                        Graduate graduate = getGraduate();
                        Course course = courseDao.getById(getIntent().getIntExtra(COURSE_ID, -1));
                        if (course == null) {
                            throw new Exception("Where is the course for " + getIntent().getIntExtra(COURSE_ID, -1));
                        }
                        Assessment assessment = null;
                        TextView anIdFld = (TextView)viewGroup.findViewById(R.id.id_assessment);
                        if ( anIdFld != null && anIdFld.getText() != null && anIdFld.getText().toString().length() > 0) {
                            assessment = assessmentDao.getById(Integer.parseInt(anIdFld.getText().toString()));
                        } else {
                            assessment = new Assessment();
                        }
                        assessment.setDueDate(getDateFromTextView(dueDate));
                        if (notes.getText() != null && notes.getText().length() > 0) {
                            assessment.setNotes(notes.getText().toString());
                        }

                        assessment.setType((Assessment.TYPE)type.getSelectedItem());
                        assessment.setCourse(course);

                        if (assessmentDao.createOrUpdate(assessment)) {
                            i(getLocalClassName(), "create or update for assessment success:  " + course.getTitle());
                            saySomething("Assessment successfully saved.");
                            intent.putExtra(COURSE_ID, course.getId());
                            intent.putExtra(ASSESSMENT_ID, assessment.getId());
                            startActivity(intent);
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

    @Override
    protected ViewGroup getViewGroup() {
        return viewGroup;
    }
}
