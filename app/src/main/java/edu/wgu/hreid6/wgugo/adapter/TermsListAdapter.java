package edu.wgu.hreid6.wgugo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import edu.wgu.hreid6.wgugo.R;
import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.Term;

/**
 * Created by hreid on 2/6/17.
 */

public class TermsListAdapter extends ArrayAdapter<Term> {

    private List<Term> terms;

    public TermsListAdapter(Context context, int resource, List<Term> terms) {
        super(context, resource, terms);
        this.terms = terms;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_term_item, parent, false);
        }

        Term term = terms.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.lbl_term_item_title);
        title.setText(term.getTitle());

        Button button = (Button)convertView.findViewById(R.id.btn_term_item_delete);
        button.setTag(term.getPid());
        return convertView;
    }
}
