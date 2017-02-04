package edu.wgu.hreid6.wgugo.data.dao;

import android.app.Activity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.wgu.hreid6.wgugo.data.PersistenceHelper;
import edu.wgu.hreid6.wgugo.data.model.Term;
import static edu.wgu.hreid6.wgugo.data.model.Term.*;

/**
 * Created by hreid on 2/3/17.
 */
public class TermDao extends AbstractDao<Term> {

    public TermDao(Activity activity) {
        super(activity);
    }

    public Term getById(Integer id) throws SQLException {
        return getQueryBuilder(Term.class).where().eq(colId, id).queryForFirst();
    }

    public List<Term> getAllTerms() throws SQLException {
        QueryBuilder<Term, ?> qb = getQueryBuilder(Term.class);
        List<Term> terms = qb.query();
        return terms;
    }

    public boolean createOrUpdate(Term term) throws SQLException {
        Dao.CreateOrUpdateStatus stat = getDao(Term.class).createOrUpdate(term);
        return stat.getNumLinesChanged() > 0;
    }

    public boolean delete(Term term) throws SQLException {
        return getDao(Term.class).delete(term) > 0;
    }

}
