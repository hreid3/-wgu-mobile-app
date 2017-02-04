package edu.wgu.hreid6.wgugo.data.dao;

import android.app.Activity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Singleton;

import edu.wgu.hreid6.wgugo.data.model.Assessment;
import edu.wgu.hreid6.wgugo.data.model.Course;

import static edu.wgu.hreid6.wgugo.data.model.Term.colId;

/**
 * Created by hreid on 2/3/17.
 */
public class CourseDao extends AbstractDao<Course>{

    public CourseDao(Activity activity) {
        super(activity);
    }

    public Course getById(Integer id) throws SQLException {
        return getQueryBuilder(Course.class).where().eq(colId, id).queryForFirst();
    }

    public List<Course> getAllCourses() throws SQLException {
        QueryBuilder<Course, ?> qb = getQueryBuilder(Course.class);
        List<Course> courses = qb.query();
        return courses;
    }

    public boolean createOrUpdate(Course course) throws SQLException {
        Dao.CreateOrUpdateStatus stat = getDao(Course.class).createOrUpdate(course);
        return stat.getNumLinesChanged() > 0;
    }

    public boolean delete(Course course) throws SQLException {
        return getDao(Course.class).delete(course) > 0;
    }
}
