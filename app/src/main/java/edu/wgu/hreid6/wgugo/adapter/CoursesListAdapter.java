package edu.wgu.hreid6.wgugo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.wgu.hreid6.wgugo.R;
import edu.wgu.hreid6.wgugo.data.model.Course;

/**
 * Created by hreid on 2/6/17.
 */

public class CoursesListAdapter extends ArrayAdapter<Course> {

    private List<Course> courses;

    public CoursesListAdapter(Context context, int resource, List<Course> courses) {
        super(context, resource, courses);
        this.courses = courses;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_course_item, parent, false);
        }

        Course course = courses.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.lbl_course_item_title);
        title.setText(course.getTitle());
        return convertView;
    }
}
