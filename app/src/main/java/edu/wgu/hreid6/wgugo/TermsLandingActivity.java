package edu.wgu.hreid6.wgugo;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import edu.wgu.hreid6.wgugo.adapter.TermsListAdapter;
import edu.wgu.hreid6.wgugo.data.model.Graduate;
import edu.wgu.hreid6.wgugo.data.model.Term;

import static android.util.Log.e;

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
            if (terms != null) {
                TermsListAdapter termsListAdapter = new TermsListAdapter(this, R.layout.list_term_item, new ArrayList<Term>(terms));
                ListView listView = (ListView) findViewById(R.id.terms_list_view);
                listView.setAdapter(termsListAdapter);
                listView.setOnItemClickListener(this);
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
