package edu.wgu.hreid6.wgugo.data.dao;

import android.app.Activity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import edu.wgu.hreid6.wgugo.data.model.Assessment;

import static edu.wgu.hreid6.wgugo.data.model.Course.colId;

/**
 * Created by hreid on 2/3/17.
 */
public class AssessmentDao extends AbstractDao<Assessment>{

    public AssessmentDao(Activity activity) {
        super(activity);
    }

    public Assessment getById(Integer id) throws SQLException {
        return getQueryBuilder(Assessment.class).where().eq(colId, id).queryForFirst();
    }

    public List<Assessment> getAllAssessments() throws SQLException {
        QueryBuilder<Assessment, ?> qb = getQueryBuilder(Assessment.class);
        List<Assessment> assessments = qb.query();
        return assessments;
    }

    public boolean createOrUpdate(Assessment course) throws SQLException {
        Dao.CreateOrUpdateStatus stat = getDao(Assessment.class).createOrUpdate(course);
        return stat.getNumLinesChanged() > 0;
    }

    public boolean delete(Assessment course) throws SQLException {
        return getDao(Assessment.class).delete(course) > 0;
    }
}
