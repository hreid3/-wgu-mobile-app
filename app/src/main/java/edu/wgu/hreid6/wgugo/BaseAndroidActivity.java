package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import static android.util.Log.*;
import java.sql.SQLException;

import javax.inject.Inject;

import edu.wgu.hreid6.wgugo.data.dao.CourseDao;
import edu.wgu.hreid6.wgugo.data.dao.GraduateDao;
import edu.wgu.hreid6.wgugo.data.dao.TermDao;
import edu.wgu.hreid6.wgugo.data.model.Graduate;

/**
 * Created by hreid on 2/3/17.
 */

abstract class BaseAndroidActivity extends AppCompatActivity {
    protected static final int MENU_ITEM_ABOUT = 0x1001;
    protected static final int MENU_ITEM_LOGOUT = 0x1020;
    protected static final int MENU_ITEM_PROFILE = 0x1005;

    protected static final int MENU_ITEM_COURSES_LIST = 0x1100;
    protected static final int MENU_ITEM_TERMS_LIST = 0x1200;
    protected static final int MENU_ITEM_ADD_COURSE = 0x2000;
    protected static final int MENU_ITEM_ADD_TERM = 0x3000;

    protected static final int MENU_ITEM_SAVE_COURSE = 0x2010;
    protected static final int MENU_ITEM_SAVE_TERM = 0x3010;

    protected static final int MENU_ITEM_SAVE_PROFILE = 0x4000;

    CourseDao courseDao; // I do not like each instance getting a copy of an dio

    TermDao termDao; // I do not like this

    GraduateDao graduateDao;

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
        menu.add(0, MENU_ITEM_ABOUT, 300, R.string.title_activity_about);
        menu.add(0, MENU_ITEM_PROFILE, 300, R.string.profile);
        menu.add(0, MENU_ITEM_LOGOUT, 400, R.string.logout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case MENU_ITEM_ABOUT:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case MENU_ITEM_PROFILE:
                startActivity(new Intent(this, GraduateFormActivity.class));
                return true;
            case MENU_ITEM_LOGOUT:
                Snackbar.make(getViewGroup(), "You are logged out, bye.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
}
