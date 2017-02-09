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

import edu.wgu.hreid6.wgugo.FormHelper;
import edu.wgu.hreid6.wgugo.R;
import edu.wgu.hreid6.wgugo.data.model.Assessment;

/**
 * Created by hreid on 2/6/17.
 */

public class AssessmentListAdapter extends ArrayAdapter<Assessment> {

    private List<Assessment> assessments;
    private int layout;

    public AssessmentListAdapter(Context context, int resource, List<Assessment> assessments, int layout) {
        super(context, resource, assessments);
        this.assessments = assessments;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
        }

        Assessment assessment = assessments.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.lbl_assessment_item_title);
        title.setText(assessment.getType().toString());

        TextView dueDate = (TextView) convertView.findViewById(R.id.lbl_assessment_item_duedate);
        dueDate.setText(FormHelper.getDisplayDate(assessment.getDueDate()));

        Button button = (Button) convertView.findViewById(R.id.btn_assessment_item_delete);
        if (button != null) {
            button.setTag(assessment.getId());
        }
        return convertView;
    }
}
