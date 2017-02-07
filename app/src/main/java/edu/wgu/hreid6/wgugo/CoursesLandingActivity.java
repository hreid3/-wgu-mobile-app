package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import static android.util.Log.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.wgu.hreid6.wgugo.adapter.CoursesListAdapter;
import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.Graduate;

public class CoursesLandingActivity extends BaseAndroidActivity  implements AdapterView.OnItemClickListener  {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_courses_landing);
        setContentView(R.layout.activity_courses_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Graduate graduate = null;

        try {
            graduate = getGraduate();
        } catch (SQLException e) {
            e(getLocalClassName(), "Could not fetch Graduate", e);
        }
        if (graduate != null) {
            Collection<Course> courses = graduate.getCourses();
            if (courses != null) {
                CoursesListAdapter coursesListAdapter = new CoursesListAdapter(this, R.layout.list_course_item, new ArrayList<Course>(courses));
                ListView listView = (ListView) findViewById(R.id.courses_list_view);
                listView.setAdapter(coursesListAdapter);
                listView.setOnItemClickListener(this);
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Intent intent = new Intent(this, CourseDetailActivity.class);
            Graduate graduate = graduateDao.getGraduate();
            if (graduate != null) {
                Collection<Course> courses = graduate.getCourses();

                Course course = (new ArrayList<Course>(courses)).get(position);
                intent.putExtra(COURSE_ID, course.getId());
            }
            startActivity(intent);
        } catch (Exception e) {
            e(getLocalClassName(), "graduate is null", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this addsc items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_ADD_COURSE, 100, getString(R.string.Add));
        menu.findItem(MENU_ITEM_ADD_COURSE).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_ADD_COURSE:
                startActivity(new Intent(this, CourseDetailActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected ViewGroup getViewGroup() {
        return this.viewGroup;
    }
}
