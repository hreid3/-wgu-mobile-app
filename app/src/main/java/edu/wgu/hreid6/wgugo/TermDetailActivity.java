package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import edu.wgu.hreid6.wgugo.adapter.CoursesListAdapter;
import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.Graduate;
import edu.wgu.hreid6.wgugo.data.model.Term;

import static android.util.Log.e;
import static android.util.Log.i;
import static edu.wgu.hreid6.wgugo.FormHelper.getDateFromTextView;
import static edu.wgu.hreid6.wgugo.FormHelper.getDisplayDate;
import static edu.wgu.hreid6.wgugo.FormHelper.isEmpty;
import static edu.wgu.hreid6.wgugo.FormHelper.setListViewHeightBasedOnChildren;

public class TermDetailActivity extends BaseAndroidActivity {

    private ViewGroup viewGroup;
    private ListPopupWindow listPopupWindow; // We need to maintain state for courses popup.
    private CoursesListAdapter coursesListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_term_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Collection<Course> termCourses = new ArrayList<>();

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
                termCourses = term.getCourses();
            }
        }

        listPopupWindow = new ListPopupWindow(this);
        Collection<Course> courses = null;
        try {
            courses = courseDao.getCoursesNotInTerm();
        } catch (SQLException e) {
            e(getLocalClassName(), "could not get courses", e);
        }
        Collection<Course> deltaCourses = new ArrayList<>(); // This looks better in Java 8
        for(Course aCourse : courses) {
            if (!termCourses.contains(aCourse)) {
                deltaCourses.add(aCourse);
            }
        }
        coursesListAdapter = new CoursesListAdapter(this, R.layout.list_course_item, new ArrayList<Course>(deltaCourses), R.layout.list_term_course_item);
        final CoursesListAdapter termCoursesListAdapter = new CoursesListAdapter(this, R.layout.list_course_item, new ArrayList<Course>(termCourses), R.layout.list_course_item);
        final ListView listView = (ListView) findViewById(R.id.term_courses_list);
        listView.setAdapter(termCoursesListAdapter);
        listView.setClickable(false);
        listPopupWindow.setAdapter(coursesListAdapter);
        listPopupWindow.setAnchorView(findViewById(R.id.btn_add_course_to_term));
        listPopupWindow.setWidth(measureContentWidth(coursesListAdapter));
        listPopupWindow.setModal(true);
        setListViewHeightBasedOnChildren(listView);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course item = coursesListAdapter.getItem(position);
                coursesListAdapter.remove(item);
                termCoursesListAdapter.add(item);
                setListViewHeightBasedOnChildren(listView);
                termCoursesListAdapter.notifyDataSetChanged();
                coursesListAdapter.notifyDataSetChanged();
                listPopupWindow.dismiss();
            }
        });
    }

    public void addCourseToTerm(View view) {
        if (listPopupWindow != null) {
            listPopupWindow.show();
        }
    }

    public void deleteCourseFromGrid(View v) {
        final ListView listView = (ListView) findViewById(R.id.term_courses_list);
        ArrayAdapter<Course> la = (ArrayAdapter<Course>)listView.getAdapter();

        Object tag = v.getTag();
        if (tag != null) {
            Integer i = new Integer(tag.toString());
            try {
                Course c = courseDao.getById(i);
                la.remove(c);
                coursesListAdapter.add(c);
            } catch (SQLException e) {
                e.printStackTrace();
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

                        final ListView listView = (ListView) findViewById(R.id.term_courses_list);
                        ListAdapter la = listView.getAdapter();
                        if (termDao.createOrUpdate(term)) {
                            i(getLocalClassName(), "create or update for term success:  " + term.getTitle());
                            saySomething("Term successfully saved.");
                            startActivity(new Intent(this, TermsLandingActivity.class));
                        }
                        // Reset all courses, then activate the selected ones
                        if (term.getCourses() != null) {
                            for (Course course : term.getCourses()) {
                                course.setTerm(null);
                                courseDao.createOrUpdate(course);
                            }
                        }
                        for(int i=0; i < la.getCount(); i++) {
                            Course course = (Course) la.getItem(i);
                            course = courseDao.getById(course.getId()); // reattach
                            course.setTerm(term);
                            courseDao.createOrUpdate(course);
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

    private int measureContentWidth(ListAdapter adapter) {
        // Menus don't tend to be long, so this is more sane than it looks.
        int width = 0;
        View itemView = null;
        int itemType = 0;
        final int widthMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            itemView = adapter.getView(i, itemView, new FrameLayout(this));
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }
        return width;
    }


}
