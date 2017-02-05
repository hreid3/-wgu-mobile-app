package edu.wgu.hreid6.wgugo.data.dao;

import android.app.Activity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.Graduate;

import static edu.wgu.hreid6.wgugo.data.model.Term.colId;

/**
 * Created by hreid on 2/3/17.
 */
public class GraduateDao extends AbstractDao<Graduate>{

    private final static int DEFAULT_GRADUATE_ID = 101010; //
    public GraduateDao(Activity activity) {
        super(activity);
    }

    public Graduate getById(Integer id) throws SQLException {
        return getQueryBuilder(Graduate.class).where().eq(colId, id).queryForFirst();
    }

    public List<Graduate> getAllCourses() throws SQLException {
        QueryBuilder<Graduate, ?> qb = getQueryBuilder(Graduate.class);
        List<Graduate> graduates = qb.query();
        return graduates;
    }

    public boolean createOrUpdate(Graduate graduate) throws SQLException {
        graduate.setId(DEFAULT_GRADUATE_ID); // Single user app.
        Dao.CreateOrUpdateStatus stat = getDao(Graduate.class).createOrUpdate(graduate);
        return stat.getNumLinesChanged() > 0;
    }

    public boolean delete(Graduate course) throws SQLException {
        return getDao(Graduate.class).delete(course) > 0;
    }

    public Graduate getFirst() throws SQLException {
        return getQueryBuilder(Graduate.class).queryForFirst();
    }
}
