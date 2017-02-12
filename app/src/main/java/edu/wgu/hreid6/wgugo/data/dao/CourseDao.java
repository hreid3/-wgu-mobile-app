package edu.wgu.hreid6.wgugo.data.dao;

import android.app.Activity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import edu.wgu.hreid6.wgugo.data.model.Assessment;
import edu.wgu.hreid6.wgugo.data.model.Course;

import static edu.wgu.hreid6.wgugo.data.model.Course.colId;
import static edu.wgu.hreid6.wgugo.data.model.Course.colStatus;
import static edu.wgu.hreid6.wgugo.data.model.Course.colTermId;

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

    public List<Course> getCoursesNotInTerm() throws SQLException {
        return getQueryBuilder(Course.class).where().isNull(colTermId).query();
    }

    public boolean createOrUpdate(Course course) throws SQLException {
        Dao.CreateOrUpdateStatus stat = getDao(Course.class).createOrUpdate(course);
        return stat.getNumLinesChanged() > 0;
    }

    public boolean delete(Course course) throws SQLException {
        return getDao(Course.class).delete(course) > 0;
    }

    public List<Course> getActiveCourses() throws SQLException {
        QueryBuilder<Course, ?> qb = getQueryBuilder(Course.class);
        List<Course.STATUS> list = new ArrayList<>();
        list.add(Course.STATUS.PENDING);
        list.add(Course.STATUS.START_APPROVED);
        list.add(Course.STATUS.FAILED);
        return qb.where().in(colStatus, list).query();
    }
}
