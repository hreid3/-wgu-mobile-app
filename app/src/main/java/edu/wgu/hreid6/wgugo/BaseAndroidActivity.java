package edu.wgu.hreid6.wgugo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

/**
 * Created by hreid on 2/3/17.
 */

abstract class BaseAndroidActivity extends AppCompatActivity {
    protected static final int MENU_ITEM_ABOUT = 0x1001;
    protected static final int MENU_ITEM_LOGOUT = 0x1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_ABOUT, 300, R.string.title_activity_about);
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
            case MENU_ITEM_LOGOUT:
                Snackbar.make(getViewGroup(), "You are logged out, bye.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    abstract protected ViewGroup getViewGroup();

}
