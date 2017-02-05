package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import static android.util.Log.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.sql.SQLException;
import java.util.List;

import edu.wgu.hreid6.wgugo.data.model.Course;

public class CoursesLandingActivity extends BaseAndroidActivity {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_courses_landing);
        setContentView(R.layout.activity_courses_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            List<Course>  courses = courseDao.getAllCourses();
        } catch (SQLException e) {
            e(getClass().getName(), "Error fetching courses", e);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
