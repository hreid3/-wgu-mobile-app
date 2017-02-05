package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import static android.util.Log.*;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;

import java.sql.SQLException;

import edu.wgu.hreid6.wgugo.data.model.Graduate;

public class MainActivity extends BaseAndroidActivity {

    private CoordinatorLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mainLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // We need to Get Graduate information, if not available, we need to popup activity to get information

        try {
            Graduate graduate = getGraduate();
            if (graduate == null) {
                // Popup activity to fetch graduate info
            } else {
                // put in summary details.
            }
        } catch (SQLException e) {
            e(getClass().getName(), "Error fetching graduate", e);
            // TODO: popup error message.
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this addsc items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_COURSES_LIST, 500, R.string.menu_item_courses_landing);
        menu.add(0, MENU_ITEM_TERMS_LIST, 600, R.string.menu_item_terms_landing);
        menu.findItem(MENU_ITEM_COURSES_LIST).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.findItem(MENU_ITEM_TERMS_LIST).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_COURSES_LIST:
                startActivity(new Intent(this, CoursesLandingActivity.class));
                return true;
            case MENU_ITEM_TERMS_LIST:
                startActivity(new Intent(this, TermsLandingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected ViewGroup getViewGroup() {
        return this.mainLayout;
    }
}
