package edu.wgu.hreid6.wgugo.data.dao;

import android.app.Activity;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

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

    public Graduate getGraduate() throws SQLException {
        return getQueryBuilder(Graduate.class).where().eq(colId, DEFAULT_GRADUATE_ID).queryForFirst();
    }

    public boolean createOrUpdate(Graduate graduate) throws SQLException {
        graduate.setId(DEFAULT_GRADUATE_ID); // Single user app.
        Dao.CreateOrUpdateStatus stat = getDao(Graduate.class).createOrUpdate(graduate);
        return stat.getNumLinesChanged() > 0;
    }

}
