package edu.wgu.hreid6.wgugo;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import edu.wgu.hreid6.wgugo.adapter.TermsListAdapter;
import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.Graduate;
import edu.wgu.hreid6.wgugo.data.model.Term;

import static android.util.Log.e;
import static android.util.Log.i;

public class TermsLandingActivity  extends BaseAndroidActivity  implements AdapterView.OnItemClickListener  {

    private ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_landing);
        this.viewGroup = (ViewGroup) findViewById(R.id.layout_terms_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Graduate graduate = null;
        setSupportActionBar(toolbar);
        try {
            graduate = getGraduate();
        } catch (SQLException e) {
            e(getLocalClassName(), "Could not fetch Graduate", e);
        }
        if (graduate != null) {
            Collection<Term> terms = graduate.getTerms();
            if (terms != null && terms.size() > 0) {
                TermsListAdapter termsListAdapter = new TermsListAdapter(this, R.layout.list_term_item, new ArrayList<Term>(terms));
                ListView listView = (ListView) findViewById(R.id.terms_list_view);
                listView.setAdapter(termsListAdapter);
                listView.setOnItemClickListener(this);
            } else {
                findViewById(R.id.no_rows).setVisibility(View.VISIBLE);
            }
        } else {
            findViewById(R.id.no_rows).setVisibility(View.VISIBLE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.viewGroup.invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Intent intent = new Intent(this, TermDetailActivity.class);
            Graduate graduate = graduateDao.getGraduate();
            if (graduate != null) {
                Collection<Term> terms = graduate.getTerms();

                Term term = (new ArrayList<Term>(terms)).get(position);
                intent.putExtra(TERM_ID, term.getPid());
            }
            startActivity(intent);
        } catch (Exception e) {
            e(getLocalClassName(), "graduate is null", e);
        }
    }

    public void deleteCourseFromGrid(View v) {
        Object tag = v.getTag();
        if (tag != null) {
            try {
                Integer i = new Integer(tag.toString());
                Term t = termDao.getById(i);
                if (t.getCourses() != null && t.getCourses().size() > 0) {
                    saySomething("You cannot delete a term with assigned courses.  Please remove the courses from the term.");
                } else {
                    if (termDao.delete(t)) {
                        i(getLocalClassName(), "succesfully deleted " + t.getPid());
                        Intent intent = new Intent(this, TermsLandingActivity.class);
                        saySomething("Term successfully deleted.");
                        startActivity(intent);
                    }
                }
            } catch (Exception ex) {
                e(getLocalClassName(), "could not delete cource", ex);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this addsc items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, MENU_ITEM_ADD_TERM, 100, getString(R.string.Add));
        menu.findItem(MENU_ITEM_ADD_TERM).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_ADD_TERM:
                startActivity(new Intent(this, TermDetailActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected ViewGroup getViewGroup() {
        return this.viewGroup;
    }

}
